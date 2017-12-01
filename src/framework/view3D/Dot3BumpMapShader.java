package framework.view3D;

import java.awt.image.BufferedImage;

import javax.media.j3d.Appearance;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureCubeMap;
import javax.media.j3d.TextureUnitState;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import framework.model3D.BumpMapGenerator;

public class Dot3BumpMapShader {
	private TextureCubeMap normalMappingTexture = null;
	private static Vector3f yAxis = new Vector3f(0.0f, 1.0f, 0.0f);
	
	public void init(DirectionalLight dirlight) {
		Vector3f lightDir = new Vector3f();
		dirlight.getDirection(lightDir);
		lightDir.negate();		// 法線方向が光線と逆向きの面が一番明るい
		lightDir.normalize();
		
		BufferedImage topLightMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage bottomLightMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage eastLightMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage westLightMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage northLightMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		BufferedImage southLightMapping = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(-1.0f, ((float)t - 256.0f) / 256.0f, ((float)s - 256.0f) / 256.0f);
				int rgb = calcRelativeLightDir(lightDir, texZ);
				southLightMapping.setRGB(s, t, rgb);
			}
		}
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(1.0f, ((float)t - 256.0f) / 256.0f, (256.0f - (float)s) / 256.0f);
				int rgb = calcRelativeLightDir(lightDir, texZ);
				northLightMapping.setRGB(s, t, rgb);
			}
		}
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(((float)s - 256.0f) / 256.0f, ((float)t - 256.0f) / 256.0f, 1.0f);
				int rgb = calcRelativeLightDir(lightDir, texZ);
				eastLightMapping.setRGB(s, t, rgb);
			}
		}
		
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f((256.0f - (float)s) / 256.0f, ((float)t - 256.0f) / 256.0f, -1.0f);
				int rgb = calcRelativeLightDir(lightDir, texZ);
				westLightMapping.setRGB(s, t, rgb);
			}
		}
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(((float)s - 256.0f) / 256.0f, 1.0f, (256.0f - (float)t) / 256.0f);
				int rgb = calcRelativeLightDir(lightDir, texZ);
				topLightMapping.setRGB(s, t, rgb);
			}
		}
		
		for (int t = 0; t < 512; t++) {
			for (int s = 0; s < 512; s++) {
				Vector3f texZ = new Vector3f(((float)s - 256.0f) / 256.0f, -1.0f, ((float)t - 256.0f) / 256.0f);
				int rgb = calcRelativeLightDir(lightDir, texZ);
				bottomLightMapping.setRGB(s, t, rgb);
			}
		}
		
		ImageComponent2D topImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, topLightMapping, true, false);
		ImageComponent2D bottomImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, bottomLightMapping, true, false);
		ImageComponent2D eastImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, eastLightMapping, true, false);
		ImageComponent2D westImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, westLightMapping, true, false);
		ImageComponent2D northImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, northLightMapping, true, false);
		ImageComponent2D southImage = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, southLightMapping, true, false);
		normalMappingTexture = new TextureCubeMap(TextureCubeMap.BASE_LEVEL, TextureCubeMap.RGB, topLightMapping.getWidth());
		normalMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Y, topImage);
		normalMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Y, bottomImage);
		normalMappingTexture.setImage(0, TextureCubeMap.POSITIVE_X, northImage);
		normalMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_X, southImage);
		normalMappingTexture.setImage(0, TextureCubeMap.POSITIVE_Z, eastImage);
		normalMappingTexture.setImage(0, TextureCubeMap.NEGATIVE_Z, westImage);
	}
	
	public void updateAppearance(Appearance ap, BumpMapGenerator bumpMapGenerator, Camera3D camera) {
		if (normalMappingTexture == null) return;
		if (!bumpMapGenerator.hasMapped()) {
			int n = ap.getTextureUnitCount();
			TextureUnitState newUnitStates[] = new TextureUnitState[n + 2];
			if (ap.getTextureUnitState() != null) System.arraycopy(ap.getTextureUnitState(), 0, newUnitStates, 2, n);
			TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.NORMAL_MAP, TexCoordGeneration.TEXTURE_COORDINATE_3);
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.REPLACE);
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_READ);
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);
			// TexCoordGeneration.NORMAL_MAP は視線に対して固定なので、視線の向きに合わせてテクスチャを回転
			ta.setTextureTransform(camera.getWorldToView());
			// テクスチャユニットの一番最初に法線マッピングを設定する
			newUnitStates[0] = new TextureUnitState(normalMappingTexture, ta, tcg);
			
			ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.COMBINE);
			ta.setCombineRgbMode(TextureAttributes.COMBINE_DOT3);
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_READ);
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);
			// テクスチャユニットの最初から二番目にバンプマッピングを設定する
			newUnitStates[1] = new TextureUnitState(bumpMapGenerator.getBumpMap(), ta, bumpMapGenerator.getTexCoordGeneration());

			ap.setTextureUnitState(newUnitStates);
			bumpMapGenerator.setMapped();
		} else {
			TextureAttributes ta = ap.getTextureUnitState(0).getTextureAttributes();
			// TexCoordGeneration.NORMZAL_MAP は視線に対して固定なので、視線の向きに合わせてテクスチャを回転
			ta.setTextureTransform(camera.getWorldToView());						
		}
	}

	private int calcRelativeLightDir(Vector3f lightDir, Vector3f texZ) {
		texZ.normalize();
		Vector3f texX = new Vector3f();
		texX.cross(yAxis, texZ);
		if (texX.length() == 0.0f) {
			texX = new Vector3f(1.0f, 0.0f, 0.0f);
		} else {
			texX.normalize();
		}
		Vector3f texY = new Vector3f();
		texY.cross(texZ, texX);	
		float x = lightDir.dot(texX);
		float y = lightDir.dot(texY);
		float z = lightDir.dot(texZ);
		int r = (int)(x * 127.5f + 127.5f);
		int g = (int)(y * 127.5f + 127.5f);
		int b = (int)(z * 127.5f + 127.5f);
		int rgb = (r << 16) + (g << 8) + b;
		return rgb;
	}
}
