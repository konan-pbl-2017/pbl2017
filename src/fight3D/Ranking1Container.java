package fight3D;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

import framework.RWT.RWTContainer;
import framework.RWT.RWTLabel;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;


public class Ranking1Container extends RWTContainer {
	private static final long serialVersionUID = 883828261190427156L;
	
	private Game game;
	
	public Ranking1Container(Game g) {
		game = g;
	}
	
	public void build(GraphicsConfiguration gc) {
		// èâä˙âª
		removeAll();
		
		int character[] = game.getCharacter();
		int rank[] = game.getRank();

		//ÇPà ï\é¶
		int i;
		for(i=0 ; i<rank.length; i++) {
			if(rank[i]==1) break;
		}
		RWTLabel l1 = new RWTLabel();
		l1.setRelativePosition(0.5f,0.9f,RWTLabel.DRAW_CENTER);
		l1.setString("ÅyÅô1stÅôÅz");
		l1.setColor(Color.RED);
		Font f1 = new Font("",Font.PLAIN,40);
		l1.setFont(f1);
		addWidget(l1);		
		
		RWTLabel l11 = new RWTLabel();
		l11.setRelativePosition(0.5f,0.5f,RWTLabel.DRAW_CENTER);
		l11.setString(String.valueOf(i+1) + "P");
		l11.setColor(Color.YELLOW);
		Font f11 = new Font("",Font.PLAIN,30);
		l11.setFont(f11);
		addWidget(l11);
		
		RWTLabel l12 = new RWTLabel();
		l12.setRelativePosition(0.5f,0.65f,RWTLabel.DRAW_CENTER);
		l12.setString(CharacterManager.getInstance().getCharacter(character[i]).getName());
		l12.setColor(Color.WHITE);
		Font f12 = new Font("",Font.PLAIN,20);
		l12.setFont(f12);
		addWidget(l12);
		
		//ÇQà ï\é¶
		for(i=0 ; i<rank.length; i++) {
			if(rank[i]==2) break;
		}		
		RWTLabel l2 = new RWTLabel();
		l2.setRelativePosition(0.2f,0.9f,RWTLabel.DRAW_CENTER);
		l2.setString("ÅyÅñ2ndÅñÅz");
		l2.setColor(Color.GREEN);
		Font f2 = new Font("",Font.PLAIN,25);
		l2.setFont(f2);
		addWidget(l2);
		
		RWTLabel l21 = new RWTLabel();
		l21.setRelativePosition(0.2f,0.6f,RWTLabel.DRAW_CENTER);
		l21.setString(String.valueOf(i+1) + "P");
		l21.setColor(Color.YELLOW);
		Font f21 = new Font("",Font.PLAIN,20);
		l21.setFont(f21);
		addWidget(l21);
		
		RWTLabel l22 = new RWTLabel();
		l22.setRelativePosition(0.2f,0.75f,RWTLabel.DRAW_CENTER);
		l22.setString(CharacterManager.getInstance().getCharacter(character[i]).getName());
		l22.setColor(Color.WHITE);
		Font f22 = new Font("",Font.PLAIN,15);
		l22.setFont(f22);
		addWidget(l22);
		
		//ëËñºï\é¶
		RWTLabel l4 = new RWTLabel();
		l4.setRelativePosition(0.5f,0.1f,RWTLabel.DRAW_CENTER);
		l4.setString("ﬂÅñÅôåãâ î≠ï\ÅôÅñﬂ");
		l4.setColor(Color.WHITE);
		Font f4 = new Font("",Font.PLAIN,40);
		l4.setFont(f4);	
		addWidget(l4);
		
		repaint();
	}
	
	@Override
	public void keyReleased(RWTVirtualKey k){
		if(k.getVirtualKey() == RWTVirtualController.BUTTON_A) game.goNextGameState();
	}
	
	@Override
	public void keyTyped(RWTVirtualKey k){

	}

	@Override
	public void keyPressed(RWTVirtualKey key) {

	}
}
