package fight3D;

import java.util.ArrayList;

import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.audio.Sound3D;
import framework.gameMain.AbstractManager;

public class CharacterManager extends AbstractManager{
	
	private static CharacterManager theInstance = null;
	
	private static String filename = "data";
	private static String characterFile = "character.def";
	
	private static final String NAME_TAG = "Name";
	private static final String MODEL_FILE_TAG = "ModelFile";
	private static final String RIGHTMOVE_ANIMATION_FILE_TAG = "RightMoveAnimationFile";
	private static final String LEFTMOVE_ANIMATION_FILE_TAG = "LeftMoveAnimationFile";

	private static final String JUMP_ANIMATION_FILE_TAG = "JumpAnimationFile";
	private static final String JUMP_SOUND_FILE_TAG = "JumpSoundFile";
	private static final String JUMP_POWER_TAG = "JumpPower";
	private static final String JUMP_FREQUENCY_TAG = "JumpFrequency";
	
	private static final String DAMAGED_ANIMATION_FILE_TAG = "DamagedAnimationFile";
	private static final String DAMAGED_SOUND_FILE_TAG = "DamagedSoundFile";

	private static final String RUN_SPEED_TAG = "RunSpeed";
	private static final String POWER_TAG = "Power";
	private static final String DEFENSE_TAG = "Defense";

	private static final String ATTACKING_PART_NAME_TAG = "AttackingPartName";
	private static final String ATTACK_ANIMATION_FILE_TAG = "AttackAnimationFile";
	private static final String ATTACK_SOUND_FILE_TAG = "AttackSoundFile";
	private static final String ATTACK_TIME_LAG_TAG = "AttackTimeLag";
	private static final String WEAPON_FILE_TAG = "WeaponFile";
	private static final String WEAPON_ANIMATION_FILE_TAG = "WeaponAnimationFile";
	private static final String WEAPON_SPEED_TAG = "WeaponSpeed";

	private static final String UPPER_ATTACKING_PART_NAME_TAG = "UpperAttackingPartName";
	private static final String UPPER_ATTACK_ANIMATION_FILE_TAG = "UpperAttackAnimationFile";
	private static final String UPPER_ATTACK_SOUND_FILE_TAG = "UpperAttackSoundFile";
	private static final String UPPER_ATTACK_TIME_LAG_TAG = "UpperAttackTimeLag";
	private static final String UPPER_WEAPON_FILE_TAG = "UpperWeaponFile";
	private static final String UPPER_WEAPON_ANIMATION_FILE_TAG = "UpperWeaponAnimationFile";
	private static final String UPPER_WEAPON_SPEED_TAG = "UpperWeaponSpeed";

	private static final String JUMP_ATTACKING_PART_NAME_TAG = "JumpAttackingPartName";
	private static final String JUMP_ATTACK_ANIMATION_FILE_TAG = "JumpAttackAnimationFile";
	private static final String JUMP_ATTACK_SOUND_FILE_TAG = "JumpAttackSoundFile";
	private static final String JUMP_ATTACK_TIME_LAG_TAG = "JumpAttackTimeLag";
	private static final String JUMP_WEAPON_FILE_TAG = "JumpWeaponFile";
	private static final String JUMP_WEAPON_ANIMATION_FILE_TAG = "JumpWeaponAnimationFile";
	private static final String JUMP_WEAPON_SPEED_TAG = "JumpWeaponSpeed";

	private static final String SIZE_TAG = "Size";
	
	private static final String COMMENT_TAG = "Comment";
	
	private ArrayList<Character> characterList = new ArrayList<Character>(); 
	
