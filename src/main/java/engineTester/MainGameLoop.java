package engineTester;

import java.util.Scanner;

public class MainGameLoop {

	private static World world = new World();

	public static void main(String[] args) {

		/*
		 * TODO Separate game server and game client, GUI, Buttons, sounds,
		 * collision detection.
		 */

		Scanner s = new Scanner(System.in);
		System.out.println("Are you host? Y/N");
		boolean isHost = s.nextLine().toLowerCase().startsWith("y");
		s.close();

		new MainGameLoop(isHost);

	}

	public MainGameLoop(boolean host) {
		world.isHost = host;
		world.start();
	}
}