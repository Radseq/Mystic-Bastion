package postProcessing;

import engineTester.MainGameLoop;
import shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = MainGameLoop.fileManager.getShaderFile("contrastVertex");
	private static final String FRAGMENT_FILE = MainGameLoop.fileManager.getShaderFile("contrastFragment");
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