	private CharacterManager() {
		addSeekFile(characterFile);
		setTag(NAME_TAG);
		setTag(MODEL_FILE_TAG);
		setTag(RIGHTMOVE_ANIMATION_FILE_TAG);
		setTag(LEFTMOVE_ANIMATION_FILE_TAG);
		setTag(JUMP_ANIMATION_FILE_TAG);
		setTag(JUMP_SOUND_FILE_TAG);
		setTag(JUMP_POWER_TAG);
		setTag(JUMP_FREQUENCY_TAG);
		setTag(DAMAGED_ANIMATION_FILE_TAG);
		setTag(DAMAGED_SOUND_FILE_TAG);
		setTag(RUN_SPEED_TAG);
		setTag(POWER_TAG);
		setTag(DEFENSE_TAG);
		setTag(ATTACKING_PART_NAME_TAG);
		setTag(ATTACK_ANIMATION_FILE_TAG);
		setTag(ATTACK_SOUND_FILE_TAG);
		setTag(ATTACK_TIME_LAG_TAG);
		setTag(WEAPON_FILE_TAG);
		setTag(WEAPON_ANIMATION_FILE_TAG);
		setTag(WEAPON_SPEED_TAG);
		setTag(UPPER_ATTACKING_PART_NAME_TAG);
		setTag(UPPER_ATTACK_ANIMATION_FILE_TAG);
		setTag(UPPER_ATTACK_SOUND_FILE_TAG);
		setTag(UPPER_ATTACK_TIME_LAG_TAG);
		setTag(UPPER_WEAPON_FILE_TAG);
		setTag(UPPER_WEAPON_ANIMATION_FILE_TAG);
		setTag(UPPER_WEAPON_SPEED_TAG);
		setTag(JUMP_ATTACKING_PART_NAME_TAG);
		setTag(JUMP_ATTACK_ANIMATION_FILE_TAG);
		setTag(JUMP_ATTACK_SOUND_FILE_TAG);
		setTag(JUMP_ATTACK_TIME_LAG_TAG);
		setTag(JUMP_WEAPON_FILE_TAG);
		setTag(JUMP_WEAPON_ANIMATION_FILE_TAG);
		setTag(JUMP_WEAPON_SPEED_TAG);
		setTag(SIZE_TAG);
		setTag(COMMENT_TAG);
		seek(filename);
	}
	
	static public CharacterManager getInstance() {
		if(theInstance == null) {
			theInstance = new CharacterManager();
		}
		return theInstance;
	}
	
	/**
	 * キャラクターを返します。
	 * @param idキャラクターナンバー
	 * @return
	 */
	public Character getCharacter(int id) {
		if(id < characterList.size()) {
			return characterList.get(id);
		}
		else {
			return null;
		}
	}
	
	/**
	 * キャラクターの数を返します。
	 * @return
	 */
	public int getNumberOfCharacters() {
		return characterList.size();
	}

