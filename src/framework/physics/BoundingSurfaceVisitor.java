package framework.physics;

import java.util.ArrayList;

import javax.media.j3d.Transform3D;

import framework.model3D.BaseObject3D;
import framework.model3D.BoundingSurface;
import framework.model3D.Object3D;
import framework.model3D.ObjectVisitor;

public class BoundingSurfaceVisitor extends ObjectVisitor {
	private ArrayList<BoundingSurface> boundingSurfaceList = new ArrayList<BoundingSurface>();
	public BoundingSurfaceVisitor() {
		boundingSurfaceList.add(new BoundingSurface());
	}

	public void preVisit(Object3D obj) {
		pushTransform(obj);
		if (obj.hasChildren()) {
			boundingSurfaceList.add(new BoundingSurface());
		}
	}

	public void postVisit(Object3D obj) {
		if (!obj.hasChildren()) {
			// ótÇÃèÍçá
			BoundingSurface[] s = (BoundingSurface[]) obj.getBoundingSurfaces().clone();
			for (int i = 0; i < s.length; i++) {
				s[i] = (BoundingSurface) s[i].clone();
				for (int j = stackList.size() - 1; j >= 0; j--) {
					s[i].transform(stackList.get(j));
				}
				boundingSurfaceList.get(boundingSurfaceList.size() - 1).addChild(s[i], true); // Transform3DÇìKâûÇ≥ÇπÇΩBoundsÇsurfaceListÇ…í«â¡
			}
		} else {
			BoundingSurface child = boundingSurfaceList.remove(boundingSurfaceList.size() - 1);
			BoundingSurface parent = boundingSurfaceList.get(boundingSurfaceList.size() - 1);
			parent.addChild(child, true);
		}
		popTransform();
	}

	public void baseVisit(BaseObject3D obj) {
		BoundingSurface parent = boundingSurfaceList.get(boundingSurfaceList.size() - 1);
		BoundingSurface[] s = (BoundingSurface[]) obj.getBoundingSurfaces().clone();
		for (int i = 0; i < s.length; i++) {
			s[i] = (BoundingSurface) s[i].clone();
			parent.addChild(s[i], true);
		}
	}

	public BoundingSurface getBoundingSurface() {
		return boundingSurfaceList.get(0);
	}
}
