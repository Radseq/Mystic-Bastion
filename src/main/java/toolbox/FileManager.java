package toolbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {
	private boolean isRelease;
	private final String resFolder = "src/main/resources";
	public final static String releasePath = new File(
			FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	public FileManager(boolean isRelease) {
		this.isRelease = isRelease;
	}

	public String getTextureFile(String textureFileName) {
		return (!isRelease) ? resFolder + "/" + textureFileName + ".png"
				: releasePath + File.separator + "images" + File.separator + textureFileName + ".png";

	}

	public String getShaderFile(String shaderFileName) {
		return (!isRelease) ? resFolder + "/" + shaderFileName + ".shader"
				: releasePath + File.separator + "shaders" + File.separator + shaderFileName + ".shader";
	}

	public String getFontFile(String fontFileName) {
		return (!isRelease) ? resFolder + "/" + fontFileName + ".fnt"
				: releasePath + File.separator + "fonts" + File.separator + fontFileName + ".fnt";
	}

	public String getModelFile(String modelFileName) {
		return (!isRelease) ? resFolder + "/" + modelFileName + ".obj"
				: releasePath + File.separator + "models" + File.separator + modelFileName + ".obj";
	}

	public void loadNatives() throws URISyntaxException {
		if (!isRelease) {
			System.setProperty("org.lwjgl.librarypath", new File("target/natives").getAbsolutePath());
		} else {
			System.setProperty("org.lwjgl.librarypath",
					new File(releasePath + File.separator + "natives").getAbsolutePath());
		}
	}

	public void loadFromJar() {
		saveNatives();
		saveImages();
		saveModels();
		saveFonts();
		saveShaders();
	}

	private void saveNatives() {
		System.out.println("Starting to copy over natives...");
		try {
			File Natives = new File("natives");
			if (!Natives.exists())
				Natives.mkdir();
			for (String file : getFilesByExtension("dll"))
				ExportResource(file, Natives + File.separator + file);
			for (String file : getFilesByExtension("dylib"))
				ExportResource(file, Natives + File.separator + file);
			for (String file : getFilesByExtension("so"))
				ExportResource(file, Natives + File.separator + file);
			for (String file : getFilesByExtension("jnilib"))
				ExportResource(file, Natives + File.separator + file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished copying natives...");
	}

	private void saveImages() {
		System.out.println("Starting to copy over images...");
		try {
			File Images = new File("images");
			if (!Images.exists())
				Images.mkdir();
			for (String file : getFilesByExtension("png")) {
				ExportResource(file, Images + File.separator + file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished copying images...");
	}

	private void saveModels() {
		System.out.println("Starting to copy over models...");
		try {
			File Models = new File("models");
			if (!Models.exists())
				Models.mkdir();
			for (String file : getFilesByExtension("obj"))
				ExportResource(file, Models + File.separator + file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished copying models...");
	}

	private void saveFonts() {
		System.out.println("Starting to copy over fonts...");
		try {
			File Fonts = new File("fonts");
			if (!Fonts.exists())
				Fonts.mkdir();
			for (String file : getFilesByExtension("fnt"))
				ExportResource(file, Fonts + File.separator + file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished copying fonts...");
	}

	private void saveShaders() {
		System.out.println("Starting to copy over shaders...");
		try {
			File Shaders = new File("shaders");
			if (!Shaders.exists())
				Shaders.mkdir();
			for (String file : getFilesByExtension("shader"))
				ExportResource(file, Shaders + File.separator + file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished copying shaders...");
	}

	private void ExportResource(String in, String out) throws Exception {
		InputStream ddlStream = FileManager.class.getClassLoader().getResourceAsStream(in);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);
			byte[] buf = new byte[2048];
			int r = ddlStream.read(buf);
			while (r != -1) {
				fos.write(buf, 0, r);
				r = ddlStream.read(buf);
			}
		} finally {
			if (fos != null) {
				fos.close();
				System.out.println("Copied " + out);
			}
		}
	}

	private List<String> getFilesByExtension(String extension) throws IOException {
		List<String> files = new ArrayList<String>();
		CodeSource src = FileManager.class.getProtectionDomain().getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			while (true) {
				ZipEntry e = zip.getNextEntry();
				if (e == null)
					break;
				String name = e.getName();
				if (name.contains("." + extension) && !name.contains("newdawn/slick"))
					files.add(name);
			}
		}
		return files;
	}
}