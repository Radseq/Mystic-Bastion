package entities;

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

	public Player(float positionX, float positionY, float positionZ, float rotX, float rotY, float rotZ, float scale,
			String entityName) {
		super(positionX, positionY, positionZ, rotX, rotY, rotZ, scale, entityName);
		// soundManager.load(soundName, false, 0.6f); // load a sound (true =
		// looping,false = play once, and volume from 0 to 1)
	}

	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
}