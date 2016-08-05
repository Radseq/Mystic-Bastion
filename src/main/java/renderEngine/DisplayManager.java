package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

import engineTester.Settings;

public class DisplayManager {

	private static long lastFrameTime;
	private static float delta;
	public final static String WINDOWNAME = "Mystic Bastion!";

	// frames per second
	static int fps;
	static boolean SHOWFPS = Settings.SHOW_FPS;
	/** last fps time */
	static long lastFPS;
	static int fpsCountRefreshRate = Settings.FPS_REFRESH_TIME;

	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);

		try {
			Display.setResizable(true); // whether our window is resizable
			Display.setDisplayMode(new DisplayMode(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT));
			Display.setVSyncEnabled(Settings.VSYNC); // whether hardware VSync
														// is enabled
			Display.setFullscreen(Settings.FULL_SCREEN); // whether fullscreen
															// is
															// enabled
			if (Settings.ENABLE_ANTIALIASING) {
				Display.create(new PixelFormat().withDepthBits(24), attribs);
				System.out.println(GL11.glGetInteger(GL11.GL_DEPTH_BITS));
				GL11.glEnable(GL13.GL_MULTISAMPLE);
			} else {
				Display.create(new PixelFormat(), attribs);
			}
			Display.setTitle(WINDOWNAME);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		GL11.glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
		lastFrameTime = getCurrentTime();
		lastFPS = lastFrameTime;
	}

	public static void updateDisplay() {
		Display.sync(Settings.FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		if (SHOWFPS) {
			updateFPS(); // update FPS Counter
		}
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static void closeDisplay() {
		Display.destroy();
	}

	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	/**
	 * Calculate the FPS
	 */
	public static void updateFPS() {
		if (getCurrentTime() - lastFPS > fpsCountRefreshRate) {
			Display.setTitle(WINDOWNAME + " FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
}
