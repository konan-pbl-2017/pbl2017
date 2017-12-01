package framework.view3D;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.media.j3d.Appearance;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureCubeMap;
import javax.media.j3d.TextureUnitState;
import javax.vecmath.Vector3f;

import framework.model3D.BackgroundBox;
import framework.model3D.FresnelsReflectionMapGenerator;
import framework.model3D.ReflectionMapGenerator;

public class FresnelsReflectionMapShader {
	boolean bTransparent = false;
	TextureCubeMap reflectionMappingTexture = null;
	TextureCubeMap fresnelsMappingTexture = null;

	public void init(BackgroundBox skyBox) {
		// 環境反射のテクスチャ
		reflectionMappingTexture = new TextureCubeMap(TextureCubeMap.BASE_LEVEL, TextureCubeMap.RGBA, skyBox.getTopImage().getWidth());
		ImageComponent2D topSkyImage = skyBox.getTopImage();
		ImageComponent2D bottomSkyImage = skyBox.getBottomImage();
		ImageComponent2D eastSkyImage = skyBox.getEastImage();
		ImageComponent2D westSkyImage = skyBox.getWestImage();
		ImageComponent2D northSkyImage = skyBox.getNorthImage();
		ImageComponent2D southSkyImage = skyBox.getSouthImage();
		reflectionMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Y, topSkyImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Y, bottomSkyImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Z, westSkyImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Z, eastSkyImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.POSITIVE_X, northSkyImage);
		reflectionMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_X, southSkyImage);
		
		// フレネル反射係数を計算しテクスチャに埋め込む
		BufferedImage topFresnelsMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage bottomFresnelsMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage eastFresnelsMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage westFresnelsMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage northFresnelsMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage southFresnelsMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(-1.0f, ((float)t - 256.0f) / 256.0f, ((float)s - 256.0f) / 256.0f);
				int rgb = calcFresnels(texZ);
				southFresnelsMapping.setRGB(s, t, rgb);
			}
		}		
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(1.0f, ((float)t - 256.0f) / 256.0f, (256.0f - (float)s) / 256.0f);
				int rgb = calcFresnels(texZ);
				northFresnelsMapping.setRGB(s, t, rgb);
			}
		}
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(((float)s - 256.0f) / 256.0f, ((float)t - 256.0f) / 256.0f, 1.0f);
				int rgb = calcFresnels(texZ);
				eastFresnelsMapping.setRGB(s, t, rgb);
			}
		}
		
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f((256.0f - (float)s) / 256.0f, ((float)t - 256.0f) / 256.0f, -1.0f);
				int rgb = calcFresnels(texZ);
				westFresnelsMapping.setRGB(s, t, rgb);
			}
		}
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(((float)s - 256.0f) / 256.0f, 1.0f, (256.0f - (float)t) / 256.0f);
				int rgb = calcFresnels(texZ);
				topFresnelsMapping.setRGB(s, t, rgb);
			}
		}
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(((float)s - 256.0f) / 256.0f, -1.0f, ((float)t - 256.0f) / 256.0f);
				int rgb = calcFresnels(texZ);
				bottomFresnelsMapping.setRGB(s, t, rgb);
			}
		}
		
		ImageComponent2D topImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, topFresnelsMapping, true, false);
		ImageComponent2D bottomImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, bottomFresnelsMapping, true, false);
		ImageComponent2D eastImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, eastFresnelsMapping, true, false);
		ImageComponent2D westImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, westFresnelsMapping, true, false);
		ImageComponent2D northImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, northFresnelsMapping, true, false);
		ImageComponent2D southImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, southFresnelsMapping, true, false);
		fresnelsMappingTexture = new TextureCubeMap(TextureCubeMap.BASE_LEVEL, TextureCubeMap.RGBA, topFresnelsMapping.getWidth());
		fresnelsMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Y, topImage);
		fresnelsMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Y, bottomImage);
		fresnelsMappingTexture.setImage(0, TextureCubeMap.POSITIVE_X, northImage);
		fresnelsMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_X, southImage);
		fresnelsMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Z, eastImage);
		fresnelsMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Z, westImage);
	}	
	
	public void updateAppearance(Appearance ap, FresnelsReflectionMapGenerator reflectionMapGenerator, Camera3D camera) {
		if (reflectionMappingTexture == null) return;
		if (!reflectionMapGenerator.hasMapped()) {
			TexCoordGeneration tcg1 = new TexCoordGeneration(TexCoordGeneration.REFLECTION_MAP, TexCoordGeneration.TEXTURE_COORDINATE_3);
			TextureAttributes ta1 = new TextureAttributes();
			ta1.setTextureMode(TextureAttributes.COMBINE);
			ta1.setCombineRgbMode(TextureAttributes.COMBINE_ADD_SIGNED);
//			ta1.setCombineRgbMode(TextureAttributes.COMBINE_INTERPOLATE);
			ta1.setCombineRgbSource(0, TextureAttributes.COMBINE_TEXTURE_COLOR);
			ta1.setCombineRgbSource(1, TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE);
			ta1.setCombineRgbSource(2, TextureAttributes.COMBINE_CONSTANT_COLOR);
			ta1.setCombineRgbFunction(0, TextureAttributes.COMBINE_SRC_COLOR);
			ta1.setCombineRgbFunction(1, TextureAttributes.COMBINE_SRC_COLOR);
			ta1.setCombineRgbFunction(2, TextureAttributes.COMBINE_SRC_COLOR);
			ta1.setTextureBlendColor(reflectionMapGenerator.getBlendColor());
			ta1.setCapability(TextureAttributes.ALLOW_TRANSFORM_READ);
			ta1.setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);
			// TexCoordGeneration.REFLECTION_MAP は視線に対して固定なので、視線の向きに合わせてテクスチャを回転
			ta1.setTextureTransform(camera.getWorldToView());
			
			TexCoordGeneration tcg2 = new TexCoordGeneration(TexCoordGeneration.REFLECTION_MAP, TexCoordGeneration.TEXTURE_COORDINATE_3);
			TextureAttributes ta2 = new TextureAttributes();
			
			if (reflectionMapGenerator.isTransparent()) {
				ta2.setTextureMode(TextureAttributes.BLEND);
			} else {
				ta2.setTextureMode(TextureAttributes.COMBINE);
				ta2.setCombineRgbMode(TextureAttributes.COMBINE_INTERPOLATE);
				ta2.setCombineRgbSource(0, TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE);
				ta2.setCombineRgbSource(1, TextureAttributes.COMBINE_OBJECT_COLOR);
				ta2.setCombineRgbSource(2, TextureAttributes.COMBINE_TEXTURE_COLOR);
				ta2.setCombineRgbFunction(0, TextureAttributes.COMBINE_SRC_COLOR);
				ta2.setCombineRgbFunction(1, TextureAttributes.COMBINE_SRC_COLOR);
				ta2.setCombineRgbFunction(2, TextureAttributes.COMBINE_SRC_ALPHA);
			}
			
			int n = ap.getTextureUnitCount();
			if (n > 0) {
				// テクスチャユニットの一番最後に反射マッピングとフレネル反射係数を設定する
				TextureUnitState newUnitStates[] = new TextureUnitState[n + 2];
				if (ap.getTextureUnitState() != null) System.arraycopy(ap.getTextureUnitState(), 0, newUnitStates, 0, n);
				newUnitStates[n] = new TextureUnitState(reflectionMappingTexture, ta1, tcg1);
				newUnitStates[n].setCapability(TextureUnitState.ALLOW_STATE_READ);
				newUnitStates[n].setCapability(TextureUnitState.ALLOW_STATE_WRITE);
				newUnitStates[n+1] = new TextureUnitState(fresnelsMappingTexture, ta2, tcg2);
				
				ap.setTextureUnitState(newUnitStates);
			} else {	
				TextureUnitState newUnitStates[] = new TextureUnitState[2];
				newUnitStates[0] = new TextureUnitState(reflectionMappingTexture, ta1, tcg1);
				newUnitStates[0].setCapability(TextureUnitState.ALLOW_STATE_READ);
				newUnitStates[0].setCapability(TextureUnitState.ALLOW_STATE_WRITE);
				newUnitStates[1] = new TextureUnitState(fresnelsMappingTexture, ta2, tcg2);
				
				ap.setTextureUnitState(newUnitStates);
			}
			reflectionMapGenerator.setMapped();
		} else {
			int n = ap.getTextureUnitCount();
			if (n > 1) {
				TextureAttributes ta = ap.getTextureUnitState(n - 2).getTextureAttributes();
				// TexCoordGeneration.REFLECTION_MAP は視線に対して固定なので、視線の向きに合わせてテクスチャを回転
				ta.setTextureTransform(camera.getWorldToView());						
			}
		}
	}

	private int calcFresnels(Vector3f texZ) {
		texZ.normalize();
		int a = (int)(-texZ.z * 127.5f + 127.5f);
		int rgb = (a << 24) + (0 << 16) + (0 << 8) + 0;
		return rgb;
	}
}
