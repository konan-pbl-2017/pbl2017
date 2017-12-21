package framework.game2D;

import java.math.BigDecimal;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;

import framework.model3D.BaseObject3D;

/**
 * 2次元の登場物のクラス
 * 
 * @author T.Kuno
 * 
 */
public class Sprite implements Movable {
	Box box = null;
	private TransformGroup transformGroup;
	private double collisionRadius;
	private float scale = 1.0f;

	// 登場物の配置位置
	private Position2D position = new Position2D();
	
	// 登場物の配置位置の奥行
	private double depth = 0.0;
	
	// 登場物の速度
	private Velocity2D velocity = new Velocity2D();


	// /////////////////////////////////////////////////
	//
	// コンストラクタ
	//
	// /////////////////////////////////////////////////

	public Sprite(String imageFile) {
		this(imageFile, 1.0f);
	}
	
	public Sprite(String imageFile, float scale) {
		this(imageFile, scale, 0.0);
	}

	public Sprite(String imageFile, float scale, double depth) {
		this.scale = scale;
		transformGroup = new TransformGroup();
		Appearance appearance = new Appearance();

		if (imageFile != null){
			TextureLoader loader = new TextureLoader(imageFile, TextureLoader.BY_REFERENCE, null);
			appearance.setTexture(loader.getTexture());
			appearance.setCapability(Appearance.ALLOW_TEXTURE_READ);
			appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		} 

		Material material = new Material();
		material.setLightingEnable(false);
		material.setDiffuseColor(0.0f, 0.0f, 0.0f);
		material.setAmbientColor(0.0f, 0.0f, 0.0f);
		material.setSpecularColor(0.0f, 0.0f, 0.0f);
		material.setEmissiveColor(1.0f, 1.0f, 1.0f);
		material.setShininess(1.0f);
		appearance.setMaterial(material);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.REPLACE);
		appearance.setTextureAttributes(ta);

		TransparencyAttributes transAttributes = new TransparencyAttributes();

