package bloom;

import engineTester.MainGameLoop;
import shaders.ShaderProgram;

public class BrightFilterShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = MainGameLoop.fileManager.getShaderFile("simpleVertex");
	private static final String FRAGMENT_FILE = MainGameLoop.fileManager.getShaderFile("brightFilterFragment");
	
	public BrightFilterShader() {
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
