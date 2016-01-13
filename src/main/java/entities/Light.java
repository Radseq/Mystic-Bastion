package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {

	private Vector3f position;
	private Vector3f colour;
	private Vector3f attenuation = new Vector3f(1, 0, 0);

	private float lightValueChange = 0.0019f;

	public void increaseColor(Vector3f colorIncrease) {
		colorIncrease = new Vector3f(getColour().x + lightValueChange, getColour().y + lightValueChange,
				getColour().z + lightValueChange);
		setColour(new Vector3f(colorIncrease.x, colorIncrease.y, colorIncrease.z));
	}

	public void decreaseColor(Vector3f colorDecrease) {
		colorDecrease = new Vector3f(getColour().x - lightValueChange, getColour().y - lightValueChange,
				getColour().z - lightValueChange);
		setColour(new Vector3f(colorDecrease.x, colorDecrease.y, colorDecrease.z));
	}

	public Light(Vector3f position, Vector3f colour) {
		this.position = position;
		this.colour = colour;
	}

	public Light(Vector3f position, Vector3f colour, Vector3f attenuation) {
		this.position = position;
		this.colour = colour;
		this.attenuation = attenuation;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColour() {
		return colour;
	}

	public void setColour(Vector3f colour) {
		this.colour = colour;
	}

}
