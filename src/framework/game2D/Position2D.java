package framework.game2D;

import javax.vecmath.Vector2d;

import framework.model3D.Position3D;

/**
 * 2次元の座標値を表す
 * 
 * @author T.Kuno
 * 
 */
public class Position2D extends Property2D {

	private double x;
	private double y;

	// //////////////////////////////////////////////////////////////
	//
	// コンストラクタ
	//
	// //////////////////////////////////////////////////////////////
	public Position2D() {
		this.x = 0.0;
		this.y = 0.0;
	}

	public Position2D(double px, double py) {
		this.x = px;
		this.y = py;
	}

	public Position2D(Vector2d v) {
		this.x = v.x;
		this.y = v.y;
	}

	// コピーコンストラクタ
	public Position2D(Position2D p) {
		this.x = p.x;
		this.y = p.y;
	}

	// Position3DからPosition2Dにするコンストラクタ
	public Position2D(Position3D p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	// /////////////////////////////////////////////////////////
	//
	// 抽象メソッドの実装
	//
	// /////////////////////////////////////////////////////////

	public Position2D clone() {
		return new Position2D(this);
	}

	// //////////////////////////////////////////////////////
	//
	// Position2Dに関する加算・減算・掛け算メソッド
	//
	// //////////////////////////////////////////////////////

	// 加算
	/**
	 * Position2Dのx,yに加算する
	 * 
	 * @param x
	 *            座標値
	 * @param y
	 *            座標値
	 * @return Position2D
	 */
	public Position2D add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Position2Dのx,yに引数のPosition2Dのx,yを加算する
	 * 
	 * @param p
	 *            Position2D(x,y)
	 * @return Position2D
	 */
	public Position2D add(Position2D p) {
		this.x += p.x;
		this.y += p.y;
		return this;
	}

	/**
	 * Position2Dのx,yに引数のVector2dのx,yを加算する
	 * 
	 * @param v
	 *            2次元ベクトル
	 * @return Position2D
	 */
	public Position2D add(Vector2d v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	// 減算
	/**
	 * Position2Dのx,yに減算する
	 * 
	 * @param x
	 *            座標値
	 * @param y
	 *            座標値
	 * @return Position2D
	 */
	public Position2D sub(double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	/**
	 * Position2Dのx,yに引数のPosition2Dのx,yを減算する
	 * 
	 * @param p
	 *            Position2D(x,y)
	 * @return Position2D
	 */
	public Position2D sub(Position2D p) {
		this.x -= p.x;
		this.y -= p.y;
		return this;
	}

	/**
	 * Position2Dのx,yに引数のVector2dのx,yを減算する
	 * 
	 * @param v
	 *            2次元ベクトル
	 * @return Position2D
	 */
	public Position2D sub(Vector2d v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}

	// 掛け算
	/**
	 * Position2Dのx,yに引数の値だけ掛け算する
	 * 
	 * @param d
	 *            倍率
	 * @return Position2D
	 */
	public Position2D mul(double d) {
		this.x *= d;
		this.y *= d;
		return this;
	}

	// //////////////////////////////////////////////////////
	//
	// Position2Dに関するメソッド
	//
	// //////////////////////////////////////////////////////

	/**
	 * Position2Dのx,yに設定する
	 * 
	 * @param x
	 *            座標値
	 * @param y
	 *            座標値
	 * @return Position2D
	 */
	public Position2D set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	// xに関するメソッド
	/**
	 * Position2Dのxに引数のxを設定する
	 * 
	 * @param x
	 *            座標値
	 * @return Position2D
	 */

	public Position2D setX(double x) {
		this.x = x;
		return this;
	}

	/**
	 * Position2Dのxを取得する
	 * 
	 * @return Position2D
	 */

	public double getX() {
		return x;
	}

	/**
	 * Position2Dのxに引数の値を加算する
	 * 
	 * @param d
	 */
	public void addX(double d) {
		this.x += d;
	}

	// yに関するメソッド

	/**
	 * Position2Dのyに引数のyを設定する
	 * 
	 * @param x
	 *            座標値
	 * @return Position2D
	 */

	public Position2D setY(double y) {
		this.y = y;
		return this;
	}

	/**
	 * Position2Dのyを取得する
	 * 
	 * @return Position2D
	 */

	public double getY() {
		return y;
	}

	/**
	 * Position2Dのyに引数の値を加算する
	 * 
	 * @param d
	 */

	public void addY(double d) {
		this.y += d;
	}

	// Vector2dに関するメソッド
	/**
	 * Position2Dのx,yにVector2dで設定する
	 * 
	 * @return Position2D
	 */

	public Position2D setVector2d(Vector2d v) {
		x = v.x;
		y = v.y;
		return this;
	}

	/**
	 * Position2Dのx,yをVector2dで取得する
	 * 
	 * @return Vector2d
	 */

	public Vector2d getVector2d() {
		return new Vector2d(x, y);
	}

}
