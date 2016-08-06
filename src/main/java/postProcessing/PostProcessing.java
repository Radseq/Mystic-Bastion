package postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import bloom.BrightFilter;
import bloom.CombineFilter;
import engineTester.Settings;
import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import models.RawModel;
import renderEngine.Loader;

public class PostProcessing {

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur vBlur2;
	private static HorizontalBlur hBlur4;
	private static VerticalBlur vBlur4;
	private static HorizontalBlur bloomHBlur;
	private static VerticalBlur bloomVBlur;
	private static CombineFilter combineFilter;

	public static void init(Loader loader) {
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		brightFilter = new BrightFilter(Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2);
		hBlur2 = new HorizontalBlur(Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2);
		vBlur2 = new VerticalBlur(Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2);
		hBlur4 = new HorizontalBlur(Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4);
		vBlur4 = new VerticalBlur(Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4);
		bloomHBlur = new HorizontalBlur(Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8);
		bloomVBlur = new VerticalBlur(Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8);
		combineFilter = new CombineFilter();
	}

	public static void doPostProcessing(int colourTexture/*, for tut 48 int brightTexture*/) {
		start();
		//brightFilter.render(brightFilter.getOutputTexture());
		brightFilter.render(colourTexture); //disable this for tutorial 48
		//hBlur2.render(brightTexture); //uncomment this for tut 48
		hBlur2.render(brightFilter.getOutputTexture()); //disable this for 48 tut
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur4.render(brightFilter.getOutputTexture());
		vBlur4.render(hBlur4.getOutputTexture());
		//hBlur.render(vBlur2.getOutputTexture());
		//vBlur.render(hBlur.getOutputTexture());
		// contrastChanger.render(vBlur.getOutputTexture());

		bloomHBlur.render(brightFilter.getOutputTexture());
		bloomVBlur.render(bloomHBlur.getOutputTexture());
		combineFilter.render(colourTexture, vBlur2.getOutputTexture(), vBlur4.getOutputTexture(), bloomVBlur.getOutputTexture());
		// contrastChanger.render(colourTexture);
		end();
	}

	public static void cleanUp() {
		contrastChanger.cleanUp();
		brightFilter.cleanUp();
		//hBlur.cleanUp();
		//vBlur.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		hBlur4.cleanUp();
		vBlur4.cleanUp();
		bloomHBlur.cleanUp();
		bloomVBlur.cleanUp();
		combineFilter.cleanUp();
	}

	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
