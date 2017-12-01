package framework.physics;
import javax.vecmath.Vector3d;


public class Force3D {
	double x;
	double y;
	double z;
	public static final Force3D ZERO = new Force3D( 0.0, 0.0, 0.0);
	
	public Force3D(double x,double y,double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Force3D(Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	public Vector3d getVector3d(){
		return new Vector3d(x,y,z);
	}

	public void add(Force3D f) {
		x += f.x;
		y += f.y;
		z += f.z;
	}
	
	public double getSeverity() {
		return getVector3d().length();
	}
}
