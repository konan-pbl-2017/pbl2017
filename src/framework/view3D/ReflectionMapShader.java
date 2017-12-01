package framework.view3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureCubeMap;
import javax.media.j3d.TextureUnitState;

import framework.model3D.BackgroundBox;
import framework.model3D.ReflectionMapGenerator;

public class ReflectionMapShader {
	TextureCubeMap reflectionMappingTexture = null;

	public void init(BackgroundBox skyBox) {
		reflectionMappingTexture = new TextureCubeMap(TextureCubeMap.BASE_LEVEL, TextureCubeMap.RGB, skyBox.getTopImage().getWidth());
		ImageComponent2D topImage = (ImageComponent2D)skyBox.getTopImage();
		ImageComponent2D bottomImage = (ImageComponent2D)skyBox.getBottomImage();
		ImageComponent2D eastImage = (ImageComponent2D)skyBox.getEastImage();
		ImageComponent2D westImage = (ImageComponent2D)skyBox.getWestImage();
		ImageComponent2D northImage = (ImageComponent2D)skyBox.getNorthImage();
		ImageComponent2D southImage = (ImageComponent2D)skyBox.getSouthImage();		
		reflectionMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Y, topImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Y, bottomImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Z, eastImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Z, westImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.POSITIVE_X, northImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_X, southImage);
	}
	
	
	public void updateAppearance(Appearance ap, ReflectionMapGenerator reflectionMapGenerator, Camera3D camera) {
		if (reflectionMappingTexture == null) return;
		if (!reflectionMapGenerator.hasMapped()) {
			TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.REFLECTION_MAP, TexCoordGeneration.TEXTURE_COORDINATE_3);
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.COMBINE);
			ta.setCombineRgbMode(TextureAttributes.COMBINE_INTERPOLATE);
			ta.setCombineRgbSource(0, TextureAttributes.COMBINE_TEXTURE_COLOR);
			ta.setCombineRgbSource(1, TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE);
			ta.setCombineRgbSource(2, TextureAttributes.COMBINE_CONSTANT_COLOR);
			ta.setCombineRgbFunction(0, TextureAttributes.COMBINE_SRC_COLOR);
			ta.setCombineRgbFunction(1, TextureAttributes.COMBINE_SRC_COLOR);
			ta.setCombineRgbFunction(2, TextureAttributes.COMBINE_SRC_COLOR);
			ta.setTextureBlendColor(reflectionMapGenerator.getBlendColor());
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_READ);
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);
			// TexCoordGeneration.REFLECTION_MAP は視線に対して固定なので、視線の向きに合わせてテクスチャを回転
			ta.setTextureTransform(camera.getWorldToView());
			int n = ap.getTextureUnitCount();
			if (n > 0) {
				// テクスチャユニットの一番最後に反射マッピングを設定する
				TextureUnitState newUnitStates[] = new TextureUnitState[n + 1];
				if (ap.getTextureUnitState() != null && n > 0) System.arraycopy(ap.getTextureUnitState(), 0, newUnitStates, 0, n);
				newUnitStates[n] = new TextureUnitState(reflectionMappingTexture, ta, tcg);
				
				ap.setTextureUnitState(newUnitStates);
			} else {	
				ap.setTexture(reflectionMappingTexture);
				ap.setTextureAttributes(ta);
				ap.setTexCoordGeneration(tcg);
			}
			reflectionMapGenerator.setMapped();
		} else {
			TextureAttributes ta;
			int n = ap.getTextureUnitCount();
			if (n > 0) {
				ta = ap.getTextureUnitState(ap.getTextureUnitCount() - 1).getTextureAttributes();
			} else {
				ta = ap.getTextureAttributes();
			}
			// TexCoordGeneration.REFLECTION_MAP は視線に対して固定なので、視線の向きに合わせてテクスチャを回転
			ta.setTextureTransform(camera.getWorldToView());						
		}
	}
}
