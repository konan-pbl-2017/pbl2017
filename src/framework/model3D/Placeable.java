package framework.model3D;

import javax.media.j3d.TransformGroup;

/**
 * 配置できるもの全て
 * @author 新田直也
 *
 */
public interface Placeable {
	abstract TransformGroup getTransformGroupToPlace();
	abstract BaseObject3D getBody();
}
