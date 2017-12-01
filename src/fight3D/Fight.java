package fight3D;

import com.sun.j3d.utils.universe.SimpleUniverse;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTVirtualController;
import framework.gameMain.Actor;
import framework.model3D.CollisionResult;
import framework.model3D.Model3D;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.view3D.Camera3D;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * @author 新田直也
 *
 */
public class Fight { // 操作ボタン
//	static long speed[] = new long[2000];
//	static int count = 0;
	
	static final long INTERVAL = 17L;
	static final long ENDTIME = 60L * 1000L;
	
	static final double DEFAULT_STAGE_WIDTH = 10.0;
	static final double DEFAULT_AREA_RIGHT = 20.0;
	static final double DEFAULT_AREA_LEFT = -20.0;
	static final double DEFAULT_AREA_BOTTOM = -30.0;

	private ArrayList<Player> playerList = new ArrayList<Player>();
	ArrayList<Actor> actorList = new ArrayList<Actor>();
	ArrayList<Attackable> attackableList = new ArrayList<Attackable>();
	FightCalculation fightCalculation = new FightCalculation();

	Universe universe;
	Camera3D camera;
	Ground stageGround = null;

	FightListener fightListener;
	private boolean isEnd = false;
	int rank[];
	int defeated[];
	int defeat[];
	private int tp[];
	private long startTime = 0;
	private long leftTime;

	public Fight(RWTCanvas3D canvas, int stagenum, int[] selectedCharacterArray,
			FightListener fightListener) {
		
		this.universe = new Universe();
		this.fightListener = fightListener;
		
		// カメラの設定
		camera = new Camera3D(universe);
		camera.setSideView();
		canvas.attachCamera(camera);

		// 光源の設定
		createLight(universe);

		// ステージの読み込み
		Stage s = StageManager.getInstance().getStage(stagenum);
		Model3D model = s.getModel();
		Object3D stageObj = model.createObject();
		stageGround = new Ground(stageObj);
		universe.placeAsAReceiver(stageGround);
		RWTCanvas3D c = (RWTCanvas3D) fightListener;
		s.setBackgroundSize(new Dimension(c.getWidth(), c.getHeight()));
		universe.place(s.getBackground());

		// キャラクタの追加、武器の追加
		for (int i = 0; i < selectedCharacterArray.length; i++) {
			Player player = createPlayer(selectedCharacterArray[i]);
			player.setPosition(getPlayersPosition(i, selectedCharacterArray.length));
			player.placeTo(universe);
			getPlayerList().add(player);
			actorList.add(player);
			camera.addTarget(player.body);
			player.setHp(fightCalculation.initialiseHP());
			player.setGp(fightCalculation.initialiseGP());
			player.setTp(fightCalculation.initialiseTP());

		}

		universe.compile();

		leftTime = ENDTIME;
		rank = new int[2];
	}

	void createLight(Universe universe){
		//環境光
		AmbientLight amblight = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
		
		amblight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(amblight);
		
    	//平行光源
        DirectionalLight dirlight = new DirectionalLight(
        		true,                           //光のON/OFF
                new Color3f(1.0f, 1.0f, 1.0f),  //光の色
                new Vector3f(0.0f, -1.0f, -0.5f) //光の方向ベクトル
        );
        dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
        
        universe.placeLight(dirlight);
    }

	public Player createPlayer(int id) {
		Player player;
		Character c = CharacterManager.getInstance().getCharacter(id);
		player = new Player(c);

		return player;
	}
	
