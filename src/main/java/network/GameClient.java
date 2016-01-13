package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import engineTester.World;
import entities.MultiPlayer;
import network.packets.Packet;
import network.packets.Packet.PacketTypes;
import network.packets.PacketDisconnect;
import network.packets.PacketLogin;
import network.packets.PacketMove;

public class GameClient extends Thread {

	private InetAddress ipAddress;
	private DatagramSocket socket;
	private World world;
	private int port;

	public GameClient(World world, String ipAddress, int port) {
		this.world = world;
		this.port = port;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
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
			handleLogin((PacketLogin) packet, address, port);
			break;
		case DISCONNECT:
			packet = new PacketDisconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((PacketDisconnect) packet).getUsername() + " has left the world...");
			world.level.removeMultiPlayer(((PacketDisconnect) packet).getUsername());
			break;
		case MOVE:
			packet = new PacketMove(data);
			handleMove((PacketMove) packet);
		}
	}

	private void handleLogin(PacketLogin packet, InetAddress address, int port) {
		System.out.println(
				"[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername() + " has joined the game...");

		// TexturedModel stanfordBunny = world.stanfordBunny;
		MultiPlayer player = new MultiPlayer(world.stanfordBunny, packet.getX(), packet.getY(), packet.getZ(), 0, 0, 0,
				packet.getScale(), packet.getUsername(), address, port);
		world.level.addEntity(player);
	}

	private void handleMove(PacketMove packet) {
		this.world.level.movePlayer(packet.getUsername(), packet.getX(), packet.getY(), packet.getZ(),
				packet.getAngle());
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
