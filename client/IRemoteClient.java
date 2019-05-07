package client.agent;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import entity.User;

public interface IRemoteClient extends Remote {
	public void refreshGrid(String[][] grid) throws RemoteException;
	public void refreshScoreBoard(Map<User, Integer> gamers) throws RemoteException;
	public void startNewTurn() throws AccessException, RemoteException, NotBoundException;
	public void endGame(User user, int score) throws RemoteException;
	public void showUserList(Set<User> users) throws AccessException, RemoteException, NotBoundException;
	public boolean vote(int x1, int y1, int x2, int y2) throws RemoteException;
	public boolean accept(User gamer) throws RemoteException;
	public void refreshCells(int x, int y, String c) throws RemoteException;
}
