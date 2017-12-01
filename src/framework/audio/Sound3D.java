package framework.audio;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Sound3D {
	private Clip clip = null;
	
	public Sound3D(String fileName) {
		Clip clip = null; 		
		AudioInputStream aistream;
		try {
	        File file = new File(fileName);
			aistream = AudioSystem.getAudioInputStream(file);
			DataLine.Info info = new DataLine.Info(Clip.class, aistream.getFormat());
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(aistream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		this.clip = clip;
	}
	
	public void play() {
		clip.stop();
		clip.setFramePosition(0); 
		clip.start();
	}
	
	public void play(double vol) {
		FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue((float)Math.log10(vol) * 20);
		clip.stop();
		clip.setFramePosition(0); 
		clip.start();
	}

	public void loopPlay() {
		clip.stop();
		clip.setFramePosition(0); 
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		clip.stop();
	}
}
