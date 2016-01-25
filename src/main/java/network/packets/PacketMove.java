package network.packets;

import network.GameServer;

public class PacketMove extends Packet {

	private String userName;
	private float posX, posY, posZ, angle;

	public PacketMove(byte[] data) {
		super(02);
		String[] dataArray = readData(data).split("@");
		this.userName = dataArray[0];
		this.posX = Float.parseFloat(dataArray[1]);
		this.posY = Float.parseFloat(dataArray[2]);
		this.posZ = Float.parseFloat(dataArray[3]);
		this.angle = Float.parseFloat(dataArray[4]);
	}

	public PacketMove(String username, float x, float y, float z, float angle) {
		super(02);
		this.userName = username;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.angle = angle;
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("02" + this.userName + "@" + this.posX + "@" + this.posY + "@" + this.posZ + "@" + this.angle)
				.getBytes();
	}

	public String getUsername() {
		return userName;
	}

	public float getX() {
		return this.posX;
	}

	public float getY() {
		return this.posY;
	}

	public float getZ() {
		return this.posZ;
	}

	public float getAngle() {
		return this.angle;
	}
}