	public void progress(RWTVirtualController controller, long interval) {
		long curTime = System.currentTimeMillis();
		if (startTime == 0) {
			startTime = curTime;
		}
		
		// 経過時間の計算
//		if (count < 2000) speed[count++] = interval;
		if (interval > INTERVAL * 3) interval = INTERVAL * 3;
		leftTime = (ENDTIME - (curTime - startTime));
		
		// 画面を更新するためfightListenerにFightを渡す。Fightの情報を持つ。
		fightListener.update(this);

		// ５分経過したか
		if (leftTime < 0) {
			isEnd = true;
			calculateRank();
			calculateDefeated();
			calculateDefeat();
			calculateTP();
			fightListener.update(this);
//			for (int i = 0; i < count; i++) {
//				System.out.println(speed[i]);
//			}
			return;
		}

		// GPの自然回復
		for (int i = 0; i < getPlayerList().size(); i++) {
			getPlayerList().get(i).recoverGp();
		}

		// カメラを調節
		camera.adjust(interval);

		// キー入力があったかどうかを記録する
		boolean[] flags = new boolean[playerList.size()];
		for (int p = 0; p < flags.length; p++) {
			flags[p] = false;
		}
		for (int playerNum = 0; playerNum < getPlayerList().size(); playerNum++) {
			// keysのi番目の中身（キー入力）がキャラクタのどの動作に当てはまるか
			if (controller.isKeyDown(playerNum, RWTVirtualController.UP)) {
				flags[playerNum] = true;
				// 攻撃ボタンを同時に押していない場合ジャンプする
				if (!controller.isKeyDown(playerNum, RWTVirtualController.BUTTON_A)) getPlayerList().get(playerNum).startJump();
			}
			if (controller.isKeyDown(playerNum, RWTVirtualController.RIGHT)) {
				flags[playerNum] = true;
				// 攻撃ボタンを同時に押していない場合右に動く
				if (!controller.isKeyDown(playerNum, RWTVirtualController.BUTTON_A)) getPlayerList().get(playerNum).rightMove();
			}
			if (controller.isKeyDown(playerNum, RWTVirtualController.LEFT)) {
				flags[playerNum] = true;
				// 攻撃ボタンを同時に押していない場合左に動く
				if (!controller.isKeyDown(playerNum, RWTVirtualController.BUTTON_A)) getPlayerList().get(playerNum).leftMove();
			}
			if (controller.isKeyDown(playerNum, RWTVirtualController.BUTTON_B)) {
				flags[playerNum] = true;
				// 攻撃ボタンを押していない場合ガードのメソッドを呼ぶ
				if (!controller.isKeyDown(playerNum, RWTVirtualController.BUTTON_A)) getPlayerList().get(playerNum).guard();
			}
			if (controller.isKeyDown(playerNum, RWTVirtualController.BUTTON_A)) {
				flags[playerNum] = true;
				// 攻撃のメソッドを呼ぶ
				Attackable a;
				Player player = getPlayerList().get(playerNum);
				if (!player.isOnGround()) {
					if (player.getElapsedTimeInJumping() > 2L || player.getElapsedTimeInJumping() < 0L) {
						// 空中にいるときは基本的にジャンプ攻撃
						a = player.startJumpAttack();
					} else {
						// ジャンプしてすぐは対空攻撃をするようにする
						// （そうしないと対空攻撃を出す操作が難しくなるため）
						a = player.startUpperAttack();						
					}
				} else {
					// 地面にいるとき
					if (controller.isKeyDown(playerNum, RWTVirtualController.UP)) {
						// 対空攻撃
						a = player.startUpperAttack();
					} else {
						// 通常攻撃
						a = player.startNormalAttack();
					}
				}
				if (a != null) {
					attackableList.add(a);
					if (a instanceof Weapon) {
						if (a.isMovable()) {
							actorList.add((Weapon) a);
						}
					}
				}
			}
		}
		for (int i = 0; i < flags.length; i++) {
			if (!flags[i]) {
				getPlayerList().get(i).normal(false);
			}
		}

		// 全登場物を動かす
		for (int i = 0; i < actorList.size(); i++) {
			Actor p = actorList.get(i);
			// 移動
			p.motion(interval, stageGround);
			// 範囲外に出たかどうかの判定
			Position3D pos = p.getPosition();
			if (pos.getY() < DEFAULT_AREA_BOTTOM
					|| pos.getX() > DEFAULT_AREA_RIGHT
					|| pos.getX() < DEFAULT_AREA_LEFT) {
				// 範囲外に出たときの処理
				outOfArea(i);
			}
		}

		// 攻撃の当たり判定
		hitJudg();
	}

	/**
	 * 範囲外に出たときの処理
	 * @param p 対象物
	 */
	private void outOfArea(int index) {
		Actor p = actorList.get(index);
		if (p instanceof Player) {
			playerDefeated(index);
		} else if (p instanceof Weapon) {
			((Weapon)p).disappear();
		}
	}

