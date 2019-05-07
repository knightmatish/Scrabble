package client.agent;

import java.awt.EventQueue;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import entity.GameState;
import entity.User;
import server.IRemoteServer;

@SuppressWarnings("serial")
public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {
	private Registry registry;
	private User user;
	private MemberGUI memberGUI;
	private GridGUI gridGUI;

	public RemoteClient(Registry registry, User user) throws RemoteException {
		this.registry = registry;
		this.user = user;
	}

	public RemoteClient(MemberGUI memberGUI, GridGUI gridGUI) throws RemoteException {
		this.memberGUI = memberGUI;
		this.gridGUI = gridGUI;
	}

	public static void main(String[] args) {
		try {
			GridGUI grid = new GridGUI();
			MemberGUI member = new MemberGUI();
			RemoteClient client = new RemoteClient(member, grid);
			grid.setRemoteClient(client);
			member.setClient(client);
			member.getFrame().setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// GUI 1. initialize client with server ip, server port, client ip, client port,
	// user name

	public void initialize(String serverIp, int serverPort, String clientIp, int clientPort, String userName)
			throws RemoteException, AlreadyBoundException {
		registry = LocateRegistry.getRegistry(serverIp, serverPort);
		user = new User(clientIp, userName, clientPort);
		LocateRegistry.createRegistry(clientPort);
		LocateRegistry.getRegistry(clientIp, clientPort).bind(userName, this);
	}

	// GUI 2. login & logout fuction/button
	public void login() {
		try {
			getServer().login(user);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Server is Down");
		}
	}

	public void logoff() {
		try {
			getServer().logoff(user);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Server is Down");
		}
	}

	// GUI 3. show active user list, with invitation function to start a game, like
	// check box
	@Override
	public void showUserList(Set<User> users) {
		memberGUI.showActiveUsers(users, user);
	}

	public GameState startGame(List<User> gamers) {
		try {
			return getServer().startGame(gamers);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Server is Down");
		}
		return GameState.SERVERNOTALIVE;
	}

	public boolean accept(User gamer) throws RemoteException {
		if (JOptionPane.showConfirmDialog(memberGUI.getFrame(),
				"You have received a game invitation from " + gamer.getName() + " . Do you want to accept it?",
				"INVITATION", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	// GUI 4. refresh the whole grids
	@Override
	public void refreshGrid(String[][] grid) {
		memberGUI.getFrame().setVisible(false);
		gridGUI.refresh(grid);
	}
	
	public void refreshCells(int x, int y, String c) {
		gridGUI.refreshCells(x,y,c);
	}
	
	@Override
	public void refreshScoreBoard(Map<User, Integer> gamers) {
		gridGUI.refreshScoreBoard(gamers);
	}

	// GUI 5. Client start to place a char with timeout
	@Override
	public void startNewTurn() {
		gridGUI.startNewTurn();
	}
	
	public void placeChar(int i, int j, String c){
		try {
			getServer().placeChar(i, j, c, user);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Server is Down");
		}
	}

	public void pass()  {
		try {
			getServer().pass(user);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Server is Down");
		}
	}
	
	public void passClaim() {
		try {
			getServer().passClaim(user);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Server is Down");
		}
	}

	// GUI 6. Client ends game show score board
	@Override
	public void endGame(User user, int score) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				gridGUI.disableControls();
				gridGUI.getFrame().setVisible(false);
				memberGUI.getFrame().setVisible(true);
				if (user != null) {
					JOptionPane.showMessageDialog(gridGUI.getFrame(),
							"The winner is " + user.getName() + " with " + score + " points");
				} else {
					JOptionPane.showMessageDialog(null, "There is no winner in this game");
				}

			}
		});

	}

	// GUI 7. client claim a word between 2 grids with timeout
	public void claim(int x1, int y1, int x2, int y2)  {
		try {
			getServer().vote(x1, y1, x2, y2, user);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Server is Down");
		}

	}

	// GUI 8. show the claimed word and vote
	@Override
	public boolean vote(int x1, int y1, int x2, int y2) {
		return gridGUI.vote(x1, y1, x2, y2);
	}

	private IRemoteServer getServer() throws AccessException, RemoteException, NotBoundException {
		return (IRemoteServer) registry.lookup("server");
	}

	public MemberGUI getMemberGUI() {
		return memberGUI;
	}

	public void setMemberGUI(MemberGUI memberGUI) {
		this.memberGUI = memberGUI;
	}

	public GridGUI getGridGUI() {
		return gridGUI;
	}

	public void setGridGUI(GridGUI gridGUI) {
		this.gridGUI = gridGUI;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
