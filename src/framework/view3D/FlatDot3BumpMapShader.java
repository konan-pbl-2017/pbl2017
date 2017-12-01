package framework.view3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import framework.model3D.BumpMapGenerator;

public class FlatDot3BumpMapShader {
	private static Vector3f yAxis = new Vector3f(0.0f, 1.0f, 0.0f);
	private static Color3f color;
	
	public void init(DirectionalLight dirlight) {
		Vector3f lightDir = new Vector3f();
		dirlight.getDirection(lightDir);
		lightDir.negate();		// 法線方向が光線と逆向きの面が一番明るい
		lightDir.normalize();
		color = calcRelativeLightDir(lightDir, yAxis);
	}
	
	public void updateAppearance(Appearance ap, BumpMapGenerator bumpMapGenerator, Camera3D camera) {
		if (!bumpMapGenerator.hasMapped()) {
			ap.setMaterial(null);
			ColoringAttributes ca = new ColoringAttributes();
			ca.setColor(color);
			ap.setColoringAttributes(ca);
			int n = ap.getTextureUnitCount();
			TextureUnitState newUnitStates[] = new TextureUnitState[n + 1];
			if (ap.getTextureUnitState() != null) System.arraycopy(ap.getTextureUnitState(), 0, newUnitStates, 1, n);
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.COMBINE);
			ta.setCombineRgbMode(TextureAttributes.COMBINE_DOT3);
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_READ);
			ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);
			// テクスチャユニットの最初から二番目にバンプマッピングを設定する
			newUnitStates[0] = new TextureUnitState(bumpMapGenerator.getBumpMap(), ta, bumpMapGenerator.getTexCoordGeneration());

			ap.setTextureUnitState(newUnitStates);
			bumpMapGenerator.setMapped();
		}
	}

	private Color3f calcRelativeLightDir(Vector3f lightDir, Vector3f texZ) {
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
		return new Color3f((x + 1.0f) / 2.0f, (y + 1.0f) / 2.0f, (z + 1.0f) / 2.0f);
	}
}
