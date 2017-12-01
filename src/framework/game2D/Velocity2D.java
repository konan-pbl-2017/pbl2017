package framework.game2D;

import javax.vecmath.Vector2d;

/**
 * 2次元の速度ベクトルを表す
 * 
 * @author T.Kuno
 */
public class Velocity2D extends Property2D {
	private double x;
	private double y;

	// //////////////////////////////////////////////////////////////
	//
	// コンストラクタ
	//
	// //////////////////////////////////////////////////////////////

	public Velocity2D() {
		this.x = 0.0;
		this.y = 0.0;

	}

	public Velocity2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Velocity2D(Vector2d v) {
		this.x = v.x;
		this.y = v.y;
	}

	// コピーコンストラクタ
	public Velocity2D(Velocity2D v) {
		x = v.x;
		y = v.y;
	}

	// /////////////////////////////////////////////////////////
	//
	// 抽象メソッドの実装
	//
	// /////////////////////////////////////////////////////////

	@Override
	public Velocity2D clone() {
		return new Velocity2D(this);
	}

	// //////////////////////////////////////////////////////
	//
	// Velocity2Dに関するメソッド
	//
	// //////////////////////////////////////////////////////

	/**
	 * Velocity2Dのx,yに設定する
	 * 
	 * @param x
	 *            座標値
	 * @param y
	 *            座標値
	 * @return Velocity2D
	 */
	public Velocity2D set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Velocity2D setX(double x) {
		this.x = x;
		return this;
	}

	public double getX() {
		return this.x;
	}

	public Velocity2D setY(double y) {
		this.y = y;
		return this;
	}

	public double getY() {
		return this.y;
	}

	public Velocity2D add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Velocity2D addX(double x) {
		this.x += x;
		return this;
	}

	public Velocity2D addY(double y) {
		this.y += y;
		return this;
	}

	public Velocity2D add(Vector2d v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Velocity2D setVector2d(Vector2d v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public Vector2d getVector2d() {
		return new Vector2d(x, y);
	}

	public Velocity2D setVelocity(double velocity) {
		Vector2d v = getVector2d();
		double oldV = v.length();
		v.scale(velocity / oldV);
		setVector2d(v);
		return this;
	}

	public void scale(double d) {
		this.x *= -1.0;
		this.y *= -1.0;
	}
}
