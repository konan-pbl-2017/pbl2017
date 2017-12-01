package template.racing;

import java.util.ArrayList;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import framework.animation.Animation3D;
import framework.gameMain.OvergroundActor;
import framework.model3D.Position3D;
import framework.physics.AngularVelocity3D;
import framework.physics.Force3D;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;
import framework.physics.Velocity3D;

public class Automobile extends OvergroundActor {
	double frontWheelRot; // �O�֊p�x
	double maxSpeed = 90.0;		// �ō����x(m/s)
	double maxSteering = Math.PI * 0.25;	// �ő�Ǌp
	double mass = 1100;			// �ԏd

	static final double halfWheelbase = 2.45 / 2.0;	// �z�C�[���x�[�X
	static final double a = 8041.0; // �쓮��
	static final double b = 88.9; // ���������x�i127�~���C�W���j
	static final double b2 = 10.0; // ���R�����W��
	static final double s = 3.0; // �T�C�h�t�H�[�X�ő�Î~���C�W��
	static final double s2 = 0.8; // �T�C�h�t�H�[�X�����C�W��
	static final Vector3d eX = new Vector3d(1.0, 0.0, 0.0);
	static final Vector3d eY = new Vector3d(0.0, 1.0, 0.0);

	ArrayList<Force3D> forces = new ArrayList<Force3D>();
	ArrayList<Position3D> appPoints = new ArrayList<Position3D>();

	public static final double EPSILON = 2.220446049250313E-16d;

	public Automobile(Solid3D body, Animation3D animation) {
		super(body, animation);
		body.setMass(mass); // �ԏd1t
		setInitialDirection(eX);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
	}

	/**
	 * �A�N�Z���𓥂�ŉ�������
	 */
	public void accelrate() {
		// �쓮�͂̌v�Z�i�O�ւ������Ă�������ɉ�������j
		if (getFrontWheelVelocity().getVector3d().length() > maxSpeed) return;	// �ō����x�𒴂����ꍇ
		Vector3d frontWheelDir = getFrontWheelDir();	// �O�ւ̕����x�N�g��
		frontWheelDir.scale(a);
		forces.set(0, new Force3D(frontWheelDir));

		// �O�ւ̈ʒu�i�쓮�͂̍�p�_�ɂȂ�j
		appPoints.set(0, getFrontPosition());
	}

