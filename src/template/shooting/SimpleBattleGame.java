package template.shooting;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.GraphicsConfigTemplate3D;

import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.gameMain.SimpleGame;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

/**
 * �v���C���[�ƓG�������Q�[���i�A�N�V�����Q�[����V���[�e�B���O�Q�[���j�̊�{�N���X
 * @author �V�c����
 *
 */
abstract public class SimpleBattleGame extends SimpleGame {	
	BattleField battleField;
	
	@Override
	public void init(Universe universe, Camera3D camera) {
		battleField = new BattleField(universe);		
		init(battleField, camera);
	}
	
	abstract public void init(BattleField battleField, Camera3D camera);
}
