package framework.audio;

public class BGM3D {
	private static Sound3D currentBGM = null;
	
	public static Sound3D registerBGM(String fileName) {
		return new Sound3D(fileName);
	}
	
	public static void playBGM(Sound3D newBGM) {
		if (currentBGM != null) currentBGM.stop();
		newBGM.loopPlay();
		currentBGM = newBGM;
	}
}
