package framework.physics;
import javax.vecmath.Vector3d;

import framework.model3D.OBB;


public class Inertia3D {
	double ixx;
	double iyy;
	double izz;
	static final Inertia3D ZERO = new Inertia3D( 0.0, 0.0, 0.0);
	
	public Inertia3D(double x,double y,double z){
		this.ixx = x;
		this.iyy = y;
		this.izz = z;
	}
	
	public Inertia3D(Vector3d v) {
		this.ixx = v.x;
		this.iyy = v.y;
		this.izz = v.z;
	}

	public Inertia3D(Solid3D obj) {
		BoundingBoxVisitor visitor = new BoundingBoxVisitor();
		obj.accept(visitor);
		if (visitor.getObbList().size() == 1) {
			OBB obb = visitor.getObbList().get(0);
			Vector3d vx = new Vector3d();
			vx.sub(obb.vertexList.get(4), obb.vertexList.get(0));

			Vector3d vy = new Vector3d();
			vy.sub(obb.vertexList.get(1), obb.vertexList.get(0));

			Vector3d vz = new Vector3d();
			vz.sub(obb.vertexList.get(2), obb.vertexList.get(0));
			
//			System.out.println("vx:" + vx + ",vy:" + vy + ",vz:" + vz);
			this.ixx = 25.0 * (1.0/12.0 * (Math.pow(vy.length(), 2.0) + Math.pow(vz.length(), 2.0)));
			this.iyy = 25.0 * (1.0/12.0 * (Math.pow(vx.length(), 2.0) + Math.pow(vz.length(), 2.0)));
			this.izz = 25.0 * (1.0/12.0 * (Math.pow(vx.length(), 2.0) + Math.pow(vy.length(), 2.0)));	
			} else {
			this.ixx = 10;
			this.iyy = 10;
			this.izz = 10;			
		}
	}

	public Vector3d getVector3d() {
		return new Vector3d(ixx, iyy, izz);
	}
}

