package framework.physics;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import framework.model3D.Object3D;
import framework.model3D.Property3D;


public class Velocity3D extends Property3D{
	private double x;
	private double y;
	private double z;

	public Velocity3D(Velocity3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
			
	public Velocity3D(Vector3d v){
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public Velocity3D(double x,double y,double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Velocity3D(){
		x = 0.0;
		y = 0.0;
		z = 0.0;	
	}
	public void applyTo(Object3D o){
		((Solid3D)o).setVelocity(this);
	}
	public double getX() {
		// TODO Auto-generated method stub
		return this.x;
	}
	public double getY() {
		// TODO Auto-generated method stub
		return this.y;
	}
	public double getZ() {
		// TODO Auto-generated method stub
		return this.z;
	}
	
	public Velocity3D setX(double x) {
		// TODO Auto-generated method stub
		this.x = x;
		return this;
	}
	
	public Velocity3D setY(double y) {
		// TODO Auto-generated method stub
		this.y = y;
		return this;
	}

	public Velocity3D setZ(double z) {
		// TODO Auto-generated method stub
		this.z = z;
		return this;
	}

	public Velocity3D add(double x, double y, double z){
		this.x += x; 
		this.y += y;
		this.z += z;
		return this;
	}

	public Velocity3D add(Vector3d v) {
		// TODO Auto-generated method stub
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public Velocity3D setVector3d(Vector3d v) {
		x = v.x;
		y = v.y;
		z = v.z;
		return this;
	}	
	
	public Vector3d getVector3d() {
		return new Vector3d(x,y,z);
	}
	
	@Override
	public Property3D clone() {
		// TODO Auto-generated method stub
		return new Velocity3D(this);
	}

	public Velocity3D rotX(double a) {
		Vector3d v = getVector3d();
		Transform3D rotX = new Transform3D();
		rotX.rotX(a);
		rotX.transform(v);
		setVector3d(v);
		return this;
	}
	
	public Velocity3D rotY(double a) {
		Vector3d v = getVector3d();
		Transform3D rotY = new Transform3D();
		rotY.rotY(a);
		rotY.transform(v);
		setVector3d(v);
		return this;
	}
	
	public Velocity3D rotZ(double a) {
		Vector3d v = getVector3d();
		Transform3D rotZ = new Transform3D();
		rotZ.rotZ(a);
		rotZ.transform(v);
		setVector3d(v);
		return this;
	}

	public Velocity3D setVelocity(double velocity) {
		Vector3d v = getVector3d();
		double oldV = v.length();
		v.scale(velocity / oldV);
		setVector3d(v);
		return this;
	}
}
