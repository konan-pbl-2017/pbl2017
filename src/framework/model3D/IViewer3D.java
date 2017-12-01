package framework.model3D;

import java.util.ArrayList;

import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Light;
import javax.media.j3d.Transform3D;



public interface IViewer3D {
	public abstract void update(ArrayList<Light> lights, BackgroundBox skyBox);
	public abstract void setGraphicsContext3D(GraphicsContext3D graphicsContext3D);
	public abstract void setModelTransform(Transform3D t);
	public abstract void draw(BaseObject3D obj);
}
