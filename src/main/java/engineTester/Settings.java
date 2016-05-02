package engineTester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import renderEngine.DisplayManager;
import toolbox.FileManager;

public class Settings {
	// Game Settings
	public static int WINDOW_WIDTH = 800;
	public static int WINDOW_HEIGHT = 600;

	public static int FPS_CAP = 30;
	public static boolean SHOW_FPS = true;
	public static int FPS_REFRESH_TIME = 1000;

	public static int SHADOW_RENDER_DISTANCE = 100;
	public static int SHADOW_MAP_SIZE = 4096;

	public static int MAX_LIGHTS = 4; // do not change

	public static boolean FULL_SCREEN = false;
	public static boolean VSYNC = false;

	public static boolean ENABLE_ANTIALIASING = true;

	public static final String gameSettingPatch = FileManager.releasePath + "/config/settings.cfg";

	public static void loadSettings() {

		File f = new File(gameSettingPatch);
		if (f.exists()) {
			readFromSettingsFile();
		} else {
			writeToSettingsFile();
		}
	}

	public static void readFromSettingsFile() {
		Properties properties = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(gameSettingPatch);
			properties.load(input);

			WINDOW_WIDTH = Integer.parseInt(properties.getProperty("width"));
			WINDOW_HEIGHT = Integer.parseInt(properties.getProperty("height"));
			FPS_CAP = Integer.parseInt(properties.getProperty("fpsCap"));
			SHOW_FPS = Boolean.parseBoolean(properties.getProperty("showFPS"));
			FPS_REFRESH_TIME = Integer.parseInt(properties.getProperty("fpsRefreshTime"));
			SHADOW_RENDER_DISTANCE = Integer.parseInt(properties.getProperty("shadowRenderDistance"));
			SHADOW_MAP_SIZE = Integer.parseInt(properties.getProperty("shadowMapSize"));
			FULL_SCREEN = Boolean.parseBoolean(properties.getProperty("fullScreen"));
			VSYNC = Boolean.parseBoolean(properties.getProperty("vsync"));
			ENABLE_ANTIALIASING = Boolean.parseBoolean(properties.getProperty("antialiasing"));
			input.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void writeToSettingsFile() {
		Properties properties = new Properties();
		OutputStream output = null;

		try {
			new File(FileManager.releasePath + "/config/").mkdir();
			output = new FileOutputStream(gameSettingPatch);
			output.write(DisplayManager.WINDOWNAME.getBytes(Charset.forName("UTF-8")));

			properties.setProperty("width", String.valueOf(WINDOW_WIDTH));
			properties.setProperty("height", String.valueOf(WINDOW_HEIGHT));
			properties.setProperty("fpsCap", String.valueOf(FPS_CAP));
			properties.setProperty("showFPS", String.valueOf(SHOW_FPS));
			properties.setProperty("fpsRefreshTime", String.valueOf(FPS_REFRESH_TIME));
			properties.setProperty("shadowRenderDistance", String.valueOf(SHADOW_RENDER_DISTANCE));
			properties.setProperty("shadowMapSize", String.valueOf(SHADOW_MAP_SIZE));
			properties.setProperty("fullScreen", String.valueOf(FULL_SCREEN));
			properties.setProperty("vsync", String.valueOf(VSYNC));
			properties.setProperty("antialiasing", String.valueOf(ENABLE_ANTIALIASING));

			// root folder
			properties.store(output, "");
			output.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}