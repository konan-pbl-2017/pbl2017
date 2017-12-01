package framework.animation;

import javax.media.j3d.Texture;

import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;


/**
 * 部品単位の姿勢情報を保持する
 * @author 新田直也
 *
 */
public class PartPose {
	String name = null;
	Position3D position = null;
	Quaternion3D quaternion = null; 
	Texture texture = null;
	Position3D texturePosition = null;
	Quaternion3D textureQuaternion = null; 
	int textureUnit;

	public PartPose(String name, Position3D p, Quaternion3D q,
			Texture texture, Position3D tp, Quaternion3D tq, int textureUnit) {
		this.name = name;
		this.position = p;
		this.quaternion = q;
		this.texture = texture;
		this.texturePosition = tp;
		this.textureQuaternion = tq;
		this.textureUnit = textureUnit;
	}
}
