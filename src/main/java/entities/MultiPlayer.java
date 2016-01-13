package entities;

import java.net.InetAddress;

import models.TexturedModel;

public class MultiPlayer extends Player {

	public InetAddress ipAddress;
	public int port;

	// private String userName;

	public MultiPlayer(TexturedModel model, float positionX, float positionY, float positionZ, float rotX, float rotY,
			float rotZ, float scale, String userName, InetAddress ipAddress, int port) {
		super(model, positionX, positionY, positionZ, rotX, rotY, rotZ, scale, userName);

		// this.userName = userName;
		this.ipAddress = ipAddress;
		this.port = port;
	}

}
