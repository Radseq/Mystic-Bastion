package network.packets;

import network.GameClient;

public class PacketDisconnect extends Packet {

	private String userName;

	public PacketDisconnect(byte[] data) {
		super(01);
		this.userName = readData(data);
	}

	public PacketDisconnect(String userName) {
		super(01);
		this.userName = userName;
	}

	@Override
	public byte[] getData() {
		return ("01" + this.userName).getBytes();
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public String getUsername() {
		return userName;
	}

}
