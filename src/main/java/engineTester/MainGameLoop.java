package engineTester;

import java.io.IOException;
import java.net.URISyntaxException;

import toolbox.FileManager;

public class MainGameLoop {

	private static World world = new World();

	public static FileManager fileManager;

	public static void main(String[] args) throws URISyntaxException, IOException {
		if (args.length == 0) {
			System.out.println("Please supply one of the following arguments");
			System.out.println("dev - Only for development use!");
			System.out.println("launch - For launching after using 'copy' argument");
			System.out.println("copy - For copying resources from jar to current dir");
		} else {
			if (args[0].equalsIgnoreCase("dev")) {
				fileManager = new FileManager(false);
				world.start();
			} else if (args[0].equalsIgnoreCase("copy")) {
				fileManager = new FileManager(false);
				fileManager.loadFromJar();
			} else if (args[0].equalsIgnoreCase("launch")) {
				fileManager = new FileManager(true);
				world.start();
			}
		}

	}
}