	/**
	 * �u���[�L�𓥂�Ō�������
	 */
	public void brake() {
		// �O�ւ̐����͂̌v�Z
		Vector3d frontWheelDir = getFrontWheelDir();	// �O�ւ̕����x�N�g��
		double dir = frontWheelDir.dot(getFrontWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		frontWheelDir.scale(-b * dir * ((Solid3D)body).getMass());
		forces.set(3, new Force3D(frontWheelDir));

		// �O�ւ̈ʒu�i�O�ւ̐����͂̍�p�_�ɂȂ�j
		appPoints.set(3, getFrontPosition());

		// ��ւ̐����͂̌v�Z
		Vector3d rearWheelDir = getDirection(); // ��ւ̕����x�N�g��
		dir = rearWheelDir.dot(getRearWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		rearWheelDir.scale(-b * dir * ((Solid3D)body).getMass());
		forces.set(4, new Force3D(rearWheelDir));

		// ��ւ̈ʒu�i��ւ̐����͂̍�p�_�ɂȂ�j
		appPoints.set(4, getRearPosition());
	}

	/**
	 * �A�N�Z�����u���[�L�������ăG���W���u���[�L���|����
	 * @param interval
	 */
	public void engineBrake(long interval) {
		// �O�ւ̐����͂̌v�Z
		Vector3d frontWheelDir = getFrontWheelDir();	// �O�ւ̕����x�N�g��
		double dir = frontWheelDir.dot(getFrontWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		frontWheelDir.scale(-b2 * dir * ((Solid3D)body).getMass());
		forces.set(3, new Force3D(frontWheelDir));

		// �O�ւ̈ʒu�i�O�ւ̐����͂̍�p�_�ɂȂ�j
		appPoints.set(3, getFrontPosition());

		// ��ւ̐����͂̌v�Z
		Vector3d rearWheelDir = getDirection(); // ��ւ̕����x�N�g��
		dir = rearWheelDir.dot(getRearWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		rearWheelDir.scale(-b2 * dir * ((Solid3D)body).getMass());
		forces.set(4, new Force3D(rearWheelDir));

		// ��ւ̈ʒu�i��ւ̐����͂̍�p�_�ɂȂ�j
		appPoints.set(4, getRearPosition());
	}

	/**
	 * �n���h�������ɐ؂�
	 * @param interval
	 */
	public void turnLeft(long interval) {
		if (frontWheelRot <= -maxSteering) {
			// �ő�Ǌp�𒴂���
			frontWheelRot = -maxSteering;
			return;
		}
		frontWheelRot -= 4.0 * (double) (interval / 1000.0); // �O�ւ̉�]�p�x

	}

	/**
	 * �n���h�����E�ɐ؂�
	 * @param interval
	 */
	public void turnRight(long interval) {
		if (frontWheelRot >= maxSteering) {
			// �ő�Ǌp�𒴂���
			frontWheelRot = maxSteering;
			return;
		}
		frontWheelRot += 4.0 * (double) (interval / 1000.0); // �O�ւ̉�]�p�x
	}

	/**
	 * �n���h���𗣂��Ď��R�Ɍ��̈ʒu�ɕ���������
	 * @param interval
	 */
	public void steeringSelfCentering(long interval) {
		if (frontWheelRot > EPSILON) {
			frontWheelRot -= 2.0 * (double) (interval / 1000.0);

		} else if (frontWheelRot < -EPSILON) {
			frontWheelRot += 2.0 * (double) (interval / 1000.0);

		} else {
			frontWheelRot = 0.0;
		}
	}

	public void motion(long interval, Ground ground) {
		if (interval == 0L) return;
		
		// �O�ւɂ��T�C�h�t�H�[�X�̌v�Z
		Vector3d frontWheelDir = getFrontWheelDir();	// �O�ւ̕����x�N�g��
		Vector3d frontWheelSideDir = new Vector3d();
		frontWheelSideDir.cross(eY, frontWheelDir);			// �O�ւ̉������x�N�g��
		Vector3d frontWheelVelocity = getFrontWheelVelocity().getVector3d();
		double frontWheelSideSlip = frontWheelSideDir.dot(frontWheelVelocity);	// �O�ւ̉����葬�x
		double omega = ((Solid3D)body).getAngularVelocity().getVector3d().dot(eY);	// �p���x
		frontWheelSideSlip -= frontWheelDir.dot(frontWheelVelocity) * Math.tan(omega / 2.0 * ((double)interval / 1000.0));	// �ԑ̂̉�]�ɔ����O�ւ̉�]�̌��ʂ���������
		double frontWheelSideForce;		// �O�ւ̉������̖��C��
		// �Î~���C�W���̌v�Z
		double frontWheelSideAccel = frontWheelSideSlip / ((double)interval / 1000.0);	// �����Ă��Ȃ���Ԃ�����������Ɖ��肷��
		if (frontWheelSideAccel > s * PhysicsUtility.GRAVITY) {
			// �ő�Î~���C�W���𒴂����ꍇ�A�����C�͂̌v�Z�ɐ؂�ւ�
				frontWheelSideForce = -s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("�O�֗\�������葬�x(�����C�ɐ؂�ւ�):" + frontWheelSideAccel);
		} else if (frontWheelSideAccel < -s * PhysicsUtility.GRAVITY) {
			// �ő�Î~���C�W���𒴂����ꍇ�A�����C�͂̌v�Z�ɐ؂�ւ�
				frontWheelSideForce = s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("�O�֗\�������葬�x(�����C�ɐ؂�ւ�):" + frontWheelSideAccel);
		} else {
			// �ő�Î~���C�W���ȓ��̏ꍇ�i�������̑��x��ł������j
			frontWheelSideForce = -frontWheelSideAccel * ((Solid3D)body).getMass();
//System.out.println("�O�֗\�������葬�x:" + frontWheelSideAccel);
		}
		frontWheelSideDir.scale(frontWheelSideForce);
		forces.set(1, new Force3D(frontWheelSideDir));

		// �O�ւ̈ʒu�i�O�ւɑ΂���T�C�h�t�H�[�X�̍�p�_�ɂȂ�j
		appPoints.set(1, getFrontPosition());

		// ��ւɂ��T�C�h�t�H�[�X�̌v�Z
		Vector3d rearWheelDir = getDirection(); // ��ւ̕����x�N�g��
		Vector3d rearWheelSideDir = new Vector3d();
		rearWheelSideDir.cross(eY, rearWheelDir); // ��ւ̉������x�N�g��
		Vector3d rearWheelVelocity = getRearWheelVelocity().getVector3d();
		double rearWheelSideSlip = rearWheelSideDir.dot(rearWheelVelocity);	// ��ւ̉����葬�x
		rearWheelSideSlip -= rearWheelDir.dot(rearWheelVelocity) * Math.tan(omega / 2.0 * ((double)interval / 1000.0));	// �ԑ̂̉�]�ɔ�����ւ̉�]�̌��ʂ���������
		double rearWheelSideForce;		// ��ւ̉������̖��C��
		// �Î~���C�W���̌v�Z
		double rearWheelSideAccel = rearWheelSideSlip / ((double)interval / 1000.0);	// �����Ă��Ȃ���Ԃ�����������Ɖ��肷��
		if (rearWheelSideAccel > s * PhysicsUtility.GRAVITY) {
			// �ő�Î~���C�W���𒴂����ꍇ�A�����C�͂̌v�Z�ɐ؂�ւ�
				rearWheelSideForce = -s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("��֗\�������葬�x(�����C�ɐ؂�ւ�):" + rearWheelSideAccel);
		} else if (rearWheelSideAccel < -s * PhysicsUtility.GRAVITY) {
			// �ő�Î~���C�W���𒴂����ꍇ�A�����C�͂̌v�Z�ɐ؂�ւ�
				rearWheelSideForce = s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("��֗\�������葬�x(�����C�ɐ؂�ւ�):" + rearWheelSideAccel);
		} else {
			// �ő�Î~���C�W���ȓ��̏ꍇ�i�������̑��x��ł������j
			rearWheelSideForce = -rearWheelSideAccel * ((Solid3D)body).getMass();
//System.out.println("��֗\�������葬�x:" + rearWheelSideAccel);
		}
		rearWheelSideDir.scale(rearWheelSideForce);
		forces.set(2, new Force3D(rearWheelSideDir));

		// ��ւ̈ʒu�i��ւɑ΂���T�C�h�t�H�[�X�̍�p�_�ɂȂ�j
		appPoints.set(2, getRearPosition());
		
//System.out.println(frontWheelRot);

		// �������Z�A�Փ˔���A�Փˉ���
		super.motion(interval, ground, forces, appPoints);

		// �v�Z���I�������N���A������ɔ�����
		forces.clear();
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		appPoints.clear();
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
	}

	/**
	 * �O�ւ̈ʒu�����߂�
	 * @return �O�ւ̈ʒu
	 */
	private Position3D getFrontPosition() {
		Vector3d front = ((Solid3D) body).getGravityCenter().getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(halfWheelbase);
		front.add(direction);
		return new Position3D(front);
	}

	/**
	 * ��ւ̈ʒu�����߂�
	 * @return ��ւ̈ʒu
	 */
	private Position3D getRearPosition() {
		Vector3d rear = ((Solid3D) body).getGravityCenter().getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(-halfWheelbase);
		rear.add(direction);
		return new Position3D(rear);
	}

	/**
	 * �O�ւ̕����x�N�g�������߂�
	 * @return �O�ւ̕����x�N�g��
	 */
	private Vector3d getFrontWheelDir() {
		Vector3d ftV;
		ftV = getDirection();
		Transform3D trans = new Transform3D();
		trans.rotY(-frontWheelRot);
		trans.transform(ftV);
		return ftV;
	}
	
	/**
	 * �O�ւ̑��x�x�N�g�������߂�
	 * @return �O�ւ̑��x�x�N�g��
	 */
	private Velocity3D getFrontWheelVelocity() {
		Velocity3D velocity = ((Solid3D) body).getVelocity();
		Vector3d angularVelocity = ((Solid3D) body).getAngularVelocity()
				.getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(halfWheelbase);
		angularVelocity.cross(angularVelocity, direction); // �p���x�x�N�g���ƒ��S����̈ʒu�x�N�g���̊O�ς����]�ɂ�鑊�Α��x�ɂȂ�
		return velocity.add(angularVelocity);
	}

	/**
	 * ��ւ̑��x�x�N�g�������߂�
	 * @return ��ւ̑��x�x�N�g��
	 */
	private Velocity3D getRearWheelVelocity() {
		Velocity3D velocity = ((Solid3D) body).getVelocity();
		Vector3d angularVelocity = ((Solid3D) body).getAngularVelocity()
				.getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(-halfWheelbase);
		angularVelocity.cross(angularVelocity, direction); // �p���x�x�N�g���ƒ��S����̈ʒu�x�N�g���̊O�ς����]�ɂ�鑊�Α��x�ɂȂ�
		return velocity.add(angularVelocity);
	}
}
