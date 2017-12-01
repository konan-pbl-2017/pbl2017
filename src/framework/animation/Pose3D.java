package framework.animation;

import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Primitive;

import framework.model3D.Object3D;
import framework.model3D.ObjectVisitor;
import framework.model3D.Position3D;
import framework.model3D.Property3D;
import framework.model3D.Quaternion3D;

/**
 * 階層化されたオブジェクトの姿勢を指定する（各部の位置、向き、テクスチャを指定可能）
 * @author 新田直也
 *
 */
public class Pose3D extends Property3D {
	private ArrayList<PartPose> partPoseList = new ArrayList<PartPose>();

	public Pose3D() {
	}

	public Pose3D(Pose3D p) {
		partPoseList = new ArrayList(p.partPoseList);
	}

	public void applyTo(Object3D obj) {
		// obj.rotate(part, vx, vy, vz, a);
		int n = partPoseList.size();
		for (int i = 0; i < n; i++) {
			final PartPose partpose = (PartPose) partPoseList.get(i);
			Object3D partObj = obj.getPart(partpose.name);
			if (partObj != null) {
				// 位置
				if (partpose.position != null)
					partObj.apply(partpose.position, false);
				// 向き
				if (partpose.quaternion != null)
					partObj.apply(partpose.quaternion, false);
				// テクスチャ
				if (partpose.texture != null 
						|| partpose.texturePosition != null 
						|| partpose.textureQuaternion != null) {
					ObjectVisitor v = new ObjectVisitor() {
						@Override
						public void postVisit(Object3D obj) {
							Appearance appearance = null;
							Node node = obj.getPrimitiveNode();
							if (node != null && node instanceof Shape3D) {
								appearance = ((Shape3D)node).getAppearance();
							} else if (node != null && node instanceof Primitive) {
								appearance = ((Primitive)node).getAppearance();								
							}
							if (appearance != null) {
								TextureUnitState tu = null;
								if (partpose.texture != null) {
									if (partpose.textureUnit == PartAnimation.SINGLE_TEXTURE) {
										appearance.setTexture(partpose.texture);
									} else {
										tu = appearance.getTextureUnitState(partpose.textureUnit);
										tu.setTexture(partpose.texture);
										appearance.setTextureUnitState(partpose.textureUnit, tu);
									}
								}
								if ((partpose.texturePosition != null || partpose.textureQuaternion != null) 
										&& obj.hasAppearancePrepared()) {	// Appearance が初回のレンダリングの直前で更新される可能性があるため
									TextureAttributes ta;
									if (partpose.textureUnit == PartAnimation.SINGLE_TEXTURE) {
										ta = appearance.getTextureAttributes();
									} else {
										tu = appearance.getTextureUnitState(partpose.textureUnit);
										ta = tu.getTextureAttributes();
									}
									if (ta == null) {
										ta = new TextureAttributes();
										ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_READ);
										ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);
									}
									Transform3D t = new Transform3D();
									if (partpose.texturePosition != null) {
										t.set(new Vector3d(partpose.texturePosition.getVector3d()));
									}
									if (partpose.textureQuaternion != null) {
										Transform3D t2 = new Transform3D();
										t2.set(partpose.textureQuaternion.getQuat());
										t.mul(t2);
									}
//									shape.getAppearance().getTextureAttributes().setTextureTransform(t);
									ta.setTextureTransform(t);
									if (partpose.textureUnit == PartAnimation.SINGLE_TEXTURE) {
										appearance.setTextureAttributes(ta);
									} else {
										tu.setTextureAttributes(ta);
										appearance.setTextureUnitState(partpose.textureUnit, tu);
									}
								}
							}
						}
						@Override
						public void preVisit(Object3D obj) {
						}
					};
					partObj.accept(v);
				}
			}
		}
	}

	public void addPose(String name, Position3D p, Quaternion3D q, Texture texture, Position3D tp, Quaternion3D tq, int textureUnit) {
		PartPose partpose = new PartPose(name, p, q, texture, tp, tq, textureUnit);
		partPoseList.add(partpose);
	}

	@Override
	public Property3D clone() {
		// TODO Auto-generated method stub
		return new Pose3D(this);
	}
}
