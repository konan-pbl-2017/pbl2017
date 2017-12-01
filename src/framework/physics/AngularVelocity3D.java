package framework.physics;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import framework.model3D.Object3D;
import framework.model3D.Property3D;


public class AngularVelocity3D extends Property3D{
	private double x;
	private double y;
	private double z;

	public AngularVelocity3D(AngularVelocity3D w) {
		x = w.x;
		y = w.y;
		z = w.z;
	}
			
	public AngularVelocity3D(double x,double y,double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public AngularVelocity3D(){
		x = 0.0;
		y = 0.0;
		z = 0.0;	
	}
	
	public void applyTo(Object3D o){
		((Solid3D)o).setAngularVelocity(this);
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
	
	public AngularVelocity3D add(double x, double y, double z){
		this.x += x; 
		this.y += y;
		this.z += z;
		return this;
	}

	public AngularVelocity3D add(Vector3d v) {
		// TODO Auto-generated method stub
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public Vector3d getVector3d(){
		return new Vector3d(x,y,z);
	}
	
	public AxisAngle4d getAxisAngle4d() {
		double l = getVector3d().length();
		if (l <= 0.00001) {
			return new AxisAngle4d(0, 0, 1.0, 0.0);			
		}
		return new AxisAngle4d(x / l, y / l, z / l, l);
	}

	@Override
	public Property3D clone() {
		// TODO Auto-generated method stub
		return new AngularVelocity3D(this);
	}	
}


