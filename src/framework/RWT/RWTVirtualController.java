package framework.RWT;

import java.awt.event.KeyEvent;


public class RWTVirtualController {

	private boolean bKeyDown[][] = {
			{false, false, false, false, false, false, false, false, false}, 
			{false, false, false, false, false, false, false, false, false}};
	private boolean rawKeyDown[] = {
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false, false
	};
	private boolean mouseButtonDown1 = false;
	private boolean mouseButtonDown2 = false;
	private boolean mouseButtonDown3 = false;
	private double mouseX = 0;
	private double mouseY = 0;
	private static RWTVirtualKey keyMap[] = new RWTVirtualKey[256];
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int RIGHT = 2;
	public static final int LEFT = 3;
	public static final int BUTTON_A = 4;
	public static final int BUTTON_B = 5;
	public static final int BUTTON_C = 6;
	public static final int BUTTON_D = 7;
	public static final int BUTTON_E = 8;
	
	public RWTVirtualController() {
		// player1用のkeyBindの初期化
		RWTVirtualController.keyBind(KeyEvent.VK_W, 0, RWTVirtualController.UP);	// w
		RWTVirtualController.keyBind(KeyEvent.VK_D, 0, RWTVirtualController.RIGHT);	//d
		RWTVirtualController.keyBind(KeyEvent.VK_A, 0, RWTVirtualController.LEFT);	// a
		RWTVirtualController.keyBind(KeyEvent.VK_S, 0, RWTVirtualController.DOWN);	// s
		RWTVirtualController.keyBind(KeyEvent.VK_V, 0, RWTVirtualController.BUTTON_B);	// v
		RWTVirtualController.keyBind(KeyEvent.VK_B, 0, RWTVirtualController.BUTTON_A);	// b
		RWTVirtualController.keyBind(KeyEvent.VK_SPACE, 0, RWTVirtualController.BUTTON_C);	// space
		RWTVirtualController.keyBind(KeyEvent.VK_E, 0, RWTVirtualController.BUTTON_D);	// e
		RWTVirtualController.keyBind(KeyEvent.VK_F, 0, RWTVirtualController.BUTTON_E);	// f
		
		// player2用のkeyBindの初期化
		//RWTVirtualController.keyBind(KeyEvent.VK_O, 1, RWTVirtualController.UP);	// o
		//RWTVirtualController.keyBind(KeyEvent.VK_SEMICOLON, 1, RWTVirtualController.RIGHT);	//;
		//RWTVirtualController.keyBind(KeyEvent.VK_K, 1, RWTVirtualController.LEFT);	// k
		//RWTVirtualController.keyBind(KeyEvent.VK_L, 1, RWTVirtualController.DOWN);	// l
		RWTVirtualController.keyBind(KeyEvent.VK_BACK_SLASH, 1, RWTVirtualController.BUTTON_B);	// \
		RWTVirtualController.keyBind(KeyEvent.VK_SHIFT, 1, RWTVirtualController.BUTTON_A);	// shift
		RWTVirtualController.keyBind(KeyEvent.VK_CONTROL, 1, RWTVirtualController.BUTTON_C);	// ctrl
		RWTVirtualController.keyBind(KeyEvent.VK_P, 1, RWTVirtualController.BUTTON_D);	// p
		
		
		//テンキー
		RWTVirtualController.keyBind(KeyEvent.VK_UP, 1, RWTVirtualController.UP);	// ↑
		RWTVirtualController.keyBind(KeyEvent.VK_RIGHT, 1, RWTVirtualController.RIGHT);	//→
		RWTVirtualController.keyBind(KeyEvent.VK_LEFT, 1, RWTVirtualController.LEFT);	// ←
		RWTVirtualController.keyBind(KeyEvent.VK_DOWN, 1, RWTVirtualController.DOWN);	// ↓
		
	}
	
	static public void keyBind(int keyCode, int playerNo, int buttonNo) {
		keyMap[keyCode] = new RWTVirtualKey(playerNo, keyCode, buttonNo);
	}
	
	public boolean isKeyDown(int player, int keyNo) {
		return bKeyDown[player][keyNo];
	}
	
	public boolean isKeyDown(int keyCode) {
		return rawKeyDown[keyCode];
	}
	
	public void setKeyDown(int keyCode, boolean b) {
		if (keyMap[keyCode] != null) {
			bKeyDown[keyMap[keyCode].getPlayer()][keyMap[keyCode].getVirtualKey()] = b;
		}
		rawKeyDown[keyCode] = b;
	}
	
	public RWTVirtualKey getVirtualKey(int keyCode) {
		return keyMap[keyCode];
	}
	
	public boolean isMouseButtonDown(int buttonNo) {
		switch (buttonNo) {
		case 0:
			return mouseButtonDown1;
		case 1:
			return mouseButtonDown2;
		case 2:
			return mouseButtonDown3;
		}
		return false;
	}
	
	public void setMouseButtonDown(int buttonNo, boolean b) {
		switch (buttonNo) {
		case 0:
			mouseButtonDown1 = b;
			break;
		case 1:
			mouseButtonDown2 = b;
			break;
		case 2:
			mouseButtonDown3 = b;
			break;
		}		
	}
	
	public double getMouseX() {
		return mouseX;
	}
	
	public double getMouseY() {
		return mouseY;
	}
	
	public void setMousePosition(double x, double y) {
		mouseX = x;
		mouseY = y;
	}

	public void moveMousePosition(double dx, double dy) {
		mouseX += dx;
		mouseY += dy;
	}
}
