package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	private static final int WIDTH = 800; // 1280
	private static final int HEIGHT = 600; // 720
	private static final int FPS_CAP = 60;
	// Whether to use fullscreen mode
	public static final boolean FULLSCREEN = false; // added to future support
													// of options screen

	private static long lastFrameTime;
	private static float delta;
	public final static String WINDOWNAME = "Mystic Bastion!";
	// Whether to enable VSync in hardware.
	public static final boolean VSYNC = false; // added to future support of
												// options screen
	// frames per second
	static int fps;
	static boolean SHOWFPS = false;
	/** last fps time */
	static long lastFPS;
	static int fpsCountRefreshRate;

	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true); // opengl
																												// 3,2

		try {
			Display.setResizable(true); // whether our window is resizable
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setVSyncEnabled(VSYNC); // whether hardware VSync is enabled
			Display.setFullscreen(FULLSCREEN); // whether fullscreen is enabled
			Display.create(new PixelFormat(), attribs);
			Display.setTitle(WINDOWNAME);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		lastFPS = lastFrameTime;
	}

	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		if (SHOWFPS) {
			updateFPS(); // update FPS Counter
		}
	}

	public static boolean ShowFPS(boolean b) {
		return SHOWFPS = b;
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

	public static int setFpsCountRefreshRate(int rate) {
		return fpsCountRefreshRate = rate;
	}
}
