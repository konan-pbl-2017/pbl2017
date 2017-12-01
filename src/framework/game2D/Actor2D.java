package framework.game2D;

import java.util.HashMap;

import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.gameMain.Actor;
import framework.model3D.BaseObject3D;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Placeable;
import framework.model3D.Position3D;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;
import framework.physics.Velocity3D;

/**
 * RadishFramework側のActorを隠蔽し、Actorへのアクセスを仲介する抽象クラス
 * 
 * @author T.Kuno
 * 
 */
public abstract class Actor2D implements Movable {
	private Actor actor;
	protected double collisionRadius = 0.5;

	private static HashMap<Class, Model3D> models = new HashMap<Class, Model3D>();
	private static HashMap<Class, Animation3D> animations = new HashMap<Class, Animation3D>();
	
	public abstract String getModelFileName();

	public abstract String getAnimationFileName();

	// /////////////////////////////////////////////////
	//
	// コンストラクタ
	//
	// /////////////////////////////////////////////////

	public Actor2D() {
		String modelFileName = getModelFileName();
		if (modelFileName == null) return;
		if (models.containsKey(this.getClass()) == false) {
			models.put(this.getClass(), ModelFactory
					.loadModel(modelFileName));
		}
		if (getAnimationFileName() != null
				&& animations.containsKey(this.getClass()) == false) {
			animations.put(this.getClass(), AnimationFactory
					.loadAnimation(getAnimationFileName()));
		}
		// キャラクターの3Dデータを読み込み配置する
		Object3D modelBody = models.get(this.getClass()).createObject();
		Animation3D animation = animations.get(this.getClass());
		if (animation != null) {
			Animation3D anim = new Animation3D(animation);
			setActor(createActor(modelBody, anim));
		} else {
			setActor(createActor(modelBody, null));
		}
	}

	// /////////////////////////////////////////////////
	//
	// Placebleのインターフェースの実装
	//
	// /////////////////////////////////////////////////

	public TransformGroup getTransformGroupToPlace() {
		return getActor().getBody().getTransformGroupToPlace();
	}

	public BaseObject3D getBody() {
		return getActor().getBody();
	}

	// /////////////////////////////////////////////////
	//
	// RadishFramework上のActorに関するメソッド
	//
	// /////////////////////////////////////////////////

	abstract protected Actor createActor(Object3D modelBody, Animation3D anim);

	/**
	 * Actor2Dが持つActorに設定する
	 * 
	 * @param actor
	 *            -- RadishFrameworkで作られているActorに引数のActorを渡す
	 */
	public void setActor(Actor actor) {
		this.actor = actor;
	}

	/**
	 * Actor2Dが持つActorを返す
	 * 
	 * @return RadishFrameworkで作られているActor
	 */
	public Actor getActor() {
		return actor;
	}

	// /////////////////////////////////////////////////
	//
	// RadishFramework上のActorが持つPositionに関するメソッド
	//
	// /////////////////////////////////////////////////

	/**
	 * このActorの位置を pos に設定する
	 * 
	 * @param pos
	 *            -- 新しい位置
	 */
	public void setPosition(Position2D pos) {
		Position3D p = new Position3D(pos.getX(), pos.getY(), getActor()
				.getPosition().getZ());
		getActor().body.apply(p, false);
	}

	/**
	 * このActorの現在の位置を返す
	 * 
	 * @return 現在の位置
	 */
	public Position2D getPosition() {
		return new Position2D(getActor().getPosition().getX(), getActor()
				.getPosition().getY());
	}

	// ////////////////////////////////////////////////
	//
	// RadishFramework上のActorが持つDirectionに関するメソッド
	//
	// ////////////////////////////////////////////////

	/**
	 * このActorの向きを vec に設定する
	 * 
	 * @param vec
	 *            -- 新しい向き
	 */
	public void setDirection(Vector2d vec) {
		Vector3d v = new Vector3d(vec.x, vec.y, getActor().getDirection()
				.getZ());
		getActor().setDirection(v);
	}
	

