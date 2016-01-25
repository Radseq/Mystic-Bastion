package network.packets;

import network.GameClient;

public class PacketLogin extends Packet {

	private String userName;
	private float posX, posY, posZ;
	private float scale;

	public PacketLogin(byte[] data) {
		super(00);
		String[] dataArray = readData(data).split("@");
		this.userName = dataArray[0];
		this.posX = Float.parseFloat(dataArray[1]);
		this.posY = Float.parseFloat(dataArray[2]);
		this.posZ = Float.parseFloat(dataArray[3]);
		this.scale = Float.parseFloat(dataArray[4]);
	}

	public PacketLogin(String username, float x, float y, float z, float scale) {
		super(00);
		this.userName = username;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.scale = scale;
	}

	@Override
	public byte[] getData() {
		return ("00" + this.userName + "@" + this.posX + "@" + this.posY + "@" + this.posZ + "@" + this.scale)
				.getBytes();
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public String getUsername() {
		return userName;
	}

	public float getX() {
		return posX;
	}

	public float getY() {
		return posY;
	}

	public float getZ() {
		return posZ;
	}

	public float getScale() {
		return scale;
	}

}
