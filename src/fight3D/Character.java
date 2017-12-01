package fight3D;
import framework.animation.Animation3D;
import framework.audio.Sound3D;
import framework.gameMain.Mode;
import framework.gameMain.ModeOnGround;
import framework.gameMain.ActorModel;
import framework.physics.Velocity3D;



public class Character extends ActorModel {

	private String name, comment;
	private String attackingPartName = null;
	private String upperAttackingPartName = null;
	private String jumpAttackingPartName = null;
	private long attackTimeLag = 0;
	private long upperAttackTimeLag = 0;
	private long jumpAttackTimeLag = 0;
	private WeaponModel weaponModel, upperWeaponModel, jumpWeaponModel;
	private Velocity3D weaponSpeed = new Velocity3D(4.0, 0.0, 0.0); 
	private Velocity3D upperWeaponSpeed = new Velocity3D(4.0, 0.0, 0.0);
	private Velocity3D jumpWeaponSpeed = new Velocity3D(4.0, 0.0, 0.0);
	private double runSpeed = 5.0, jumpPower = 10.0;
	private int p, jf, d, s;

	
	private static Animation3D defaultAnimation = new Animation3D();
	public Animation3D rightMoveAnimation = defaultAnimation;
	public Animation3D leftMoveAnimation = defaultAnimation;
	public Animation3D jumpAnimation = defaultAnimation;
	public Animation3D damagedAnimation = defaultAnimation;
	public Animation3D attackAnimation = defaultAnimation;
	public Animation3D upperAttackAnimation = defaultAnimation;
	public Animation3D jumpAttackAnimation = defaultAnimation;
	public Sound3D jumpSound = null;
	public Sound3D attackSound = null;
	public Sound3D upperAttackSound = null;
	public Sound3D jumpAttackSound = null;
	public Sound3D damagedSound = null;
	
	public Character(String fileName) {
		super(fileName);
	}

	/**
	 * アニメーションを返す
	 * @param m プレイヤーのモード
	 * @param s プレイヤーの状態
	 * @return アニメーション
	 */
	public Animation3D getAnimation(Mode m, State s) {
		if(m instanceof ModeOnGround) {
			//左右に動くアニメーションは地上にいるときのみ
			if(s instanceof StateLeftMove) {
				return new Animation3D(leftMoveAnimation);
			}
			else if(s instanceof StateRightMove) {
				return new Animation3D(rightMoveAnimation);
			}
		}
		if(s instanceof StateJump) {
			return new Animation3D(jumpAnimation);
		}
		else if(s instanceof StateAttack){
			return new Animation3D(attackAnimation);				
		}
		else if(s instanceof StateUpperAttack){
			return new Animation3D(upperAttackAnimation);				
		}
		else if(s instanceof StateJumpAttack){
			return new Animation3D(jumpAttackAnimation);				
		}
		else if(s instanceof StateDamaged){
			return new Animation3D(damagedAnimation);
		}
		return new Animation3D(defaultAnimation);
	}
	
	/**
	 * 効果音を返す
	 * @param m プレイヤーのモード
	 * @param s プレイヤーの状態
	 * @return 効果音
	 */
	public Sound3D getSoundEffect(Mode m, State s) {
		if(s instanceof StateJump) {
			return jumpSound;
		}
		else if(s instanceof StateAttack){
			return attackSound;				
		}
		else if(s instanceof StateUpperAttack){
			return upperAttackSound;				
		}
		else if(s instanceof StateJumpAttack){
			return jumpAttackSound;				
		}
		else if(s instanceof StateDamaged){
			return damagedSound;
		}
		return null;
	}
	
//	public Attackable getAttackable(Player p){
//		Attackable a = new AttackingPart(attackingPartName , p);
//		return a;
//	}

	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	// 攻撃する部分の名前（通常攻撃、上向きの攻撃、下向きの攻撃）
	public void setAttackingPartName(String apn) {
		attackingPartName = apn;
	}
	public String getAttackingPartName() {
		return attackingPartName;
	}
	