	/**
	 * このActorの向きを vec に設定する
	 * 
	 * @param vec
	 *            -- 新しい向き
	 */
	public void setDirection(Vector3d vec) {
		getActor().setDirection(vec);
	}

	/**
	 * このActorの現在の向きを返す
	 * 
	 * @return 現在の位置
	 */
	public Vector2d getDirection() {
		return new Vector2d(getActor().getDirection().getX(), getActor()
				.getDirection().getY());
	}

	// ///////////////////////////////////////////////
	//
	// RadishFramework上のActorが持つVelocityに関するメソッド
	//
	// ///////////////////////////////////////////////

	/**
	 * このActorの速度を vel に設定する
	 * 
	 * @param vel
	 *            -- 新しい速度
	 */
	public void setVelocity(Velocity2D vel) {
		Velocity3D velocity = new Velocity3D(vel.getX(), vel.getY(), getActor()
				.getVelocity().getZ());
		((Solid3D) getActor().body).apply(velocity, false);
	}

	/**
	 * このActorの速度を velVector に設定する
	 * 
	 * @param velVector
	 *            -- 新しい速度
	 */
	public void setVelocity(Vector2d velVector) {
		Velocity2D vel = new Velocity2D(velVector.getX(), velVector.getY());
		setVelocity(vel);
	}

	/**
	 * このActorの現在の速度を返す
	 * 
	 * @return 現在の速度
	 */
	public Velocity2D getVelocity() {
		return new Velocity2D(getActor().getVelocity().getX(), getActor()
				.getVelocity().getY());
	}

	// /////////////////////////////////////////
	//
	// RadishFramework上のmotionメソッドを呼び出す
	//
	// ////////////////////////////////////////

	/**
	 * Actorのmotionメソッドを呼び出す
	 * 
	 * @param interval
	 */
	public void motion(long interval) {
		getActor().motion(interval);
	}

	/**
	 * Actorのmotionメソッドを呼び出す
	 * 
	 * @param interval
	 */
	public void motion(long interval, Ground2D ground) {
		getActor().motion(interval, ground.getGround());
	}
	
	public void rotate(double angle) {
		getActor().rotZ(angle);
	}
	
	public void rotateX(double angle) {
		getActor().rotX(angle);
	}
	
	public void rotateY(double angle) {
		getActor().rotY(angle);
	}
	
	public void rotateZ(double angle) {
		getActor().rotZ(angle);
	}

	// ///////////////////////////////////////////////////////////////
	//
	// RadishFramework上のPhysicsUtilityのcheckColisionメソッドを呼び出す
	//
	// ///////////////////////////////////////////////////////////////

	/**
	 * 自分と別の登場物との当たり判定
	 * 
	 * @param other
	 *            自分以外の登場物
	 * @return true --- 当たった, false --- 当たってない
	 */
	public boolean checkCollision(Movable other) {
		if (other.getBody() != null) {
			if (PhysicsUtility.checkCollision((Object3D) this.getActor().getBody(),
					null, (Object3D) other.getBody(), null) != null) {
				return true;
			}
			return false;
		} else if (other instanceof Sprite) {
			return ((Sprite)other).checkCollision(this);
		} else if (other instanceof Actor2D) {
			Vector2d v = new Vector2d();
			v.sub(this.getPosition().getVector2d(), other.getPosition().getVector2d());
			if (v.length() <= (collisionRadius + ((Actor2D)other).getCollisionRadius())) {
				return true;
			} else {
				return false;
			}			
		}
		return false;
	}

	/**
	 * Bounding Circle（境界円） の半径を設定する
	 * 
	 * @param collisionRadius
	 */
	public void setCollisionRadius(double collisionRadius) {
		this.collisionRadius = collisionRadius;
	}

	/**
	 * Bounding Circle（境界円） の半径を設定する
	 * 
	 * @return --- BSの半径
	 */
	public double getCollisionRadius() {
		return collisionRadius;
	}

}
