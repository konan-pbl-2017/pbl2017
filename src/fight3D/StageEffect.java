package fight3D;
import javax.vecmath.Point3d;


public class StageEffect {

	private Point3d p1 = new Point3d();
	private Point3d p2 = new Point3d();
	private String effect;
	
	//ステージの、特殊効果のある位置
	public void setPositions(Point3d p1, Point3d p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Point3d[] getPositions(){
		Point3d[] p = {p1, p2};
		return p;
	}

	//ステージの持つ特殊効果
	public void setEffect(String e){
		effect = e;
	}
	public String getEffect(){
		return effect;
	}
	
	/**
	 * このポイントがeffectエリアに入ってるかどうか。
	 * @param p
	 * @return
	 */
	public boolean isIncluded(Point3d p){
	if(isIncluded(p.x, p1.x, p2.x) && isIncluded(p.y, p1.y, p2.y) && isIncluded(p.z, p1.z, p2.z)){
		return true;
	}
	else{
		return false;}	
	}
	
	/**
	 * xがp1とp2の間に入ってるかどうか。
	 * @param x
	 * @param p1
	 * @param p2
	 * @return
	 */
	private boolean isIncluded(double x, double p1, double p2) {
		if((x-p1)*(x-p2) <= 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
