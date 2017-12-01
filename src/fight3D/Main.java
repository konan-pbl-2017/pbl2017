package fight3D;


public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game g = new Game();
		g.setFramePolicy(5, true);
		g.start();
	}
}
