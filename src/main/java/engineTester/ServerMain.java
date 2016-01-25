package engineTester;

import java.net.InetAddress;
import java.net.UnknownHostException;

import network.GameServer;

public class ServerMain {

	private final static int SERVER_PORT = 2323;

	public static GameServer socketServer;

	public static void main(String[] args) {
		System.out.println("Will host on " + getIp() + ":" + SERVER_PORT);
		socketServer = new GameServer(SERVER_PORT);
		socketServer.start();
	}

	public static String getIp() {

		String myIp = "";
		InetAddress ip;

		try {
			ip = InetAddress.getLocalHost();
			myIp = ip.getHostAddress(); // This method returns the IP.
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return myIp;
	}
}