package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float distanceFromPlayer = 35;
	private float angleAroundPlayer = 0;

	// camera zoom
	private final float CAMERA_MAX_ZOOM_IN = 0f;
	private final float CAMERA_MAX_ZOOM_OUT = 40f;
	private boolean allowCameraZoomIn = true;
	private boolean allowCameraZoomOut = true;
	// end

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;

	private Player player;

	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		yaw %= 360;
	}

	public void invertPitch() {
		this.pitch = -pitch;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		// 6 value above means camera max zoom in will be on head
		position.y = player.getPosition().y + verticDistance + 6;
	}

	private float calculateHorizontalDistance() {
		// return (float) (distanceFromPlayer *
		// Math.cos(Math.toRadians(pitch+4)));
		float hD = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch + 6)));
		if (hD < 0)
			hD = 0;
		return hD;
	}

	private float calculateVerticalDistance() {
		// return (float) (distanceFromPlayer *
		// Math.sin(Math.toRadians(pitch+4)));
		float vD = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch + 6)));
		if (vD < 0)
			vD = 0;
		return vD;
	}

	private void calculateZoom() {

		// Find out how much the user moves the mouse wheel
		float zoomLevel = Mouse.getDWheel() * 0.05f;

		if (distanceFromPlayer < CAMERA_MAX_ZOOM_IN) {
			allowCameraZoomIn = false;
			allowCameraZoomOut = true;
		}

		else if (distanceFromPlayer > CAMERA_MAX_ZOOM_OUT) {
			allowCameraZoomIn = true;
			allowCameraZoomOut = false;
		}

		if ((allowCameraZoomIn && zoomLevel > 0) || (allowCameraZoomOut && zoomLevel < 0)) {
			// Zoom OUT when the mouse wheel is moved DOWN
			distanceFromPlayer -= zoomLevel;

			// Zoom IN when the mouse wheel is moved DOWN
			// distanceFromPlayer += zoomLevel;
		}
	}
	/*
	 * private void calculateZoom(){ float zoomLevel = Mouse.getDWheel() *
	 * 0.03f; distanceFromPlayer -= zoomLevel; if(distanceFromPlayer<5){
	 * distanceFromPlayer = 0; // 0 camera from head } }
	 */

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.2f;
			pitch -= pitchChange;
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
			if (pitch < 0) {
				pitch = 0;
			} else if (pitch > 90) {
				pitch = 90;
			}
		}
	}

	private void calculateAngleAroundPlayer() {
		//if (Mouse.isButtonDown(0)) {
			//float angleChange = Mouse.getDX() * 0.3f;
			//angleAroundPlayer -= angleChange;
		//}
	}

}