		transAttributes.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_READ);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_WRITE);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_MODE_READ);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

		transAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
		transAttributes.setTransparency(0.0f);
		// transAttributes.setSrcBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA);
		transAttributes.setDstBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA);
		appearance.setTransparencyAttributes(transAttributes);

		box = new Box(1.0f * scale, 1.0f * scale, 0.0f, Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS, appearance);
		transformGroup.addChild(box);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		position.set(0.0, 0.0);
		setPosition(0.0, 0.0, depth);
		velocity.set(0.0, 0.0);
		setCollisionRadius(1.0);
	}


	// /////////////////////////////////////////////////
	//
	// SpriteのPositionに関するメソッド
	//
	// /////////////////////////////////////////////////

	/**
	 * スプライトの位置を設定する。
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		position.set(x, y);
		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(x, y, depth));
		transformGroup.setTransform(t3d);
	}
	
	/**
	 * スプライトの位置を設定する。
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y, double z) {
		depth = z;
		position.set(x, y);
		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(x, y, z));
		transformGroup.setTransform(t3d);
	}

	// インターフェースの実装
	@Override
	public Position2D getPosition() {
		return this.position;
	}

	@Override
	public void setPosition(Position2D pos) {
		setPosition(pos.getX(), pos.getY());
	}


	// //////////////////////////////////////////////////////
	//
	// SpriteのVelocityに関するメソッド
	//
	// //////////////////////////////////////////////////////
	/**
	 * スプライトの速度を設定する。
	 * 
	 * @param x
	 * @param y
	 */
	public void setVelocity(double x, double y) {
		velocity.set(x, y);
	}

	// インターフェースの実装
	@Override
	public void setVelocity(Velocity2D vel) {
		setVelocity(vel.getX(), vel.getY());
	}

	@Override
	public Velocity2D getVelocity() {
		return this.velocity;
	}

	// /////////////////////////////////////////
	//
	// Spriteのmotionメソッド
	//
	// ////////////////////////////////////////

	// インターフェースの実装
	@Override
	public void motion(long interval) {
		double frame = (double)interval / 1000.0;
		setPosition(position.getX() + frame * velocity.getX(), position.getY() + frame * velocity.getY());
	}

	/**
	 * オブジェクトとの衝突判定をし、その結果に応じて物体を動かす
	 * 
	 * @param interval
	 * @param mazeGround
	 *            --- 迷路ゲームのステージ
	 */
	public void motion(long interval, Map2D mazeGround) {
		// 衝突判定する前に一度スプライトの位置を動かす
		motion(interval);

		// 衝突判定および衝突応答
		mazeGround.collisionResponse(this);
	}

	
	// //////////////////////////////
	//
	// プレイヤーを左方向に動かすメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタの位置を左方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveLeft(double d) {
		this.position.addX(-1.0 * d / 100);
		setPosition(position);
	}

	// //////////////////////////////
	//
	// プレイヤーを右方向に動かすメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタの位置を右方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveRight(double d) {
		this.position.addX(1.0 * d / 100);
		setPosition(position);
	}

	// //////////////////////////////
	//
	// プレイヤーを上方向に動かすメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタの位置を上方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveUp(double d) {
		this.position.addY(1.0 * d / 100);
		setPosition(position);
	}

	// //////////////////////////////
	//
	// プレイヤーを下方向に動かすメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタの位置を下方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveDown(double d) {
		this.position.addY(-1.0 * d / 100);
		setPosition(position);
	}

	// //////////////////////////////
	//
	// 衝突判定関連のメソッド
	//
	// //////////////////////////////

	/**
	 * 衝突判定のBounding Sphere（境界球）をcollisionRadiusで設定する
	 * 
	 * @param collisionRadius
	 *            -- BoundingSphereの半径
	 */
	public void setCollisionRadius(double collisionRadius) {
		this.collisionRadius = collisionRadius;
	}

	/**
	 * 衝突判定のBounding Sphere（境界球）の半径を返す
	 * 
	 * @return　 BoundingSphereの半径
	 */
	public double getCollisionRadius() {
		return collisionRadius;
	}
	
	/**
	 * イメージを変更する
	 * @param imageFile イメージファイル名
	 */
	public void setImage(String imageFile) {
		Appearance appearance = box.getAppearance();
		if (imageFile != null && appearance != null){
			TextureLoader loader = new TextureLoader(imageFile, TextureLoader.BY_REFERENCE, null);
			appearance.setTexture(loader.getTexture());
		} 
	}

	@Override
	public BaseObject3D getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransformGroup getTransformGroupToPlace() {
		// TODO Auto-generated method stub
		return transformGroup;
	}

	@Override
	public boolean checkCollision(Movable other) {
		if (other instanceof Sprite) {
			Sprite otherSprite = (Sprite)other;
			double thisMinX = getPosition().getX();
			double thisMaxX = getPosition().getX() + 2.0 * (double)scale;
			double thisMinY = getPosition().getY();
			double thisMaxY = getPosition().getY() + 2.0 * (double)scale;
			double otherMinX = otherSprite.getPosition().getX();
			double otherMaxX = otherSprite.getPosition().getX() + 2.0 * (double)scale;
			double otherMinY = otherSprite.getPosition().getY();
			double otherMaxY = otherSprite.getPosition().getY() + 2.0 * (double)scale;
			if((otherMinX <= thisMinX && otherMaxX >= thisMaxX)
					|| (otherMinX >= thisMinX && otherMaxX <= thisMaxX)){
				if(otherMaxY > thisMinY && otherMaxY < thisMaxY){
					return true;
				} else if(otherMinY < thisMaxY && otherMinY > thisMinY){
					return true;
				}
			} else if((otherMinY <= thisMinY && otherMaxY >= thisMaxY)
				|| (otherMinY >= thisMinY && otherMaxY <= thisMaxY)){
				if(otherMaxX > thisMinX && otherMaxX < thisMaxX){
					return true;				
				} else if(otherMinX < thisMaxX && otherMinX > thisMaxX){
					return true;				
				}
			} else if (otherMinX > thisMinX && otherMinX < thisMaxX && otherMaxY > thisMinY && otherMaxY < thisMaxY) {
				return true;			
			} else if (otherMaxX > thisMinX && otherMaxX < thisMaxX && otherMaxY > thisMinY && otherMaxY < thisMaxY) {
				return true;			
			} else if (otherMaxX > thisMinX && otherMaxX < thisMaxX && otherMinY > thisMinY && otherMinY < thisMaxY) {
				return true;			
			} else if (otherMinX > thisMinX && otherMinX < thisMaxX && otherMinY > thisMinY && otherMinY < thisMaxY) {
				return true;			
			}
			return false;
		} else if (other instanceof Actor2D) {
			Vector2d v = new Vector2d();
			v.sub(this.getPosition().getVector2d(), other.getPosition()
					.getVector2d());
			if (v.length() <= (getCollisionRadius() + ((Actor2D)other).getCollisionRadius())) {
				return true;
			} else {
				return false;
			}			
		}
		return false;
	}
	
	public boolean checkCollisionWithRadius(Movable other) {
		if (other instanceof Sprite) {
			Sprite otherSprite = (Sprite)other;
			double thisMinX = getPosition().getX();
			double thisMaxX = getPosition().getX() + 2.0 * collisionRadius;
			double thisMinY = getPosition().getY();
			double thisMaxY = getPosition().getY() + 2.0 * collisionRadius;
			double otherMinX = otherSprite.getPosition().getX();
			double otherMaxX = otherSprite.getPosition().getX() + 2.0 * otherSprite.collisionRadius;
			double otherMinY = otherSprite.getPosition().getY();
			double otherMaxY = otherSprite.getPosition().getY() + 2.0 * otherSprite.collisionRadius;
			if((otherMinX <= thisMinX && otherMaxX >= thisMaxX)
					|| (otherMinX >= thisMinX && otherMaxX <= thisMaxX)){
				if(otherMaxY > thisMinY && otherMaxY < thisMaxY){
					return true;
				} else if(otherMinY < thisMaxY && otherMinY > thisMinY){
					return true;
				}
			} else if((otherMinY <= thisMinY && otherMaxY >= thisMaxY)
				|| (otherMinY >= thisMinY && otherMaxY <= thisMaxY)){
				if(otherMaxX > thisMinX && otherMaxX < thisMaxX){
					return true;				
				} else if(otherMinX < thisMaxX && otherMinX > thisMaxX){
					return true;				
				}
			} else if (otherMinX > thisMinX && otherMinX < thisMaxX && otherMaxY > thisMinY && otherMaxY < thisMaxY) {
				return true;			
			} else if (otherMaxX > thisMinX && otherMaxX < thisMaxX && otherMaxY > thisMinY && otherMaxY < thisMaxY) {
				return true;			
			} else if (otherMaxX > thisMinX && otherMaxX < thisMaxX && otherMinY > thisMinY && otherMinY < thisMaxY) {
				return true;			
			} else if (otherMinX > thisMinX && otherMinX < thisMaxX && otherMinY > thisMinY && otherMinY < thisMaxY) {
				return true;			
			}
			return false;
		} else if (other instanceof Actor2D) {
			Vector2d v = new Vector2d();
			v.sub(this.getPosition().getVector2d(), other.getPosition()
					.getVector2d());
			if (v.length() <= (getCollisionRadius() + ((Actor2D)other).getCollisionRadius())) {
				return true;
			} else {
				return false;
			}			
		}
		return false;
	}
}
