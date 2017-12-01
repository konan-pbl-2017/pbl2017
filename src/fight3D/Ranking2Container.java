package fight3D;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

import framework.RWT.RWTContainer;
import framework.RWT.RWTLabel;
import framework.RWT.RWTLine;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;


public class Ranking2Container extends RWTContainer {
	private static final long serialVersionUID = 7397968207139488409L;
	
	private Game game;
	
	public Ranking2Container(Game g) {
		game = g;
	}
	
	public void build(GraphicsConfiguration gc) {
		// ‰Šú‰»
		removeAll();
		
		int character[] = game.getCharacter();
		int rank[] = game.getRank();
		int tp[] = game.getTp();
		int Defeat[] = game.getDefeat();
		int Defeated[] = game.getDefeated();
		
		RWTLabel laa = new RWTLabel();
		laa.setRelativePosition(0.5f,0.1f,RWTLabel.DRAW_CENTER);
		laa.setString("ß–™Œ‹‰Ê”­•\~~Ú×~~™–ß");
		laa.setColor(Color.PINK);
		Font fa = new Font("",Font.PLAIN,35);
		laa.setFont(fa);
		addWidget(laa);

		
		RWTLine liw1 = new RWTLine();
		liw1.setRelativePosition(0.09f, 0.18f, 0.91f, 0.18f);
		liw1.setColor(Color.CYAN);
		addWidget(liw1);

		drawRow(0.25f, "y‡ˆÊz", "y–¼‘Oz", "yÚ×z");
		
		//‚PˆÊ•\¦
		int i;
		for(i=0 ; i<rank.length; i++) {
			if(rank[i]==1) break;
		}
		drawRow(0.35f, "™‚PˆÊ™", 
				CharacterManager.getInstance().getCharacter(character[i]).getName(), 
				"[TP:"+String.valueOf(tp[i])+"] [“|‚µ‚½”:"+String.valueOf(Defeat[i])+"] [“|‚³‚ê‚½”:"+String.valueOf(Defeated[i])+"]");

		//‚QˆÊ•\¦
		for(i=0 ; i<rank.length; i++) {
			if(rank[i]==2) break;
		}	
		drawRow(0.45f, "–‚QˆÊ–", 
				CharacterManager.getInstance().getCharacter(character[i]).getName(), 
				"[TP:"+String.valueOf(tp[i])+"] [“|‚µ‚½”:"+String.valueOf(Defeat[i])+"] [“|‚³‚ê‚½”:"+String.valueOf(Defeated[i])+"]");
			
		RWTLabel lab = new RWTLabel();
		lab.setRelativePosition(0.93f,0.98f,RWTLabel.DRAW_CENTER);
		lab.setString("yOKz");
		lab.setColor(Color.RED);
		Font fb = new Font("",Font.PLAIN,30);
		lab.setFont(fb);
		addWidget(lab);
		
		repaint();
	}
	
	public void keyPressed(RWTVirtualKey k){
		
	}
	
	public void keyReleased(RWTVirtualKey k){
		if(k.getVirtualKey() == RWTVirtualController.BUTTON_A){
			game.goNextGameState();
		}		
	}
	
	public void keyTyped(RWTVirtualKey k){
		
	}

	private void drawRow(float hight, String rank, String name, String data) {
		RWTLabel la1 = new RWTLabel();
		la1.setRelativePosition(0.17f,hight,RWTLabel.DRAW_CENTER);
		la1.setString(rank);
		la1.setColor(Color.WHITE);
		Font f1 = new Font("",Font.PLAIN,13);
		la1.setFont(f1);
		addWidget(la1);
		
		RWTLabel la2 = new RWTLabel();
		la2.setRelativePosition(0.35f,hight,RWTLabel.DRAW_CENTER);
		la2.setString(name);
		la2.setColor(Color.WHITE);
		la2.setFont(f1);
		addWidget(la2);
		
		RWTLabel la3 = new RWTLabel();
		la3.setRelativePosition(0.48f,hight);
		la3.setString(data);
		la3.setColor(Color.WHITE);
//		Font f3 = new Font("",Font.PLAIN,13);
		la3.setFont(f1);
		addWidget(la3);
		
		//‰¡ü
		RWTLine liw = new RWTLine();
		liw.setRelativePosition(0.09f, hight+0.03f, 0.91f, hight+0.03f);
		liw.setColor(Color.CYAN);
		addWidget(liw);
		//cü		
		RWTLine lih1 = new RWTLine();
		lih1.setRelativePosition(0.09f, hight-0.07f, 0.09f, hight+0.03f);
		lih1.setColor(Color.YELLOW);
		addWidget(lih1);
		
		RWTLine lih2 = new RWTLine();
		lih2.setRelativePosition(0.24f, hight-0.07f, 0.24f, hight+0.03f);
		lih2.setColor(Color.YELLOW);
		addWidget(lih2);
		
		RWTLine lih3 = new RWTLine();
		lih3.setRelativePosition(0.46f, hight-0.07f, 0.46f, hight+0.03f);
		lih3.setColor(Color.YELLOW);
		addWidget(lih3);
		
		RWTLine lih4 = new RWTLine();
		lih4.setRelativePosition(0.91f, hight-0.07f, 0.91f, hight+0.03f);
		lih4.setColor(Color.YELLOW);
		addWidget(lih4);

	}
}