	public void setUpperAttackingPartName(String s) {
		upperAttackingPartName = s;
	}

	public String getUpperAttackingPartName() {
		return upperAttackingPartName;
	}
	
	public void setJumpAttackingPartName(String s) {
		jumpAttackingPartName = s;
	}

	public String getJumpAttackingPartName() {
		return jumpAttackingPartName;
	}

	// 武器モデル（通常攻撃、上向きの攻撃、下向きの攻撃）
	public void setWeaponModel(WeaponModel w) {
		weaponModel = w;
	}
	public WeaponModel getWeaponModel() {
		return weaponModel;
	}
	
	public void setUpperWeaponModel(WeaponModel w) {
		upperWeaponModel = w;
	}

	public WeaponModel getUpperWeaponModel() {
		return upperWeaponModel;
	}

	public void setJumpWeaponModel(WeaponModel w) {
		jumpWeaponModel = w;
	}

	public WeaponModel getJumpWeaponModel() {
		return jumpWeaponModel;
	}
	
	//飛び道具の速さ（通常攻撃、上向きの攻撃、下向きの攻撃）
	public void setWeaponSpeed(double x, double y, double z) {
		this.weaponSpeed = new Velocity3D(x, y, z);
	}

	public Velocity3D getWeaponSpeed() {
		return weaponSpeed;
	}

	public void setUpperWeaponSpeed(double x, double y, double z) {
		upperWeaponSpeed = new Velocity3D(x, y, z);
	}

	public Velocity3D getUpperWeaponSpeed() {
		return upperWeaponSpeed;
	}

	public void setJumpWeaponSpeed(double x, double y, double z) {
		jumpWeaponSpeed = new Velocity3D(x, y, z);
	}
				
	public Velocity3D getJumpWeaponSpeed() {
		return jumpWeaponSpeed;
	}
				
	//移動の速さ
	public void setRunSpeed(double rs) {
		this.runSpeed = rs;
	}

	public double getRunSpeed() {
		return runSpeed;
	}

	//攻撃力
	public void setPower(int p) {
		this.p = p;
	}

	public int getPower() {
		return p;
	}

	//ジャンプ力
	public void setJumpPower(double e) {
		this.jumpPower = e;
	}

	public double getJumpPower() {
		return jumpPower;
	}

	//ジャンプ回数
	public void setJumpFrequency(int jf) {
		this.jf = jf;
	}

	public int getJumpFrequency() {
		return jf;
	}

	//防御力
	public void setDefense(int d) {
		this.d = d;
	}

	public int getDefense() {
		return d;
	}
	
	public void setSize(int s) {
		this.s = s;
	}

	public int getSize() {
		return s;
	}
	
	//WeaponとAttackingPartとどちらを持っているかを聞くメソッド
	public boolean hasAttackingPart(){
		if(getAttackingPartName() != null)
			return true;
		else return false;
	}

	public boolean hasUpperAttackingPart(){
		if(getUpperAttackingPartName() != null)
			return true;
		else return false;
	}

	public boolean hasJumpAttackingPart(){
		if(getJumpAttackingPartName() != null)
			return true;
		else return false;
	}

	public void setAttackTimeLag(long attackTimeLag) {
		this.attackTimeLag = attackTimeLag;
	}

	public long getAttackTimeLag() {
		return attackTimeLag;
	}

	public void setUpperAttackTimeLag(long upperAttackTimeLag) {
		this.upperAttackTimeLag = upperAttackTimeLag;
	}

	public long getUpperAttackTimeLag() {
		return upperAttackTimeLag;
	}

	public void setJumpAttackTimeLag(long jumpAttackTimeLag) {
		this.jumpAttackTimeLag = jumpAttackTimeLag;
	}

	public long getJumpAttackTimeLag() {
		return jumpAttackTimeLag;
	}

}
