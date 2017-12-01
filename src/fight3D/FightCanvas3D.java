package fight3D;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTLabel;


public class FightCanvas3D extends RWTCanvas3D implements FightListener{
	
	Game game;
	
	double distance;
	RWTLabel player[] = new RWTLabel[6];
	float showY = 0.8f;
	float showX[];
	
    static final double POINT_SPACE = 0.075;
	double Point_distance = 0;

    RWTLabel TP[] = new RWTLabel[6];
    static final float TP_showY = 0.85f;
	float TP_showX[];
	
	RWTLabel HP[] = new RWTLabel[6];
	static final float HP_showY = 0.9f;
	float HP_showX[];
	
	RWTLabel GP[] = new RWTLabel[6];
	static final float GP_showY = 0.95f;
	float GP_showX[];
	
	int playerNum;

	RWTLabel TIMER_Minute = new RWTLabel();
	RWTLabel kolon = new RWTLabel();
	RWTLabel TIMER_Second = new RWTLabel();
	
	public void update(Fight f){
		
		//各種ポイントの表示
		for(int i = 0; i < f.getPlayerList().size(); i++){
	        
	        TP_showX = new float[playerNum];
	        HP_showX = new float[playerNum];
	        	
	        	String T_P = Integer.toString(f.getPlayerList().get(i).getTp());
	        	String H_P = Integer.toString(f.getPlayerList().get(i).getHp());
	        	String G_P = Integer.toString(f.getPlayerList().get(i).getGp());
	        	
	        	TP[i].setString("TP : " + T_P);
	        	HP[i].setString("HP : " + H_P);
	        	GP[i].setString("GP : " + G_P);
	        	TP_showX[i] = (float) ((POINT_SPACE - 0.05) + Point_distance * i);
	        	HP_showX[i] = (float) ((POINT_SPACE - 0.05) + Point_distance * i);
	        	GP_showX[i] = (float) ((POINT_SPACE - 0.05) + Point_distance * i);
	        	TP[i].setRelativePosition((float)(TP_showX[i] + POINT_SPACE),TP_showY, RWTLabel.DRAW_RIGHT);
	        	HP[i].setRelativePosition((float)(HP_showX[i] + POINT_SPACE),HP_showY, RWTLabel.DRAW_RIGHT);
	        	GP[i].setRelativePosition((float)(HP_showX[i] + POINT_SPACE),GP_showY, RWTLabel.DRAW_RIGHT);
		}
		
		
		//タイマー表示
		String.valueOf(f.getRemnantMinute());
		String.valueOf(f.getRemnantSecond());
		

		String TIMER_M = Integer.toString(f.getRemnantMinute());
		String TIMER_S = Integer.toString(f.getRemnantSecond());
		if(TIMER_S.length() == 1){
			TIMER_S = "0" + TIMER_S;
		}
		
		TIMER_Minute.setString(TIMER_M);
		kolon.setString(":");
		TIMER_Second.setString(TIMER_S);
		
		if(f.checkEnd() == true){
			game.setRank(f.getRank());
			game.setTp(f.getTp());
			game.setDefeated(f.getDefeated());
			game.setDefeat(f.getDefeat());
			game.goNextGameState();
			
		} 
	}
	
	 ////////////////////////////////////////////////////////////////////////////////////       
	public FightCanvas3D(GraphicsConfiguration gc, Game g, int playerNum) {
		super(gc);
		init(g, playerNum);
	}

	public FightCanvas3D(Game g, int playerNum) {
		init(g, playerNum);
	}
	
	public void init(Game g, int playerNum) {
		game = g;
		this.playerNum = playerNum;
	//プレイヤー

		double space = 0.075;
        distance = (1.0 - space * 2) / (double)playerNum;

        showX = new float[playerNum];
        
        for(int i = 0; i < playerNum; i++){
        	player[i] = new RWTLabel();
        	String name = Integer.toString(i+1);
        	name = name + "P";
        	player[i].setString(name);
        	player[i].setColor(Color.GREEN);
        	showX[i] = (float) (distance * i);
        	player[i].setRelativePosition((float)(showX[i] + space),showY);
        	Font p = new Font("",Font.PLAIN,20);
        	player[i].setFont(p);
        	addWidget(player[i]);
        }
 
      //トータルポイント
        
		double Point_space = 0.075;
        Point_distance = (1.0 - Point_space * 2) / (double)playerNum;

        TP_showX = new float[playerNum];
        
        for(int i = 0; i < playerNum; i++){
        	TP[i] = new RWTLabel();
        	String name = "TP:";
        	
        	TP[i].setString(name);
        	TP[i].setColor(Color.RED);
        	TP_showX[i] = (float) ((Point_space - 0.06) + Point_distance * i);
        	TP[i].setRelativePosition((float)(TP_showX[i] + Point_space),TP_showY, RWTLabel.DRAW_LEFT);
        	Font tp = new Font("",Font.PLAIN,10);
        	TP[i].setFont(tp);
        	addWidget(TP[i]);
        }
        
        //ヒットポイント

        HP_showX = new float[playerNum];
        
        for(int i = 0; i < playerNum; i++){
        	HP[i] = new RWTLabel();
        	String name = "HP:";
        	
        	HP[i].setString(name);
        	HP[i].setColor(Color.BLUE);
        	HP_showX[i] = (float) ((Point_space - 0.06)+ Point_distance * i);
        	HP[i].setRelativePosition((float)(HP_showX[i] + Point_space),HP_showY, RWTLabel.DRAW_LEFT);
        	Font hp = new Font("",Font.PLAIN,10);
        	HP[i].setFont(hp);
        	addWidget(HP[i]);
        }
        
        //ガードポイント
        
        GP_showX = new float[playerNum];
        
        for(int i = 0; i < playerNum; i++){
        	GP[i] = new RWTLabel();
        	String name = "GP:";
        	
        	GP[i].setString(name);
        	GP[i].setColor(Color.YELLOW);
        	GP_showX[i] = (float) ((Point_space - 0.06) + Point_distance * i);
        	GP[i].setRelativePosition((float)(GP_showX[i] + Point_space),GP_showY, RWTLabel.DRAW_LEFT);
        	Font gp = new Font("",Font.PLAIN,10);
        	GP[i].setFont(gp);
        	addWidget(GP[i]);
        }

		TIMER_Minute.setColor(Color.WHITE);
		kolon.setColor(Color.WHITE);
		TIMER_Second.setColor(Color.WHITE);
		TIMER_Minute.setRelativePosition(0.8f, 0.1f);
		kolon.setRelativePosition(0.86f, 0.1f);
		TIMER_Second.setRelativePosition(0.9f, 0.1f);
		Font t1 = new Font("",Font.PLAIN,40);
		Font k = new Font("",Font.PLAIN,40);
		Font t2 = new Font("",Font.PLAIN,40);
		TIMER_Minute.setFont(t1);
		kolon.setFont(k);
		TIMER_Second.setFont(t2);
		addWidget(TIMER_Minute);
		addWidget(kolon);
		addWidget(TIMER_Second);
				
        Point_distance = (1.0 - POINT_SPACE * 2) / (double)playerNum;
	}


}
        	
