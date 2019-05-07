package server;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

public class ServerAgent {
	
	public static void main(String[] args) throws IOException, SQLException {
		IRemoteServer server = new RemoteServer();
		LocateRegistry.createRegistry(19140);
		Registry registry = LocateRegistry.getRegistry("localhost", 19140);
		registry.rebind("server", server);		
	}

}
