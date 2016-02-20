package gameSounds;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import engineTester.World;

public class SoundManager {
	// Global stores for the sounds:
	private HashMap<String, IntBuffer> buffersMap; // (name, buffer) pairs
	private HashMap<String, IntBuffer> sourcesMap; // (name, buffer) pairs

	private FloatBuffer listenerPosition; // Listener's position
	private FloatBuffer listenerOrientation; // Listener's orientation
	private FloatBuffer listenerVelocity; // Listener's velocity

	private float listenerAngle = 0; // listener's angle (in radians)

	/** The length of the audio */
	private float length;

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public SoundManager() {
		buffersMap = new HashMap<String, IntBuffer>();
		sourcesMap = new HashMap<String, IntBuffer>();

		initOpenAL();
		initListener();
	}

	private void initOpenAL() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			return;
		}
		AL10.alGetError();
	}

	// Position and orientate the listener:
	private void initListener() {
		// Set the listener's initial position:
		World world = new World();

		float x = world.player.getPosition().x;
		float y = world.player.getPosition().y;
		float z = world.player.getPosition().z;
		listenerPosition = BufferUtils.createFloatBuffer(3).put(new float[] { x, y, z });
		listenerPosition.flip();

		// Set the listener's initial orientation:
		// The first 3 elements are the "look at" point
		// The second 3 elements are the "up direction"
		listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });
		listenerOrientation.flip();

		// Set the listener's initial velocity:
		listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
		listenerVelocity.flip();

		AL10.alListener(AL10.AL_POSITION, listenerPosition);
		AL10.alListener(AL10.AL_VELOCITY, listenerVelocity);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOrientation);
	}

	public void cleanUp() {
		Set<String> keys = sourcesMap.keySet();
		Iterator<String> iter = keys.iterator();

		String name;
		IntBuffer buffer, source;
		while (iter.hasNext()) {
			name = iter.next();

			source = sourcesMap.get(name);
			System.out.println("Stopping " + name);
			AL10.alSourceStop(source);
			AL10.alDeleteSources(source);

			buffer = buffersMap.get(name);
			AL10.alDeleteBuffers(buffer);
		}
		AL.destroy();
	}

	public void stop() {
		Set<String> keys = sourcesMap.keySet();
		Iterator<String> iter = keys.iterator();
		String name;
		IntBuffer source;

		while (iter.hasNext()) {
			name = iter.next();

			source = sourcesMap.get(name);
			AL10.alSourceStop(source);
		}
	}

	public boolean load(String name, boolean toLoop, float volume) {
		if (sourcesMap.get(name) != null) {
			System.out.println(name + " already loaded");
			return true;
		}

		IntBuffer buffer = initBuffer(name);
		if (buffer == null)
			return false;

		IntBuffer source = initSource(name, buffer, toLoop, volume);

		if (source == null) {
			// no need for the buffer anymore
			AL10.alDeleteBuffers(buffer);
			return false;
		}

		if (toLoop)
			System.out.println("Looping source created for " + name);
		else
			System.out.println("Source created for " + name);

		buffersMap.put(name, buffer);
		sourcesMap.put(name, source);

		return true;
	}

	public boolean load(String name, float x, float y, float z, boolean toLoop, float volume) {
		if (load(name, toLoop, volume))
			return setPos(name, x, y, z);
		else
			return false;
	}

	// return a handle to the given resource
	// not used for now
	@SuppressWarnings("unused")
	private InputStream getResource(String resourceName) {
		return getClass().getClassLoader().getResourceAsStream(resourceName);
	}

	// Load a file, and create a buffer for it:
	private IntBuffer initBuffer(String filename) {
		// WaveData waveFile = WaveData.create(getResource("Sounds/" +
		// filename));

		WaveData waveFile = null;
		try {
			waveFile = WaveData.create(
					new BufferedInputStream(new FileInputStream("sound" + File.separatorChar + filename + ".wav")));
		} catch (FileNotFoundException e) {
			System.err.println("Tried to load sound " + filename + ".wav , didn't work");
			e.printStackTrace();
		}

		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		AL10.alGenBuffers(buffer);
		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			System.out.println("Error loading file: " + filename);
			return null;
		}
		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		return buffer;
	}

	// Create a source (a point in space that makes sound):
	private IntBuffer initSource(String name, IntBuffer buffer, boolean toLoop, float volume) {
		IntBuffer source = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(source);

		// Check for errors:
		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			System.out.println("Error creating a source for: " + name);
			return null;
		}

		// Position the source at the origin:
		FloatBuffer sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
		sourcePosition.flip();

		// The source has no initial velocity:
		FloatBuffer sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
		sourceVelocity.flip();

		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, volume);// volume
		AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePosition);
		AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVelocity);

		// Lenght of sound
		int bytes = AL10.alGetBufferi(buffer.get(0), AL10.AL_SIZE);
		int bits = AL10.alGetBufferi(buffer.get(0), AL10.AL_BITS);
		int channels = AL10.alGetBufferi(buffer.get(0), AL10.AL_CHANNELS);
		int freq = AL10.alGetBufferi(buffer.get(0), AL10.AL_FREQUENCY);

		int samples = bytes / (bits / 8);
		length = (samples / (float) freq) / channels;
		// end

		if (toLoop)
			AL10.alSourcei(source.get(0), AL10.AL_LOOPING, AL10.AL_TRUE); // looping
		else
			AL10.alSourcei(source.get(0), AL10.AL_LOOPING, AL10.AL_FALSE); // looping
		// AL10.alSourceQueueBuffers(source.get(0), buffer.get(0));
		return source;
	}

	// Move the named sound to (x, y, z):
	public boolean setPos(String name, float x, float y, float z) {
		IntBuffer source = sourcesMap.get(name);
		if (source == null) {
			System.out.println("No source found for " + name);
			return false;
		}

		// Create a FloatBuffer with the given coordinates:
		FloatBuffer sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[] { x, y, z });
		sourcePosition.flip();

		AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePosition);
		return true;
	}

	public boolean play(String name) {
		IntBuffer source = sourcesMap.get(name);
		if (source == null) {
			System.out.println("No source found for " + name);
			return false;
		}
		AL10.alSourcePlay(source.get(0));
		/*
		 * new java.util.Timer().schedule(new java.util.TimerTask() {
		 * 
		 * @Override public void run() {
		 * 
		 * } }, (long) length);
		 */
		// float a = DisplayManager.getFrameTimeSeconds();
		// if (length == a) {

		// }
		return true;
	}

	// move the listener by (x, z) step
	public void moveListener(float xStep, float zStep) {
		float x = listenerPosition.get(0) + xStep;
		float z = listenerPosition.get(2) + zStep;
		setListenerPos(x, z);
	}

	// position the listener at (xNew, zNew)
	public void setListenerPos(float xNew, float zNew) {
		float xOffset = xNew - listenerPosition.get(0);
		float zOffset = zNew - listenerPosition.get(2);

		// We are not changing the y-coord:
		// ( Listener only moves over XZ plane )
		listenerPosition.put(new float[] { xNew, 0.0f, zNew });
		listenerPosition.flip();

		AL10.alListener(AL10.AL_POSITION, listenerPosition); // update the
																// listener's
																// position

		// Keep the listener facing the same direction by
		// moving the "look at" point by the offset values:
		listenerOrientation.put(0, listenerOrientation.get(0) + xOffset);
		listenerOrientation.put(2, listenerOrientation.get(2) + zOffset);

		AL10.alListener(AL10.AL_ORIENTATION, listenerOrientation); // update the
																	// listener's
																	// orientation
	}

	// turn the listener counterclockwise by "angle" radians
	public void turnListener(float angle) {
		setListenerOrientation(listenerAngle + angle);
	}

	// set the listener's orientation to be "angle" radians
	// in the counterclockwise direction around the y-axis
	public void setListenerOrientation(float angle) {
		listenerAngle = angle;

		float xOffset = -1.0f * (float) Math.sin(angle);
		float zOffset = -1.0f * (float) Math.cos(angle);

		// face in the (xLen, zLen) direction by adding the
		// offset values to the listener's "look at" point:
		listenerOrientation.put(0, listenerOrientation.get(0) + xOffset);
		listenerOrientation.put(2, listenerOrientation.get(2) + zOffset);

		AL10.alListener(AL10.AL_ORIENTATION, listenerOrientation); // update the
																	// listener's
																	// orientation
	}
}

// Using: SoundManager soundManager = new SoundManager(); // Create the Sound
// Manager String soundName = "Footsteps";
// soundManager.load(soundName, false);
// load a sound (true = looping, // false = play once)

// soundManager.play(soundName); // play the sound

// Move the sound source
// soundManager.setPos( soundName, x, y, z );

// Move the listener
// moveListener( xStep, zStep ); // move relative to current position
// setListenerPos( x, z ); // move to the specified location

// Rotate the listener

// turnListener( angle ); // turn relative to current orientation

// setListenerOrientation( float angle ); // look in the specified // direction