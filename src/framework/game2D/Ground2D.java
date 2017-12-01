package framework.game2D;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import framework.AI.GeometryGraph;
import framework.gameMain.ActorModel;
import framework.gameMain.GameModel;
import framework.model3D.BaseObject3D;
import framework.model3D.GeometryCollector;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Placeable;
import framework.model3D.Universe;
import framework.physics.Ground;

public class Ground2D extends GameModel implements Placeable {
	private String stageName;
	private String backImageName;
	private BufferedImage backgroundImage;
	private TransformGroup stageTrans = null;

	private Ground ground;

	Model3D stageModel = null;
	
	public Ground2D(String groundFilename, String backgroundFilename,
	int width, int height) {
		this(groundFilename, backgroundFilename, width, height, 1.0);
	}

	public Ground2D(String groundFilename, String backgroundFilename,
			int width, int height, double scale) {
		super(groundFilename);
		if (stageModel == null) {
			stageModel = getModel();
		}
		
		if (stageTrans == null) {
			stageTrans = new TransformGroup();
		}

		if (groundFilename != null) {
			// ステージの3Dデータを読み込み配置する
			Object3D stageObj = stageModel.createObject();
			stageObj.scale(scale);
			ground = new Ground(stageObj);
			stageTrans.addChild(ground.getTransformGroupToPlace());
		}

		if (backgroundFilename != null) {
			// BackGroundのデータを読み込み配置する
			try {
				backgroundImage = ImageIO.read(new File(backgroundFilename));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Dimension dim = new Dimension(width,height);
			double scalew = (double) dim.width / backgroundImage.getWidth();
			double scaleh = (double) dim.height / backgroundImage.getHeight();
			AffineTransformOp atOp = new AffineTransformOp(AffineTransform
					.getScaleInstance(scalew, scaleh),
					AffineTransformOp.TYPE_BILINEAR);

			BufferedImage img = new BufferedImage(dim.width, dim.height,
					backgroundImage.getType());
			atOp.filter(backgroundImage, img);
			backgroundImage = img;

			Background background = new Background();
			ImageComponent2D imageCompornent = new ImageComponent2D(
					ImageComponent2D.FORMAT_RGB, backgroundImage);
			background.setImage(imageCompornent);
			BoundingSphere bs = new BoundingSphere();
			bs.setRadius(100);
			background.setApplicationBounds(bs);

			stageTrans.addChild(background);
		}
	}

	public Ground getGround() {
		return ground;
	}

	public void setGround(Ground ground) {
		this.ground = ground;
	}

	@Override
	public BaseObject3D getBody() {
		// TODO 自動生成されたメソッド・スタブ
		if (ground == null) return null;
		return ground.getBody();
	}

	@Override
	public TransformGroup getTransformGroupToPlace() {
		// TODO 自動生成されたメソッド・スタブ
		return stageTrans;
	}
	
}
