package framework.RWT;


public class RWTVirtualKey {

	private int player;
	private int physicalKey;
	private int virtualKey;

	public RWTVirtualKey(int player, int physicalKey, int virtualKey){
		setPlayer(player);
		setPhysicalKey(physicalKey);
		setVirtualKey(virtualKey);
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int getPlayer() {
		return player;
	}

	public void setPhysicalKey(int physicalKey) {
		this.physicalKey = physicalKey;
	}

	public int getPhysicalKey() {
		return physicalKey;
	}

	public void setVirtualKey(int virtualKey) {
		this.virtualKey = virtualKey;
	}

	public int getVirtualKey() {
		return virtualKey;
	}

	public void assignPlayer(int playernum) {
		
	}
}
