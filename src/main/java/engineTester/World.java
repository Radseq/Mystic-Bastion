package engineTester;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.MultiPlayer;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import network.GameClient;
import network.packets.PacketDisconnect;
import network.packets.PacketLogin;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class World implements Runnable {

	public static World world;

	private Thread thread;

	public static boolean running = false;

	public boolean isHost;

	public Level level = new Level();

	private final int SERVER_PORT = 2323;
	public GameClient socketClient;

	public TexturedModel stanfordBunny;

	private Player player;

	public synchronized void start() {
		running = true;

		thread = new Thread(this);
		thread.start();

		System.out.println("Will join to " + getIp() + ":" + SERVER_PORT);
		socketClient = new GameClient(this, "localhost", SERVER_PORT);
		socketClient.start();
	}

	public synchronized void stop() {
		running = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		world = this;
		Random random = new Random((new Date()).getTime() % 5666778);

		DisplayManager.createDisplay();
		DisplayManager.ShowFPS(false);
		DisplayManager.setFpsCountRefreshRate(500);

		Loader loader = new Loader();
		TextMaster.init(loader);
		MasterRenderer renderer = new MasterRenderer(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());

		// public static World world;

		// *********TERRAIN TEXTURE STUFF**********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		TexturedModel rocks = new TexturedModel(OBJFileLoader.loadOBJ("rocks", loader),
				new ModelTexture(loader.loadTexture("rocks")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));

		TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader), fernTextureAtlas);

		TexturedModel bobble = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));

		List<Terrain> terrains = new ArrayList<Terrain>();
		// true false at end of terrain constructor means generate random height
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap", false);
		Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap", true);

		terrains.add(terrain);
		// terrains.add(terrain2);
		// *****************************************

		// ******************NORMAL MAP MODELS************************

		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));

		List<Entity> normalMapEntities = new ArrayList<Entity>();

		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));

		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));

		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));

		Entity en1 = new Entity(barrelModel, 75, 10, -75, 0, 0, 0, 1f);
		Entity en2 = new Entity(boulderModel, 85, 10, -75, 0, 0, 0, 1f);
		Entity en3 = new Entity(crateModel, 65, 10, -75, 0, 0, 0, 0.04f);

		// add lamps
		// Entity lamp1 = new Entity(lamp, 185, -4.7f, -293, 0, 0, 0, 1);
		// Entity lamp2 = new Entity(lamp, 370, 4.2f, -300, 0, 0, 0, 1);
		// Entity lamp3 = new Entity(lamp, 293, -6.8f, -305, 0, 0, 0, 1);

		lamp.getTexture().setUseFakeLighting(true);

		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));

		// ****************** Player ************************
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel,
				new ModelTexture(loader.loadTexture("playerTexture")));
		player = new MultiPlayer(stanfordBunny, 110, 5, -90, 0, 100, 0, 0.6f, "bob" + random.nextInt(400), null, -1);
		level.addEntity(player);

		PacketLogin loginPacket = new PacketLogin(player.getEntityName(), player.getPositionX(), player.getPositionY(),
				player.getPositionZ(), player.getScale());

		loginPacket.writeData(socketClient);
		// *****************************************

		FontType font = new FontType(loader.loadTexture("candara"), new File("src/main/resources/candara.fnt"));
		GUIText text = new GUIText(player.getEntityName(), 2f, font, new Vector2f(0f, 0.53f), 1f, true);

		Camera camera = new Camera(player);
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		// **********Water Renderer Set-up************************

		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		// 7x7 squares
		for (int i = 1; i < 7; i++) {
			for (int j = 1; j < 7; j++) {
				waters.add(new WaterTile(i * 100, -j * 100, -1));
			}
		}

		// **********Water Renderer END************************

		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
		ParticleSystem system = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 1);

		text.setColour(1, 0, 0);
		text.setDistanceFieldWidth(0.5f);
		text.setDistanceFieldEdge(0.2f);
		text.setBorderWidth(0.4f);
		text.setBorderEdge(0.2f);
		text.setOffset(0.006f, 0.006f);
		text.setOutlineColour(0, 1, 1);

		// const float width = 0.5;
		// const float edge = 0.1;
		// const float borderWidth = 0.4;
		// const float borderEdge = 0.1;
		// const vec2 offset = vec2(0.006, 0.006);
		// const vec3 outlineColour = vec3(1.0, 0.0, 0.0);

		bobble.getTexture().setHasTransparency(true);

		fern.getTexture().setHasTransparency(true);

		fernTextureAtlas.setNumberOfRows(2);

		/*
		 * Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap,
		 * "heightmap"); List<Terrain> terrains = new ArrayList<Terrain>();
		 * terrains.add(terrain);
		 */

		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);

		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);

		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);

		// ************ENTITIES*******************

		normalMapEntities.add(en1);
		normalMapEntities.add(en2);
		normalMapEntities.add(en3);

		// Random random = new Random(5666778);
		for (int i = 0; i < 320; i++) {
			if (i % 3 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;

				float y = terrain.getHeightOfTerrain(x, z);
				if (y > 0) {
					level.addEntity(new Entity(fern, 3, x, y, z, 0, random.nextFloat() * 360, 0, 0.9f));
				}
			}

			if (i % 10 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;

				float y = terrain.getHeightOfTerrain(x, z);
				if (y > 0) {
					level.addEntity(new Entity(lamp, x, y, z, 0, 0, 0, 1));
					// lights.add(new Light(new Vector3f(x, y + 10, z),
					// new Vector3f(random.nextInt(), random.nextInt(),
					// random.nextInt()),
					// new Vector3f(1, 0.01f, 0.02f)));
				}
			}

			if (i % 1 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				if ((x > 50 && x < 100) || (z < -50 && z > -100)) {

				} else {
					float y = terrain.getHeightOfTerrain(x, z);
					if (y > 0) {
						level.addEntity(new Entity(bobble, 1, x, y, z, 0, random.nextFloat() * 360, 0,
								random.nextFloat() * 0.6f + 0.8f));
					}
				}
			}
		}
		for (int i = 0; i < 30; i++) {
			float x = 400 + random.nextFloat() * 200;
			float z = -400 + random.nextFloat() * 200;

			float y = terrain.getHeightOfTerrain(x, z);
			normalMapEntities
					.add(new Entity(boulderModel, x, y, z, random.nextFloat() * 360, 0, 0, 0.5f + random.nextFloat()));
		}

		Entity rock = new Entity(rocks, 75, 4.6f, -75, 0, 0, 0, 75);
		level.addEntity(rock);

		// *******************OTHER SETUP***************

		lights.add(sun);
		// add light to lamps
		// lights.add(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0,
		// 0), new Vector3f(1, 0.01f, 0.02f)));
		// lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2,
		// 2), new Vector3f(1, 0.01f, 0.02f)));
		// lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2,
		// 0), new Vector3f(1, 0.01f, 0.02f)));

		// waters.add(water);

		// **********Particle Renderer Set-up************************
		system.randomizeRotation();
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(0.8f);

		// ****************Game Loop Below*********************

		while (!Display.isCloseRequested()) { // && ended
			if (!running)
				break;
			// **********Change color of fog depend of
			// day/might************************

			if (SkyboxRenderer.getTime() >= 0 && SkyboxRenderer.getTime() < 5000) {
				sun.setColour(new Vector3f(0.3f, 0.3f, 0.3f));
				MasterRenderer.RED = 0.01f;
				MasterRenderer.GREEN = 0.01f;
				MasterRenderer.BLUE = 0.01f;
			} else if (SkyboxRenderer.getTime() >= 5000 && SkyboxRenderer.getTime() < 8000) {
				sun.increaseColor(new Vector3f(0.0001f, 0.0001f, 0.0001f));
				MasterRenderer.RED += 0.00157f;
				MasterRenderer.GREEN += 0.00157f;
				MasterRenderer.BLUE += 0.0018f;
			} else if (SkyboxRenderer.getTime() >= 8000 && SkyboxRenderer.getTime() < 21000) {
				sun.setColour(new Vector3f(1f, 1f, 1f));
				MasterRenderer.RED = 0.5444f;
				MasterRenderer.GREEN = 0.62f;
				MasterRenderer.BLUE = 0.69f;
			} else {
				sun.decreaseColor(new Vector3f(0.0001f, 0.0001f, 0.0001f));
				MasterRenderer.RED -= 0.002f;
				MasterRenderer.GREEN -= 0.002f;
				MasterRenderer.BLUE -= 0.002f;
			}

			// **********Change color of fog END************************

			player.move(terrain);
			camera.move();
			picker.update();

			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
				system.generateParticles(
						new Vector3f(player.getPositionX(), player.getPositionY(), player.getPositionZ()));
				system.generateParticles(new Vector3f(160, 10, -280));// in
																		// location

				// isGenerateParticles = false;
				// new Particle(new Vector3f(player.getPosition()), new
				// Vector3f(0, 30, 0), 1, 4, 0, 1);
			}

			ParticleMaster.update(camera);

			en1.increaseRotation(0, 1, 0);
			en2.increaseRotation(0, 1, 0);
			en3.increaseRotation(0, 1, 0);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			// render reflection texture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight()); // water.getHeight()
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(level.getEntities(), normalMapEntities, terrains, lights, camera,
					new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1)); // -water.getHeight()
			camera.getPosition().y += distance;
			camera.invertPitch();

			// render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(level.getEntities(), normalMapEntities, terrains, lights, camera,
					new Vector4f(0, -1, 0, waters.get(0).getHeight() + 0.2f)); // water.getHeight()

			// render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			renderer.renderScene(level.getEntities(), normalMapEntities, terrains, lights, camera,
					new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waters, camera, sun);

			ParticleMaster.renderParticles(camera);

			guiRenderer.render(guiTextures);
			TextMaster.render();

			DisplayManager.updateDisplay();

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				running = false;
				PacketDisconnect packet = new PacketDisconnect(this.player.getEntityName());
				packet.writeData(this.socketClient);
			}
		}
		// *********Clean Up Below**************
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		// stop();
		// thread.interrupt();
		if (!isHost) {
			// socketClient.interrupt();
			this.stop();
		}
	}

	public static String getIp() {

		String myIp = "";
		InetAddress ip;

		try {
			ip = InetAddress.getLocalHost();
			myIp = ip.getHostAddress(); // This method returns the IP.
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return myIp;
	}

}
