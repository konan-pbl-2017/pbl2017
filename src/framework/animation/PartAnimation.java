package framework.animation;

import java.util.ArrayList;

import javax.media.j3d.Texture;

import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;


/**
 * 部品単位のアニメーション情報を保持する
 * @author 新田直也
 *
 */
public class PartAnimation {
	private String name;
	private int textureUnit;
	private ArrayList<KeyFrame> keyList = new ArrayList<KeyFrame>();
	public static final int SINGLE_TEXTURE = -1;
	
	public PartAnimation(String name) {
		 this.name = name;
		 textureUnit = SINGLE_TEXTURE;
	}
	
	public PartAnimation(String name, int textureUnit) {
		 this.name = name;
		 this.textureUnit = textureUnit;
	}
	
	String getName() {
		return name;
	}
	
	int getTextureUnit() {
		return textureUnit;
	}

	//アニメーションの経過時間の前後のkeyをKeyFrame型の配列で取得するメソッド
	KeyFrame[] getKey(long t){
		int i;
		KeyFrame[] aroundKey = new KeyFrame[2];
					
		for(i=1;i<keyList.size();i++){
			
			//tがkeyそのものだった場合（tの前後のKeyFrameが必要ない時）、要素０にそのものを代入し、要素１をnullに設定しておく
			if(t == keyList.get(i).key){
				aroundKey[0] = keyList.get(i);
				aroundKey[1] = null;
				return aroundKey;
			}
			
			//tの前後のKeyFrameを代入
			if(t < keyList.get(i).key){
				aroundKey[0] = keyList.get(i-1);
				aroundKey[1] = keyList.get(i);
				return aroundKey;
			}
		}
		return null;
	}
	
	//keyListの最後の要素（keyの最大値）を取得して返すメソッド
	long getLastKey() {
		return keyList.get(keyList.size()-1).key;
	}
	
	public void add(long k, Position3D p) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = p;
		e.keyValueQuaternion = null;
		e.keyValueTexture = null;
		e.keyValueTexturePosition = null;
		e.keyValueTextureQuaternion = null;
		keyList.add(e);
	}
	
	public void add(long k, Quaternion3D q) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = q;
		e.keyValueTexture = null;
		e.keyValueTexturePosition = null;
		e.keyValueTextureQuaternion = null;
		keyList.add(e);
	}
	
	public void add(long k, Position3D p, Quaternion3D q) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = p;
		e.keyValueQuaternion = q;
		e.keyValueTexture = null;
		e.keyValueTexturePosition = null;
		e.keyValueTextureQuaternion = null;
		keyList.add(e);
	}
		
	public void addTexture(long k, Texture texture) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = null;
		e.keyValueTexture = texture;
		e.keyValueTexturePosition = null;
		e.keyValueTextureQuaternion = null;
		keyList.add(e);
	}
	
	public void addTexture(long k, Texture texture, Position3D tp) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = null;
		e.keyValueTexture = texture;
		e.keyValueTexturePosition = tp;
		e.keyValueTextureQuaternion = null;
		keyList.add(e);
	}
	
	public void addTexture(long k, Texture texture, Quaternion3D tq) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = null;
		e.keyValueTexture = texture;
		e.keyValueTexturePosition = null;
		e.keyValueTextureQuaternion = tq;
		keyList.add(e);
	}
	
	public void addTexture(long k, Texture texture, Position3D tp, Quaternion3D tq) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = null;
		e.keyValueTexture = texture;
		e.keyValueTexturePosition = tp;
		e.keyValueTextureQuaternion = tq;
		keyList.add(e);
	}
	
	public void addTexture(long k, Position3D tp) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = null;
		e.keyValueTexture = null;
		e.keyValueTexturePosition = tp;
		e.keyValueTextureQuaternion = null;
		keyList.add(e);
	}
	
	public void addTexture(long k, Quaternion3D tq) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = null;
		e.keyValueTexture = null;
		e.keyValueTexturePosition = null;
		e.keyValueTextureQuaternion = tq;
		keyList.add(e);
	}
	
	public void addTexture(long k, Position3D tp, Quaternion3D tq) {
		KeyFrame e = new KeyFrame();
		e.key = k;
		e.keyValuePosition = null;
		e.keyValueQuaternion = null;
		e.keyValueTexture = null;
		e.keyValueTexturePosition = tp;
		e.keyValueTextureQuaternion = tq;
		keyList.add(e);
	}
}
