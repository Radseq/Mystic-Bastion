package water;

public class WaterTile {

	private float tileSize = 60;

	private float height;
	private float x, z;

	public WaterTile(float centerX, float centerZ, float height, int tileSize) {
		this.x = centerX;
		this.z = centerZ;
		this.height = height;
		this.tileSize = tileSize;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public float getTileSize() {
		return tileSize;
	}
}
