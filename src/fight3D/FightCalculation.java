package fight3D;

public class FightCalculation {
	static final int MAX_HP = 100;
	static final int MIN_HP = 0;
	static final int MAX_TP = 999;
	static final int MIN_TP = 0;
	static final int MAX_GP = 100;
	static final int MIN_GP = 0;
	
	int initialiseHP(){
		return MAX_HP;
	}
	
	
	int initialiseTP(){
		return MIN_TP;
	}
	
	
	int initialiseGP(){
		return MAX_GP;
	}
	
	
	int decreaseHP(int enemiesAP,int myHP,int myDP){
		int afterHP = 0;
		afterHP = myHP - (enemiesAP*2 - myDP);
		if(afterHP<MIN_HP){
			afterHP = MIN_HP;
		}
		return afterHP;
	}
	
	
	boolean isDead(int myHP){
		if(myHP == MIN_HP){
			return true;
		}
		return false;
	}
	
	
	int increaseTP(int myAP,int enemiesHP,int enemiesDP,int myTP){
		int afterTP = 0;
		afterTP = myTP + (myAP*2 - enemiesDP)/2;
		if(afterTP>MAX_TP){
			afterTP = MAX_TP;
		}
		return afterTP;
	}
	
	
	int decreaseTP(int myTP){
		return myTP/2;
	}
	
	
	int increaseGP(int myGP){
		int afterGP = 0;
		afterGP = myGP+1;
		if(myGP>MAX_GP){
			myGP =MAX_GP;
		}
		return afterGP;
	}
	
	
	int decreaseGP(int enemiesAP,int myDP,int myGP){
		int afterGP = 0;
		afterGP = myGP - (enemiesAP*2 - myDP);
		if(afterGP<MIN_GP){
			afterGP = MIN_GP;
		}
		return afterGP;
	}
	
	boolean isOver(int myGP){
		if(myGP <= MIN_GP){
			return true;
		}
		return false;
	}
}
