package server;

import static entity.GameState.GAMESTARTED;
import static entity.GameState.INVITATIONFAILED;
import static entity.GameState.INVITATIONSUCCESS;
import static entity.GameState.PLAYERDISCONNECTED;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.swing.Timer;

import client.agent.IRemoteClient;
import entity.GameState;
import entity.User;

@SuppressWarnings("serial")
public class RemoteServer extends UnicastRemoteObject implements IRemoteServer {
	private String[][] grid;
	private List<User> gamers;
	private Map<User, Integer> users;
	private boolean startFlag;
	private int passCount;
	private Timer timer;
	private long currentTime;
	private static final int TIMEOUT = 60;
	
	public static void main(String[] args) {
		
		try {
			String ip = args[0];
			int port = Integer.parseInt(args[1]);
			IRemoteServer server = new RemoteServer();
			LocateRegistry.createRegistry(port);
			Registry registry = LocateRegistry.getRegistry(ip, port);
			registry.rebind("server", server);	
			System.out.println("Server start at ip [ "+ ip + " ] on port [ " + port + " ]" );
		} catch (IOException | SQLException | NumberFormatException | NullPointerException e ) {
			e.printStackTrace();
		}
		
	}

	public RemoteServer() throws IOException, SQLException {
		grid = new String[20][20];
		gamers = new CopyOnWriteArrayList<User>();
		users = new ConcurrentHashMap<User, Integer>();
		startFlag = false;
		passCount = 0;
		currentTime = System.currentTimeMillis();
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				long delay = (System.currentTimeMillis() - currentTime) / 1000;
				System.out.println("Heart beat: " + delay);
				if (delay > TIMEOUT) {
					gracefulShutDown();
				}
			}
		});

	}

	public void login(User user) throws RemoteException {
		System.out.println("User " + user.toString() + " logs in");
		users.put(user, 0);
		showActiveUsers();
	}

	public void logoff(User user) {
		System.out.println("User " + user.toString() + " logs off");
		if (startFlag == true && gamers.contains(user)) {
			gracefulShutDown();
		}
		users.remove(user);
		showActiveUsers();

	}

	public synchronized GameState startGame(List<User> newGamers) {
		if (startFlag) {
			return GAMESTARTED;
		}

		final User inviter = newGamers.get(0);

		int accptedGamers = newGamers.parallelStream().mapToInt(newGamer -> {
			try {
				if (newGamer.equals(inviter) || getRemoteClient(newGamer).accept(inviter)) {
					return 1;
				}
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
			return 0;
		}).sum();

		if (accptedGamers < newGamers.size()) {
			showActiveUsers();
			return INVITATIONFAILED;
		}

		grid = new String[20][20];
		gamers = new CopyOnWriteArrayList<User>();
		startFlag = true;
		passCount = 0;

		int connectedGammers = newGamers.parallelStream().mapToInt(newGamer -> {
			try {
				users.put(newGamer, 0);
				gamers.add(newGamer);
				getRemoteClient(newGamer).refreshGrid(grid);
				return 1;
			} catch (RemoteException | NotBoundException e) {
				System.out.println("New gamer: " + newGamer.toString() + " is disconnected\n");
				e.printStackTrace();
				return 0;
			}
		}).sum();

		try {
			if (connectedGammers == newGamers.size()) {
				refreshScoreBoard();
				startNewTurn(newGamers.get(0));
				return INVITATIONSUCCESS;
			}
		} catch (NotBoundException | RemoteException e) {
			System.out.println("The first gamer: " + newGamers.get(0).toString() + " is disconnected\n");
			e.printStackTrace();
		}

		gracefulShutDown();
		return PLAYERDISCONNECTED;
	}

	
	public boolean placeChar(int x, int y, String c, User user) {
		timer.stop();
		grid[x][y] = c;

		int connectedGamers = gamers.parallelStream().mapToInt(gamer -> {
			try {
				getRemoteClient(gamer).refreshCells(x,y, c);
				return 1;
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected\n");
				e.printStackTrace();
				return 0;
			}
		}).sum();
		passCount = 0;

		if (connectedGamers == gamers.size()) {
			currentTime = System.currentTimeMillis();
			timer.start();
			return true;
		}

		gracefulShutDown();
		return false;
	}

	public boolean vote(int x1, int y1, int x2, int y2, User user) {
		timer.stop();
		final User gamer = user;
		int count = gamers.parallelStream().mapToInt(voter -> {
			try {
				if (voter.equals(gamer) || getRemoteClient(voter).vote(x1, y1, x2, y2)) {
					return 1;
				} else {
					return 0;
				}
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + voter.toString() + " is disconnected\n during voting");
				e.printStackTrace();
			}
			return -1 - gamers.size();
		}).sum();

		if (count == gamers.size()) {
			int score = (x1 == x2) ? 1 + Math.abs(y1 - y2) : 1 + Math.abs(x1 - x2);
			users.put(user, users.get(user) + score);
			System.out.println("User " + user.toString() + " gets socre: " + score);
			refreshScoreBoard();
		}

		if (count >= 0) {
			return true;
		}

		gracefulShutDown();
		return false;

	}

	public boolean pass(User user) {
		timer.stop();
		System.out.println("User: " + user.toString() + " passes");
		passCount++;
		try {
			if (passCount < gamers.size()) {
				int index = gamers.indexOf(user);
				user = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
				startNewTurn(user);
				return true;
			}
		} catch (NotBoundException | RemoteException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected\n");
			e.printStackTrace();
		}
		gracefulShutDown();
		return false;
	}

	public boolean passClaim(User user) {
		timer.stop();
		System.out.println("User: " + user.toString() + " passes claiming word");
		try {
			int index = gamers.indexOf(user);
			user = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
			startNewTurn(user);
			return true;
		} catch (NotBoundException | RemoteException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected\n");
			e.printStackTrace();
		}
		gracefulShutDown();
		return false;
	}

	private IRemoteClient getRemoteClient(User user) throws RemoteException, NotBoundException {
		return (IRemoteClient) LocateRegistry.getRegistry(user.getIp(), user.getPort()).lookup(user.getName());
	}

	private void startNewTurn(User user) throws AccessException, RemoteException, NotBoundException {
		getRemoteClient(user).startNewTurn();
		currentTime = System.currentTimeMillis();
		timer.start();
	}

	private void showActiveUsers() {
		Set<User> disconnectedUsers = users.keySet().parallelStream().map(user -> {
			if (startFlag == false || !gamers.contains(user)) {
				try {
					System.out.println("Checking User :" + user.toString());
					getRemoteClient(user).showUserList(users.keySet());
				} catch (RemoteException | NotBoundException e) {
					System.out.println("User: " + user.toString() + " is disconnected\n");
					e.printStackTrace();
					return user;
				}
			}
			return null;
		}).filter(i -> i != null).collect(Collectors.toSet());

		if (disconnectedUsers.size() > 0) {
			for (User user : disconnectedUsers) {
				users.remove(user);
			}
			showActiveUsers();
		}
	}

	private void refreshScoreBoard() {
		Map<User, Integer> scores = new ConcurrentHashMap<User, Integer>();
		for (User gamer : gamers) {
			scores.put(gamer, users.get(gamer));
		}
		gamers.parallelStream().forEach(gamer -> {
			try {
				getRemoteClient(gamer).refreshScoreBoard(scores);
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		});
	}

	private void gracefulShutDown() {
		timer.stop();
		User user = null;
		int score = 0;
		for (User gamer : gamers) {
			if (users.get(gamer) > score) {
				user = gamer;
				score = users.get(gamer);
			}
		}
		final User winner = user;
		final int hightestScore = score;

		System.out.println("start graceful shutdown");
		gamers.parallelStream().forEach(gamer -> {
			try {
				getRemoteClient(gamer).endGame(winner, hightestScore);
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected\n");
				users.remove(gamer);
				e.printStackTrace();
			}
		});
		gamers.clear();
		startFlag = false;
		showActiveUsers();
	}

}
