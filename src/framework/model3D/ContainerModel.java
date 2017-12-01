package framework.model3D;

import javax.media.j3d.Transform3D;

/**
 * 子供を持つモデル
 * @author 新田直也
 *
 */
public class ContainerModel extends Model3D {
	private Model3D[] children;
	private Transform3D defaultTransform = null;
	
	public ContainerModel(String name,Model3D[] children) {
		this.children = children;
		this.name = name;
		this.defaultTransform = null;
	}
	
	public ContainerModel(String name, Model3D[] children,
			Transform3D transform) {
		this.children = children;
		this.name = name;
		this.defaultTransform = transform;
	}

	public Object3D createObject() {
		Object3D[] objChild = new Object3D[children.length];
		for(int i = 0;i < children.length;i++){
			objChild[i] = (Object3D) children[i].createObject(); 
		}
		return new Object3D(name, objChild, defaultTransform);
	}
	
	public Object3D createObjectSharingAppearance() {
		Object3D[] objChild = new Object3D[children.length];
		for(int i = 0;i < children.length;i++){
			objChild[i] = (Object3D) children[i].createObjectSharingAppearance(); 
		}
		return new Object3D(name, objChild, defaultTransform);
	}
}
