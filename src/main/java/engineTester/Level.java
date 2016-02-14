package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.MultiPlayer;

/*
 * Class made for list of entites, adding/deleting/getting
 */
public class Level {

	private List<Entity> entities = new ArrayList<Entity>();

	public synchronized List<Entity> getEntities() {
		return this.entities;
	}

	public synchronized void addEntity(Entity entity) {
		// this.getEntities().add(entity);
		entities.add(entity);
		if (entity.getEntityName() != null)
			System.out.println(entity.getEntityName() + "   " + entity.getPosition().x + "   " + entity.getPosition().y
					+ "   " + entity.getPosition().z);
	}

	public synchronized void movePlayer(String username, float dx, float dy, float dz, float angle) {
		int index = getMultiPlayerIndex(username);
		// MultiPlayer player = (MultiPlayer) this.getEntities().get(index);
		MultiPlayer player = (MultiPlayer) this.entities.get(index);
		// player.setPositionX(dx);
		// player.setPositionY(dy);
		// player.setPositionZ(dz);
		player.setPosition(new Vector3f(dx, dy, dz));
		player.setRotY(angle);
	}

	public synchronized void removeMultiPlayer(String username) {
		int index = 0;
		// for (Entity entity : getEntities()) {
		for (Entity entity : entities) {
			if (entity instanceof MultiPlayer && ((MultiPlayer) entity).getEntityName().equals(username)) {
				break;
			}
			index++;
		}
		// this.getEntities().remove(index);
		entities.remove(index);
	}

	private int getMultiPlayerIndex(String username) {
		int index = 0;
		// for (Entity entity : getEntities()) {
		for (Entity entity : entities) {
			if (entity instanceof MultiPlayer && ((MultiPlayer) entity).getEntityName().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

}
