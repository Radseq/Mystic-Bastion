package gameSounds;

import java.io.IOException;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Sounds {

	private Audio audio;
	private float pitch;
	private float gain;
	private boolean replay;

	public Sounds(float pitch, float gain, boolean replay) {
		this.pitch = pitch;
		this.gain = gain;
		this.replay = replay;
	}

	public void loadSound(String fileName, String exe) {
		try {
			String withoutDot = exe.replace(".", "");
			if (exe == ".ogg") {
				audio = AudioLoader.getAudio(withoutDot.toUpperCase(),
						ResourceLoader.getResourceAsStream("sound/" + fileName + exe));
			} else if (exe == ".xm") {
				audio = AudioLoader.getStreamingAudio("MOD",
						ResourceLoader.getResource("sound/" + fileName + exe.toUpperCase()));
			} else if (exe == ".aif") {
				audio = AudioLoader.getAudio(withoutDot.toUpperCase(),
						ResourceLoader.getResourceAsStream("sound/" + fileName + exe));
			} else if (exe == ".wav") {
				audio = AudioLoader.getAudio(withoutDot.toUpperCase(),
						ResourceLoader.getResourceAsStream("sound/" + fileName + exe));
			} else {
				System.out.println("Only Support OGG MOD AIF or WAV");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playMusic(String fileName, String exe) {
		loadSound(fileName, exe);
		audio.playAsMusic(pitch, gain, replay);
	}

	public void playSoundEffect(String fileName, String exe) {
		loadSound(fileName, exe);
		audio.playAsSoundEffect(pitch, gain, replay);
	}

	public void cleanUp() {
		AL.destroy();
	}
}