	@Override
	protected void create() {
		// TODO Auto-generated method stub
		Character p = new Character(getData(MODEL_FILE_TAG));
		//WeaponModel w = new WeaponModel(getData(WEAPON_FILE_TAG));
		//p.setWeponModel(w);
		
		// キャラクタ名
		p.setName(getData(NAME_TAG));
		
		// キャラクタの説明
		p.setComment(getData(COMMENT_TAG));
		
		// アニメーションファイル
		if(getData(RIGHTMOVE_ANIMATION_FILE_TAG) != null) {
			Animation3D animation = AnimationFactory.loadAnimation(getData(RIGHTMOVE_ANIMATION_FILE_TAG));
			if(animation != null) {
				p.rightMoveAnimation = animation;
			}
		}
		if(getData(LEFTMOVE_ANIMATION_FILE_TAG) != null) {
			Animation3D animation = AnimationFactory.loadAnimation(getData(LEFTMOVE_ANIMATION_FILE_TAG));
			if(animation != null) {
				p.leftMoveAnimation = animation;
			}
		}
		if(getData(JUMP_ANIMATION_FILE_TAG) != null) {
			Animation3D animation = AnimationFactory.loadAnimation(getData(JUMP_ANIMATION_FILE_TAG));
			if(animation != null) {
				p.jumpAnimation = animation;
			}
		}
		if(hasData(JUMP_SOUND_FILE_TAG)) {
			Sound3D sound = new Sound3D(getData(JUMP_SOUND_FILE_TAG));
			if(sound != null) {
				p.jumpSound = sound;
			}
		}
		if(getData(DAMAGED_ANIMATION_FILE_TAG) != null) {
			Animation3D animation = AnimationFactory.loadAnimation(getData(DAMAGED_ANIMATION_FILE_TAG));
			if(animation != null) {
				p.damagedAnimation = animation;
			}
		}
		if(hasData(DAMAGED_SOUND_FILE_TAG)) {
			Sound3D sound = new Sound3D(getData(DAMAGED_SOUND_FILE_TAG));
			if(sound != null) {
				p.damagedSound = sound;
			}
		}
		
		// パラメータ
		if(!hasData(RUN_SPEED_TAG)) System.out.println("Error!! No RunSpeed data.");
		else p.setRunSpeed(Double.parseDouble(getData(RUN_SPEED_TAG)));
		if(!hasData(POWER_TAG)) System.out.println("Error!! No Power data.");
		else p.setPower(Integer.parseInt(getData(POWER_TAG)));
		if(!hasData(JUMP_POWER_TAG)) System.out.println("Error!! No JumpPower data.");
		else p.setJumpPower(Double.parseDouble(getData(JUMP_POWER_TAG)));
		if(!hasData(JUMP_FREQUENCY_TAG)) System.out.println("Error!! No JumpFrequency data.");
		else p.setJumpFrequency(Integer.parseInt(getData(JUMP_FREQUENCY_TAG)));
		if(!hasData(DEFENSE_TAG)) System.out.println("Error!! No Defense data.");
		else p.setDefense(Integer.parseInt(getData(DEFENSE_TAG)));
		if(!hasData(SIZE_TAG)) System.out.println("Error!! No Size data.");
		else p.setSize(Integer.parseInt(getData(SIZE_TAG)));
		
		// 通常の攻撃（牽制攻撃）
		if(getData(ATTACK_ANIMATION_FILE_TAG) != null) {
			Animation3D animation = AnimationFactory.loadAnimation(getData(ATTACK_ANIMATION_FILE_TAG));
			if(animation != null) {
				p.attackAnimation = animation;
			}
		}
		if(hasData(ATTACK_SOUND_FILE_TAG)) {
			Sound3D sound = new Sound3D(getData(ATTACK_SOUND_FILE_TAG));
			if(sound != null) {
				p.attackSound = sound;
			}
		}
		if(hasData(ATTACKING_PART_NAME_TAG)) p.setAttackingPartName(getData(ATTACKING_PART_NAME_TAG));
		if(hasData(ATTACK_TIME_LAG_TAG)) p.setAttackTimeLag(Long.parseLong(getData(ATTACK_TIME_LAG_TAG)));
		if(hasData(WEAPON_FILE_TAG)) {
			WeaponModel weapon = new WeaponModel(getData(WEAPON_FILE_TAG));
			if(hasData(WEAPON_ANIMATION_FILE_TAG)) {
				Animation3D animation = AnimationFactory.loadAnimation(getData(WEAPON_ANIMATION_FILE_TAG));
				if(animation != null) {
					weapon.weaponAnimation = animation;
				}
			}
			p.setWeaponModel(weapon);
		}
		if(hasData(WEAPON_SPEED_TAG)) {
			String vector = getData(WEAPON_SPEED_TAG);
			String axis[] = vector.split(",");
			p.setWeaponSpeed(Double.parseDouble(axis[0]),Double.parseDouble(axis[1]),Double.parseDouble(axis[2]));		
		}
		//ATTACKING_PART_NAMEとWEAPON_FILEのデータが重複するとき、どちらもデータがないとき、エラー。
		if((!hasData(ATTACKING_PART_NAME_TAG))&&(!hasData(WEAPON_FILE_TAG)))
			System.out.println("Error!! No Weapon nor AttackingPart data.");	
		else if((hasData(ATTACKING_PART_NAME_TAG))&&(hasData(WEAPON_FILE_TAG)))
			System.out.println("Error!! Weapon & AttackingPart are overlapping.");
		
		// 上向きの攻撃（対空攻撃）
		if(getData(UPPER_ATTACK_ANIMATION_FILE_TAG) != null) {
			Animation3D animation = AnimationFactory.loadAnimation(getData(UPPER_ATTACK_ANIMATION_FILE_TAG));
			if(animation != null) {
				p.upperAttackAnimation = animation;
			}
		}
		if(hasData(UPPER_ATTACK_SOUND_FILE_TAG)) {
			Sound3D sound = new Sound3D(getData(UPPER_ATTACK_SOUND_FILE_TAG));
			if(sound != null) {
				p.upperAttackSound = sound;
			}
		}
		if(hasData(UPPER_ATTACKING_PART_NAME_TAG)) p.setUpperAttackingPartName(getData(UPPER_ATTACKING_PART_NAME_TAG));
		if(hasData(UPPER_ATTACK_TIME_LAG_TAG)) p.setUpperAttackTimeLag(Long.parseLong(getData(UPPER_ATTACK_TIME_LAG_TAG)));
		if(hasData(UPPER_WEAPON_FILE_TAG)) {
			WeaponModel upperWeapon = new WeaponModel(getData(UPPER_WEAPON_FILE_TAG));
			if(hasData(UPPER_WEAPON_ANIMATION_FILE_TAG)) {
				Animation3D animation = AnimationFactory.loadAnimation(getData(UPPER_WEAPON_ANIMATION_FILE_TAG));
				if(animation != null) {
					upperWeapon.weaponAnimation = animation;
				}
			}
			p.setUpperWeaponModel(upperWeapon);
		}
		if(hasData(UPPER_WEAPON_SPEED_TAG)) {
			String vector = getData(UPPER_WEAPON_SPEED_TAG);
			String axis[] = vector.split(",");
			p.setUpperWeaponSpeed(Double.parseDouble(axis[0]),Double.parseDouble(axis[1]),Double.parseDouble(axis[2]));		
		}
		//UPPER_ATTACKING_PART_NAMEとUPPER_WEAPON_FILEのデータが重複するとき、エラー。
		if((hasData(UPPER_ATTACKING_PART_NAME_TAG))&&(hasData(UPPER_WEAPON_FILE_TAG)))
			System.out.println("Error!! Weapon & AttackingPart are overlapping.");
		
		// 下向きの攻撃（ジャンプ攻撃）
		if(getData(JUMP_ATTACK_ANIMATION_FILE_TAG) != null) {
			Animation3D animation = AnimationFactory.loadAnimation(getData(JUMP_ATTACK_ANIMATION_FILE_TAG));
			if(animation != null) {
				p.jumpAttackAnimation = animation;
			}
		}
		if(hasData(JUMP_ATTACK_SOUND_FILE_TAG)) {
			Sound3D sound = new Sound3D(getData(JUMP_ATTACK_SOUND_FILE_TAG));
			if(sound != null) {
				p.jumpAttackSound = sound;
			}
		}
		if(hasData(JUMP_ATTACKING_PART_NAME_TAG)) p.setJumpAttackingPartName(getData(JUMP_ATTACKING_PART_NAME_TAG));
		if(hasData(JUMP_ATTACK_TIME_LAG_TAG)) p.setJumpAttackTimeLag(Long.parseLong(getData(JUMP_ATTACK_TIME_LAG_TAG)));
		if(hasData(JUMP_WEAPON_FILE_TAG)) {
			WeaponModel jumpWeapon = new WeaponModel(getData(JUMP_WEAPON_FILE_TAG));
			if(hasData(JUMP_WEAPON_ANIMATION_FILE_TAG)) {
				Animation3D animation = AnimationFactory.loadAnimation(getData(JUMP_WEAPON_ANIMATION_FILE_TAG));
				if(animation != null) {
					jumpWeapon.weaponAnimation = animation;
				}
			}
			p.setJumpWeaponModel(jumpWeapon);
		}
		if(hasData(JUMP_WEAPON_SPEED_TAG)) {
			String vector = getData(JUMP_WEAPON_SPEED_TAG);
			String axis[] = vector.split(",");
			p.setJumpWeaponSpeed(Double.parseDouble(axis[0]),Double.parseDouble(axis[1]),Double.parseDouble(axis[2]));		
		}
		//JUMP_ATTACKING_PART_NAMEとJUMP_WEAPON_FILEのデータが重複するとき、エラー。
		if((hasData(JUMP_ATTACKING_PART_NAME_TAG))&&(hasData(JUMP_WEAPON_FILE_TAG)))
			System.out.println("Error!! Weapon & AttackingPart are overlapping.");

		characterList.add(p);
	}
}
