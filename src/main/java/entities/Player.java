package entities;

import org.lwjgl.input.Keyboard;

import engineTester.World;
import models.TexturedModel;
import network.packets.PacketMove;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160;
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 18;

	private float currentSpeed = 0;
	public float currentTurnSpeed = 0;

	// private int count;

	public float getCurrentTurnSpeed() {
		return currentTurnSpeed;
	}

	// SoundManager soundManager = new SoundManager();
	String soundName = "Footsteps";

	private float upwardsSpeed = 0;

	private boolean isInAir = false;

	public Player(TexturedModel model, float positionX, float positionY, float positionZ, float rotX, float rotY,
			float rotZ, float scale, String entityName) {
		super(model, positionX, positionY, positionZ, rotX, rotY, rotZ, scale, entityName);
		// soundManager.load(soundName, false, 0.6f); // load a sound (true =
		// looping,false = play once, and volume from 0 to 1)
	}

	public void move(Terrain terrain) {
		checkInputs();
		float b = currentTurnSpeed * DisplayManager.getFrameTimeSeconds();
		super.increaseRotation(0, b, 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		float a = upwardsSpeed * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, a, 0);
		float terrainHeight = terrain.getHeightOfTerrain(getPositionX(), getPositionZ());
		if (super.getPositionY() < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.setPositionY(terrainHeight);
		}
		float y = super.getPositionY();
		if (dx != 0 || y != 0 || dz != 0 || b != 0) {
			PacketMove packet = new PacketMove(this.getEntityName(), this.getPositionX(), this.getPositionY(),
					this.getPositionZ(), this.getRotY());
			packet.writeData(World.world.socketClient);
		}

		// float a = soundManager.getLength();
		// if (distance == 0) {
		// soundManager.stop();
		// } else {
		// new java.util.Timer().schedule(new java.util.TimerTask() {
		// @Override
		// public void run() {
		// count = 0;
		// }
		// }, (long) a);
		// if (count == 0) {
		// soundManager.play(soundName);
		// }
		// count = 1;
		// }

	}

	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	public void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}
}