	/**
	 * 攻撃の当たり判定と、当たったときの処理
	 */
	private void hitJudg() {
		// 当たり判定
		for (int i = attackableList.size() - 1; i >= 0; i--) {

			Attackable attack = attackableList.get(i);
			if (attack.isAlive() && attack.isActivate()) {
				// 攻撃物が有効だった場合
				for (int j = 0; j < getPlayerList().size(); j++) {
					CollisionResult collisionResult = null;
					Player attacker = attack.getOwner();
					Player damagedCandidate = getPlayerList().get(j);
	
					if (attacker != damagedCandidate) {
						collisionResult = PhysicsUtility.checkCollision(attack.getBody(), 
								attack.getPartName(),
								(Object3D) damagedCandidate.body,
								null);
					}
					// 衝突があった場合
					if (collisionResult != null) {
						// if (attacker != damagedCandidate) {
	
						if (damagedCandidate.state.getClass() != StateGuard.class) {
							// ダメージを受ける。
							if (damagedCandidate.state.getClass() == StateFlinch.class) {
								damagedCandidate.normal(true);
								damagedCandidate.setGp(100);
							}
							damagedCandidate.damaged(attacker.getPosition());
							int afterHP = (int) fightCalculation.decreaseHP(attack
									.getAP(), damagedCandidate.getHp(),
									damagedCandidate.character.getDefense());
							// 攻撃によってキャラが死んでいない場合
							if (fightCalculation.isDead(afterHP) != true) {
								damagedCandidate.setHp(afterHP);
								// damagedCandidate.changeState(new StateFlinch(),
								// false);
							}
							// 死んだ場合
							else {
								// キャラを殺したキャラクタの「倒した回数」を１増やす
								attacker.setDefeat(attacker.getDefeat() + 1);
	
								// キャラが倒されたときの処理
								playerDefeated(j);
							}
							// 攻撃によるTPの上昇
							int attackerAfterTP = fightCalculation.increaseTP(
									attack.getAP(), damagedCandidate.getHp(),
									damagedCandidate.character.getDefense(), attacker.getTp());
							attacker.setTp(attackerAfterTP);
						} else {
							// ダメージを受けない。
							int afterGP = (int) fightCalculation.decreaseGP(attack
									.getAP(), damagedCandidate.character.getDefense(), damagedCandidate.getGp());
	
							if (!fightCalculation.isOver(afterGP)) {
								// 攻撃によってGPが０にならなかった場合
								damagedCandidate.setGp(afterGP);
							} else {
								// 攻撃によってGPが０になった場合
								damagedCandidate.flinch();
								damagedCandidate.setGp(0);
							}
						}
	
						// 攻撃物を無効にする
						attack.disappear();
					}
				}
			}
			if (!attack.isAlive()) {
				// 攻撃物が無効になっていた場合、攻撃物リストから削除
				attackableList.remove(i);
				for (int j = 0; j < actorList.size(); j++) {
					if (actorList.get(j) == attack) {
						actorList.remove(j);
						break;
					}
				}
				continue;
			}
		}
	}

	/**
	 * プレイヤーが倒されたときの処理
	 * @param index 倒されたプレイヤーの番号
	 */
	private void playerDefeated(int index) {
		Player damagedCandidate = getPlayerList().get(index);
		
		// TPを減らす
		int damagedAfterTP = fightCalculation
				.decreaseTP(damagedCandidate.getTp());
		damagedCandidate.setTp(damagedAfterTP);

		// 死んだキャラクタの「死亡回数」を１増やす
		damagedCandidate.setDefeated(damagedCandidate
				.getDefeated() + 1);

		// HPとGPを初期値に戻す
		damagedCandidate.setHp(fightCalculation
				.initialiseHP());
		damagedCandidate.setGp(fightCalculation
				.initialiseGP());
		
		// 出現位置に再登場させる
		damagedCandidate.setPosition(getPlayersPosition(index, getPlayerList().size()));
	}

	/**
	 * 
	 * プレイヤーの登場（再登場）位置を返す
	 * @param index　プレイヤーの番号
	 * @param numPlayer 全プレイヤーの数
	 * @return
	 */
	private Position3D getPlayersPosition(int index, int numPlayer) {
		return new Position3D((((double)index / (double)(numPlayer - 1)) - 0.5) * DEFAULT_STAGE_WIDTH, 0.0, 0.0);
	}

	private void calculateTP() {
		// TODO Auto-generated method stub
		tp = new int[playerList.size()];
		for (int i = 0; i < playerList.size(); i++) {
			tp[i] = playerList.get(i).getTp();
		}
	}

	private void calculateDefeated() {
		// TODO Auto-generated method stub
		defeated = new int[playerList.size()];
		for (int i = 0; i < playerList.size(); i++) {
			defeated[i] = playerList.get(i).getDefeated();
		}
	}

	private void calculateDefeat() {
		// TODO Auto-generated method stub
		defeat = new int[playerList.size()];
		for (int i = 0; i < playerList.size(); i++) {
			defeat[i] = playerList.get(i).getDefeat();
		}
	}

	private void calculateRank() {
		// TODO Auto-generated method stub

		for (int i = 0; i < rank.length; i++) {
			int max = 0;
			int maxNum = 0;
			int playerTp;
			for (int j = 0; j < rank.length; j++) {
				if (rank[j] == 0
						&& max <= (playerTp = getPlayerList().get(j).getTp())) {
					max = playerTp;
					maxNum = j;
				}
			}
			rank[maxNum] = i + 1;
		}
	}

	int getRemnantMinute() {
		int time = (int) (leftTime / 1000) / 60;
		if(time > 0) {
			return time;
		}
		return 0;
	}

	int getRemnantSecond() {
		int time = (int) (leftTime / 1000) % 60;
		if (time > 0) {
			return time;
		}
		return 0;
	}

	boolean checkEnd() {
		return isEnd;
	}

	int[] getRank() {
		return rank;
	}

	ArrayList<Player> getPlayerList() {
		return playerList;
	}

	int[] getTp() {
		return tp;
	}

	int[] getDefeated() {
		return defeated;
	}

	int[] getDefeat() {
		return defeat;
	}
}