package framework.game2D;

import java.util.HashMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;

import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.gameMain.OvergroundActor;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;

public class OvergroundSprite2D extends OvergroundActor2D {
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private float scale;
	private Box box;

	// /////////////////////////////////////////////////
	//
	// コンストラクタ
	//
	// /////////////////////////////////////////////////
	
	public OvergroundSprite2D(String imageFile) {
		this(imageFile, 1.0f);
	}

	public OvergroundSprite2D(String imageFile, float scale) {
		this.scale = scale;
		
		if (imageFile != null && textures.containsKey(imageFile) == false) {
			TextureLoader loader = new TextureLoader(imageFile, TextureLoader.BY_REFERENCE, null);
			textures.put(imageFile, loader.getTexture());
		}
		// キャラクターの3Dデータを読み込み配置する
		Texture texture = textures.get(imageFile);
		
		Appearance appearance = new Appearance();

		if (imageFile != null){
			appearance.setTexture(texture);
			appearance.setCapability(Appearance.ALLOW_TEXTURE_READ);
			appearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		} 

		Material material = new Material();
		material.setLightingEnable(false);
		material.setAmbientColor(1.0f, 1.0f, 1.0f);
		material.setSpecularColor(0.0f, 0.0f, 0.0f);
		material.setShininess(1.0f);
		appearance.setMaterial(material);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		appearance.setTextureAttributes(ta);

		TransparencyAttributes transAttributes = new TransparencyAttributes();

		transAttributes
				.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_READ);
		transAttributes
				.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_WRITE);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_MODE_READ);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
		transAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

		transAttributes.setTransparencyMode(TransparencyAttributes.BLEND_ONE);
		transAttributes.setTransparency(0.0f);
		// transAttributes.setSrcBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA);
		transAttributes
				.setDstBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA);
		appearance.setTransparencyAttributes(transAttributes);

		box = new Box(1.0f * scale, 1.0f * scale, 0.01f, Box.GENERATE_TEXTURE_COORDS
				| Box.GENERATE_NORMALS, appearance);
		Object3D body = new Object3D("sprite", box);
		
		setActor(createActor(body, null));
		
		setPosition(0.0, 0.0);
		setVelocity(0.0, 0.0);
		setCollisionRadius(1.0);		
	}

	@Override
	public String getAnimationFileName() {
		return null;
	}

	@Override
	public String getModelFileName() {
		return null;
	}
	
	public void setImage(String imageFile) {
		if (imageFile != null && textures.containsKey(imageFile) == false) {
			TextureLoader loader = new TextureLoader(imageFile, TextureLoader.BY_REFERENCE, null);
			textures.put(imageFile, loader.getTexture());
		}
		// キャラクターの3Dデータを読み込み配置する
		Texture texture = textures.get(imageFile);
		
		Appearance appearance = box.getAppearance();
		if (imageFile != null){
			appearance.setTexture(texture);
		}
	}
}
