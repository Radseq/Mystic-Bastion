package network.packets;

import network.GameServer;

public abstract class Packet {

	public static enum PacketTypes {
		INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02);

		private int packetId;

		public int getId() {
			return packetId;
		}

		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}
	}

	public byte packetId;

	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}

	public static PacketTypes lookUpPacket(int id) {
		for (PacketTypes packetTypes : PacketTypes.values()) {
			if (packetTypes.getId() == id) {
				return packetTypes;
			}
		}
		return PacketTypes.INVALID;
	}

	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2);
	}

	public static PacketTypes lookUpPacket(String packetId) {
		try {
			return lookUpPacket(Integer.parseInt(packetId));
		} catch (NumberFormatException e) {
			return PacketTypes.INVALID;
		}
	}

	public abstract void writeData(GameServer server);

	public abstract byte[] getData();
}
