package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import engineTester.World;
import entities.MultiPlayer;
import network.packets.Packet;
import network.packets.Packet.PacketTypes;
import network.packets.PacketDisconnect;
import network.packets.PacketLogin;
import network.packets.PacketMove;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private List<MultiPlayer> connectedPlayers = new ArrayList<MultiPlayer>();
	World world;

	public GameServer(World game, int port) {
		// world = game;
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookUpPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new PacketLogin(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((PacketLogin) packet).getUsername()
					+ " has connected...");

			// total waste of time here: 2 weeks...
			// first because of opengl content
			// Secondly because of infinity pos of player -> instead of null
			// in player texture, i used world.stanfordBunny
			MultiPlayer player = new MultiPlayer(null, 75, 5, -75, 0, 100, 0, 0.6f,
					((PacketLogin) packet).getUsername(), address, port);
			this.addConnection(player, (PacketLogin) packet);
			break;
		case DISCONNECT:
			packet = new PacketDisconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((PacketDisconnect) packet).getUsername() + " has left...");
			this.removeConnection((PacketDisconnect) packet);
			break;
		case MOVE:
			packet = new PacketMove(data);
			this.handleMove(((PacketMove) packet));
		}
	}

	public void addConnection(MultiPlayer player, PacketLogin packet) {
		boolean alreadyConnected = false;
		for (MultiPlayer multiPlayer : this.connectedPlayers) {
			if (player.getEntityName().equalsIgnoreCase(multiPlayer.getEntityName())) {
				if (multiPlayer.ipAddress == null) {
					multiPlayer.ipAddress = player.ipAddress;
				}
				if (multiPlayer.port == -1) {
					multiPlayer.port = player.port;
				}
				alreadyConnected = true;
			} else {
				// relay to the current connected player that there is a new
				// player
				sendData(packet.getData(), multiPlayer.ipAddress, multiPlayer.port);

				// relay to the new player that the currently connect player
				// exists

				PacketLogin packetCurrentPlayer = new PacketLogin(multiPlayer.getEntityName(),
						multiPlayer.getPositionX(), multiPlayer.getPositionY(), multiPlayer.getPositionZ(),
						multiPlayer.getScale());

				sendData(packetCurrentPlayer.getData(), player.ipAddress, player.port);
			}
		}
		if (!alreadyConnected) {
			this.connectedPlayers.add(player);
		}
	}

	public void removeConnection(PacketDisconnect packet) {
		this.connectedPlayers.remove(getMultiPlayerIndex(packet.getUsername()));
		packet.writeData(this);
	}

	public MultiPlayer getMultiPlayer(String username) {
		for (MultiPlayer multiPlayer : this.connectedPlayers) {
			if (multiPlayer.getEntityName().equals(username)) {
				return multiPlayer;
			}
		}
		return null;
	}

	public int getMultiPlayerIndex(String username) {
		int index = 0;
		for (MultiPlayer multiPlayer : this.connectedPlayers) {
			if (multiPlayer.getEntityName().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {

		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (MultiPlayer multiPlayer : connectedPlayers) {
			sendData(data, multiPlayer.ipAddress, multiPlayer.port);
		}
	}

	private void handleMove(PacketMove packet) {
		if (getMultiPlayer(packet.getUsername()) != null) {
			// int index = getMultiPlayerIndex(packet.getUsername());
			// MultiPlayer player = this.connectedPlayers.get(index);
			// player.setPositionX(packet.getX());
			// player.setPositionY(packet.getY());
			// player.setPositionZ(packet.getZ());
			// player.setRotY(packet.getAngle());
			packet.writeData(this);
		}
	}

}
