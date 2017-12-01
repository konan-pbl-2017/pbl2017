package framework.physics;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;

import framework.model3D.BaseObject3D;
import framework.model3D.BoundingSurface;
import framework.model3D.Object3D;
import framework.model3D.Placeable;


/**
 * 地面などの（基本的に動かない）構造物を表すオブジェクト
 * @author 新田直也
 *
 */
public class Ground implements Placeable {
	private BaseObject3D groundObj = null;
	private BoundingSurface boundingSurface = null;		// 衝突判定用ボリュームのキャッシュ
	
	public Ground(BaseObject3D obj) {
		groundObj = obj;
	}
	
	public BaseObject3D getBody() {
		return groundObj;
	}
	

	@Override
	public TransformGroup getTransformGroupToPlace() {
		return groundObj.getTransformGroupToPlace();
	}

	BoundingSurface getBoundingSurface() {
		if (boundingSurface == null) {
			// キャッシュに何も積まれていない場合
			BoundingSurfaceVisitor surfaceVisitor = new BoundingSurfaceVisitor();
			if (groundObj instanceof Object3D) {
				((Object3D)groundObj).accept(surfaceVisitor);
			} else {
				surfaceVisitor.baseVisit(groundObj);
			}
			boundingSurface = surfaceVisitor.getBoundingSurface();
		}
		return boundingSurface;
	}
}
