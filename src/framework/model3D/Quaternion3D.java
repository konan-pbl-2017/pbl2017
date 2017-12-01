package framework.model3D;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;

public class Quaternion3D extends Property3D{
	private Quat4d quaternion;
	
	//コンストラクタ=============================
	public Quaternion3D(){
		AxisAngle4d aa = new AxisAngle4d(0.0, 0.0, 1.0, 0.0);
		quaternion = new Quat4d();
		quaternion.set(aa);
	}
	
	public Quaternion3D(AxisAngle4d aa){
		quaternion = new Quat4d();
		quaternion.set(aa);
	}

	public Quaternion3D(double x,double y,double z,double w){
		AxisAngle4d aa = new AxisAngle4d(x, y, z, w);
		quaternion = new Quat4d();
		quaternion.set(aa);
	}
	public Quaternion3D(Quaternion3D q){
		this.quaternion = (Quat4d)q.getQuat().clone();
	}
	public Quat4d getQuat(){
		return quaternion;
	}
	
	//ゲッタ=================================
	public double getX(){
		return quaternion.x;
	}
	public double getY(){
		return quaternion.y;
	}
	public double getZ(){
		return quaternion.z;
	}
	public double getW(){
		return quaternion.w;
	}
	public Quaternion3D getInterpolate(Quaternion3D q,double alpha){
		quaternion.interpolate(q.getQuat(), alpha);
		return this;
	}
	
	public AxisAngle4d getAxisAngle() {
		AxisAngle4d axisAngle = new AxisAngle4d();
		axisAngle.set(quaternion);
		return axisAngle;
	}
	
	//セッタ=================================
	public Quaternion3D setQuaternion(double x,double y,double z,double w){
		quaternion = new Quat4d(x, y, z, w);
		return this;
	}

	public Quaternion3D setAxisAngle(double x,double y,double z,double w){
		AxisAngle4d aa = new AxisAngle4d(x, y, z, w);
		quaternion = new Quat4d();
		quaternion.set(aa);
		return this;
	}

	public Quaternion3D add(AxisAngle4d aa){
		Quat4d q = new Quat4d();
		q.set(aa);
		quaternion.mul(q);
		return this;
	}
	
	@Override
	public void applyTo(Object3D o) {
		// TODO Auto-generated method stub
		o.setQuaternion(this);
	}
	
	@Override
	public Property3D clone() {
		// TODO Auto-generated method stub
		return new Quaternion3D(this);
	}

	public double norm() {
        return quaternion.w * quaternion.w + quaternion.x * quaternion.x + quaternion.y * quaternion.y + quaternion.z * quaternion.z;
	}

	public Quaternion3D normalize() {
		quaternion.normalize();
		return this;
	}
}
