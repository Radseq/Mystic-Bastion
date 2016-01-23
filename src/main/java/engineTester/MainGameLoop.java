package engineTester;

import java.util.Scanner;

public class MainGameLoop {

	private static World world = new World();

	public static void main(String[] args) {

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