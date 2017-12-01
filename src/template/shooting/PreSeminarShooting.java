package template.shooting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.Texture;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.image.TextureLoader;

import framework.AI.GeometryGraph;
import framework.AI.IState;
import framework.AI.Location;
import framework.RWT.RWTBoard;
import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTLabel;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.gameMain.SimpleGame;
import framework.model3D.BackgroundBox;
import framework.model3D.CollisionResult;
import framework.model3D.GeometryCollector;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Velocity3D;
import framework.view3D.Camera3D;

/**
 * �V���[�e�B���O�Q�[���̂��߂̃e���v���[�g
 * @author �V�c����
 *
 */
public class PreSeminarShooting extends SimpleBattleGame {
	private boolean bShot = false;
	private RWTContainer container;						// �v���C��ʂ̃R���e�i
	private RWTLabel gameOver = null;					// �Q�[���I�[�o�[�\���p
	private RWTLabel scoreDisplay = null;				// �X�R�A�\���p
	private int score = 0;								// �X�R�A
	
	@Override
	public void init(BattleField field, Camera3D camera) {
		// ����
		AmbientLight amblight = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));

		amblight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		field.placeLight(amblight);

		// ���s����
		DirectionalLight dirlight = new DirectionalLight(true, // ����ON/OFF
				new Color3f(1.0f, 1.0f, 1.0f), // ���̐F
				new Vector3f(0.0f, -1.0f, -0.5f) // ���̕����x�N�g��
		);
		dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		field.placeLight(dirlight);

		// �L�����N�^�[��3D�f�[�^��ǂݍ��ݔz�u����
		Object3D pochaBody = ModelFactory.loadModel("data\\pocha\\pocha.wrl").createObject();
		Animation3D pochaAnimation = AnimationFactory.loadAnimation("data\\pocha\\walk.wrl");
		Player player = new Player(pochaBody, pochaAnimation);
		player.setPosition(new Position3D(120.0, 30.0, 30.0));
		player.setDirection(new Vector3d(0.0, 0.0, 1.0));
		field.place(player);

		// �X�e�[�W��3D�f�[�^��ǂݍ��ݔz�u����
		Object3D stageObj = ModelFactory.loadModel("data\\Neuschwanstein\\Neuschwanstein.wrl").createObject();
		Ground stage = new Ground(stageObj);
		field.place(stage);
		
		// �w�i���쐬����
		buildSkyBox(field);
		
		// �J�����̐ݒ�i�O�l�̎��_�ɂ���j
		camera.addTarget(player);
		camera.setCameraBack(new Vector3d(0.0, 4.0, 7.0));
		camera.setViewLine(player.getDirection());
		camera.setFieldOfView(1.5);
	}

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D f = new RWTFrame3D();
		f.setSize(800, 600);
		f.setTitle("Pre-Seminar Shooting Game");
		f.setMouseCapture(true);
		return f;
	}
	
	protected RWTContainer createRWTContainer() {
		container = new RWTContainer() {
				@Override
				public void build(GraphicsConfiguration gc) {				
					RWTCanvas3D canvas;
					if (gc != null) {
						canvas = new RWTCanvas3D(gc, true);
					} else {
						canvas = new RWTCanvas3D(true);
					}
					canvas.setRelativePosition(0.0f, 0.0f);
					canvas.setRelativeSize(1.0f, 1.0f);
					gameOver = new RWTLabel(0.25f, 0.5f, "Game Over!!", Color.RED, new Font("", Font.ITALIC, 36));
					gameOver.setVisible(false);
					canvas.addWidget(gameOver);
					RWTBoard scoreBoard = new RWTBoard(0.72f, 0.035f, 0.27f, 0.08f, new Color(1.0f, 1.0f, 1.0f, 0.3f));
					scoreBoard.setVisible(true);
					canvas.addWidget(scoreBoard);
					scoreDisplay = new RWTLabel(0.75f, 0.1f, "Score: " + score, Color.WHITE, new Font("", Font.PLAIN, 20));
					scoreDisplay.setVisible(true);
					canvas.addWidget(scoreDisplay);
					addCanvas(canvas);
					repaint();
				}
				// RWT���ł̓C�x���g���������Ȃ�
				@Override
				public void keyPressed(RWTVirtualKey key) {}
				@Override
				public void keyReleased(RWTVirtualKey key) {}
				@Override
				public void keyTyped(RWTVirtualKey key) {}
		};
		return container;
	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
		// ������ �ȉ��ɃL�[����̃v���O���������� ������
		
		// ���̂��ׂĂ̓o�ꕨ�i�v���C���[��G�Ȃǁj����Ăɓ�����
		battleField.motion(interval);
		
		// ������ �Q�[���I�[�o�[���ۂ��𔻒肷�� ������
		
		// ������ �_���v�Z ������
		
		// �J�����̉�]
		camera.setViewLine(battleField.getPlayerDirection());
	}
	
	/**
	 * �Q�[���̃��C��
	 * @param args
	 */
	public static void main(String[] args) {
		PreSeminarShooting game = new PreSeminarShooting();
		game.setFramePolicy(5, 33, false);
		game.start();		
	}

	/**
	 * �w�i���쐬����
	 * @param field
	 */
	private void buildSkyBox(BattleField field) {
		TextureLoader loaderTop = new TextureLoader("data\\sky.jpg", 
				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, 
				null);
		Texture textureTop = loaderTop.getTexture();
		TextureLoader loaderBottom = new TextureLoader("data\\sky.jpg", 
				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, 
				null);
		Texture textureBottom = loaderBottom.getTexture();
		TextureLoader loaderNorth = new TextureLoader("data\\sky.jpg", 
				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, 
				null);
		Texture textureNorth = loaderNorth.getTexture();
		TextureLoader loaderSouth = new TextureLoader("data\\sky.jpg", 
				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, 
				null);
		Texture textureSouth = loaderSouth.getTexture();
		TextureLoader loaderWest = new TextureLoader("data\\sky.jpg", 
				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, 
				null);
		Texture textureWest = loaderWest.getTexture();
		TextureLoader loaderEast = new TextureLoader("data\\sky.jpg", 
				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, 
				null);
		Texture textureEast = loaderEast.getTexture();
		
		BackgroundBox background = new BackgroundBox(textureNorth, textureWest, 
				textureSouth, textureEast, textureTop, textureBottom);
		BoundingSphere bs = new BoundingSphere();
		bs.setRadius(1000);
		background.setApplicationBounds(bs);
		field.place(background);
	}
}
