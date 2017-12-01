package framework.animation;
import javax.media.j3d.Transform3D;

import framework.model3D.Object3D;

/**
 * ‰Šúó‘Ô‚Ìp¨
 * @author V“c’¼–ç
 *
 */
public class DefaultPose3D extends Pose3D {
	public void applyTo(Object3D obj) {
		obj.scale.setTransform(new Transform3D());
		for (int n = 0; n < obj.children.length; n++) {
			subApplyTo((Object3D)obj.children[n]);
		}			
	}
	
	private void subApplyTo(Object3D obj) {
		obj.pos.setTransform(new Transform3D());
		obj.rot.setTransform(new Transform3D());
		obj.scale.setTransform(new Transform3D());
		for (int n = 0; n < obj.children.length; n++) {
			subApplyTo((Object3D)obj.children[n]);
		}					
	}
}
