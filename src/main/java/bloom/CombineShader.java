package bloom;

import engineTester.MainGameLoop;
import shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = MainGameLoop.fileManager.getShaderFile("simpleVertex");
	private static final String FRAGMENT_FILE = MainGameLoop.fileManager.getShaderFile("combineFragment");
	
	private int location_colourTexture;
	private int location_highlightTexture2;
	private int location_highlightTexture4;
	private int location_highlightTexture8;
	
	protected CombineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_colourTexture = super.getUniformLocation("colourTexture");
		location_highlightTexture2 = super.getUniformLocation("highlightTexture2");
		location_highlightTexture4 = super.getUniformLocation("highlightTexture4");
		location_highlightTexture8 = super.getUniformLocation("highlightTexture8");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_colourTexture, 0);
		super.loadInt(location_highlightTexture2, 1);
		super.loadInt(location_highlightTexture4, 2);
		super.loadInt(location_highlightTexture8, 3);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
