package framework.animation;

import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.View;

import com.sun.j3d.utils.universe.SimpleUniverse;

import cv97.SceneGraph;
import cv97.field.MFFloat;
import cv97.field.MFVec3f;
import cv97.j3d.AppearanceNodeObject;
import cv97.j3d.IndexedFaceSetNodeObject;
import cv97.j3d.SceneGraphJ3dObject;
import cv97.node.AppearanceNode;
import cv97.node.BackgroundNode;
import cv97.node.IndexedFaceSetNode;
import cv97.node.Node;
import cv97.node.NodeObject;
import cv97.node.OrientationInterpolatorNode;
import cv97.node.PositionInterpolatorNode;
import cv97.node.ShapeNode;
import cv97.node.SpotLightNode;
import cv97.node.TransformNode;
import cv97.util.LinkedListNode;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;
import framework.schedule.ScheduleManager;
import framework.schedule.TaskController;

public class AnimationFactory {
	private static Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());

	public static Animation3D loadAnimation(String fileName){
			
			SceneGraph sg = new SceneGraph(SceneGraph.NORMAL_GENERATION);
			
			// ファイル読み込み時にレンダリングが行われるので、オフスクリーンレンダリングを禁止する
			TaskController renderingController = ScheduleManager.getInstance().getController("rendering");
			renderingController.waitForActivation();
			renderingController.activate();
			
			// ファイル読み込み
			View view = canvas.getView();
			if (view != null) view.removeCanvas3D(canvas);
	 		SceneGraphJ3dObject sgObject = new SceneGraphJ3dObject(canvas, sg);
			sg.setObject(sgObject);
			sg.load(fileName);
			
			// オフスクリーンレンダリングの許可
			renderingController.deactivate();
			
			// Animation3Dへの変換
			Animation3D animation= loadAnimation(sg);
			
			return animation;	
		}

	private static Animation3D loadAnimation(LinkedListNode node){
		Animation3D mergedAnimation = null;
		System.out.println(node.getClass());
		
		if( ! (node instanceof PositionInterpolatorNode || node instanceof OrientationInterpolatorNode) ){
			Node childNodes;
			String name = "";
			if (node instanceof SceneGraph) {
				childNodes = ((SceneGraph)node).getNodes();
			} else {
				childNodes = ((Node)node).getChildNodes();
				name = ((Node)node).getName();				
			}
			if(childNodes != null){
				
				for(Node n = childNodes;n != null;n = n.next()){
					Animation3D animation;
					if((animation = loadAnimation(n)) != null){
						if (mergedAnimation != null) {
							mergedAnimation.merge(animation);
						} else {
							mergedAnimation = animation;							
						}
					}
					
					//兄弟で無限ループにならないようにする処理
					Node dummy = n;
					dummy = dummy.next();
					if(n == dummy){
						break;
					}
				}
				
//				System.out.println("子供の数は"+ list.size());
				
				if(mergedAnimation != null){
					return mergedAnimation;
				}
			}
			return null;
		} else if (node instanceof PositionInterpolatorNode) {	
			
			String name = ((PositionInterpolatorNode)node).getName();
			PositionInterpolatorNode pi = (PositionInterpolatorNode)node;
			
			//アニメーションデータの取得
			int i=0;
			PartAnimation pa = new PartAnimation(name);
			for(i=0;i<pi.getNKeys();i++){
				pa.add((long)(pi.getKey(i)*1000),new Position3D(pi.getKeyValue(i)[0], pi.getKeyValue(i)[1], pi.getKeyValue(i)[2]));
			}
			
			//アニメーションの作成
			Animation3D animation = new Animation3D();
			animation.addPartAnimation(pa);
			
			return animation;
		} else if (node instanceof OrientationInterpolatorNode) {
			
			String name = ((OrientationInterpolatorNode)node).getName();
			OrientationInterpolatorNode oi = (OrientationInterpolatorNode)node;
			
			//アニメーションデータの取得
			int i=0;
			PartAnimation pa = new PartAnimation(name);
			for(i=0;i<oi.getNKeys();i++){
				pa.add((long)(oi.getKey(i) * 1000),new Quaternion3D(oi.getKeyValue(i)[0], oi.getKeyValue(i)[1], oi.getKeyValue(i)[2], oi.getKeyValue(i)[3]));
			}
			
			//アニメーションの作成
			Animation3D animation = new Animation3D();
			animation.addPartAnimation(pa);
			
			return animation;
		}
		return null;
	}
}
