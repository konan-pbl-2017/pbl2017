package fight3D;
import javax.media.j3d.BranchGroup;

import framework.model3D.Object3D;


/**
 * プレイヤーの体の中の攻撃に使用している部分
 * @author 新田直也
 *
 */
public class AttackingPart implements Attackable {

	private String partName = null;
	private Player owner;
	private boolean fActive = true;
	private long timeLag = 0;	// 攻撃が効き始めるまでの時間（発生時間）
	
	//コンストラクタ
	public AttackingPart(String pn, Player o, long t){
		partName = pn;
		owner = o;
		timeLag = t;
	}
	
	@Override
	//攻撃力を返す
	public int getAP() {
		// TODO Auto-generated method stub
		return owner.character.getPower();
	}

	@Override
	//物体（３次元）の形を教える
	public Object3D getBody() {
		// TODO Auto-generated method stub
		return owner.body;
	
	}

	@Override
	public String getPartName() {
		// TODO Auto-generated method stub
		return partName;
	}

	@Override
	//誰が攻撃したか（有効な攻撃）
	public Player getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}

	@Override
	//攻撃する物（体の一部や武器）と一緒に自身も動くことができるか
	public boolean isMovable() {
		// TODO Auto-generated method stub
		return false;
	}

	//攻撃し終わったら、消える
	void onEndAnimation() {
		disappear();
	}

	//存在しているか
	public boolean isAlive() {
		return fActive;
	}

	@Override
	public void appear() {
		// TODO Auto-generated method stub
		fActive = true;
	}

	@Override
	public void disappear() {
		// TODO Auto-generated method stub
		fActive = false;		
	}

	@Override
	public boolean isActivate() {
		// TODO Auto-generated method stub
		return (owner.animation.time >= timeLag);
	}
}
