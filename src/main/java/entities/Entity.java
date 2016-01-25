package entities;

public class Entity {

	// private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;

	private String entityName;

	private float positionX;
	private float positionY;
	private float positionZ;

	// private List<Entity> entities = new ArrayList<Entity>();

	public Entity(float positionX, float positionY, float positionZ, float rotX, float rotY, float rotZ, float scale) {

		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		// this.position = new Vector3f(positionX, positionY, positionZ);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public Entity(float positionX, float positionY, float positionZ, float rotX, float rotY, float rotZ, float scale,
			String entityName) {

		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		// this.position = new Vector3f(positionX, positionY, positionZ);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.entityName = entityName;
	}

	public Entity(int index, float positionX, float positionY, float positionZ, float rotX, float rotY, float rotZ,
			float scale) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;

		// this.position = new Vector3f(positionX, positionY, positionZ);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.positionX += dx;
		this.positionY += dy;
		this.positionZ += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getEntityName() {
		return entityName;
	}

	public float getPositionX() {
		return positionX;
	}

	public void setPositionX(float positionX) {
		this.positionX = positionX;
	}

	public float getPositionY() {
		return positionY;
	}

	public void setPositionY(float positionY) {
		this.positionY = positionY;
	}

	public float getPositionZ() {
		return positionZ;
	}

	public void setPositionZ(float positionZ) {
		this.positionZ = positionZ;
	}

}