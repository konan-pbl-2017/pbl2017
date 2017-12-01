package framework.model3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;


public class BackgroundBox extends Background {
	Texture northTex;
	Texture westTex;
	Texture southTex;
	Texture eastTex;
	Texture topTex;
	Texture bottomTex;
	
	public BackgroundBox(Texture northTex, Texture westTex, Texture southTex, Texture eastTex, Texture topTex, Texture bottomTex) {
		this.northTex = northTex;
		this.westTex = westTex;
		this.southTex = southTex;
		this.eastTex = eastTex;
		this.topTex = topTex;
		this.bottomTex = bottomTex;
		Point3d p0 = new Point3d(-1.0,-1.0,-1.0);
		Point3d p1 = new Point3d(-1.0,-1.0, 1.0);
		Point3d p2 = new Point3d(-1.0, 1.0,-1.0);
		Point3d p3 = new Point3d(-1.0, 1.0, 1.0);
		Point3d p4 = new Point3d( 1.0, 1.0,-1.0);
		Point3d p5 = new Point3d( 1.0, 1.0, 1.0);
		Point3d p6 = new Point3d( 1.0,-1.0,-1.0);
		Point3d p7 = new Point3d( 1.0,-1.0, 1.0);
		QuadArray top = new QuadArray(4, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
		top.setCoordinate(0, p4);
		top.setCoordinate(1, p5);
		top.setCoordinate(2, p3);
		top.setCoordinate(3, p2);
		top.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.0f));
		top.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 1.0f));
		top.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));
		top.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 0.0f));
		QuadArray bottom = new QuadArray(4, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
		bottom.setCoordinate(0, p7);
		bottom.setCoordinate(1, p6);
		bottom.setCoordinate(2, p0);
		bottom.setCoordinate(3, p1);
		bottom.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.0f));
		bottom.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 1.0f));
		bottom.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));
		bottom.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 0.0f));
		QuadArray north = new QuadArray(4, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
		north.setCoordinate(0, p4);
		north.setCoordinate(1, p6);
		north.setCoordinate(2, p7);
		north.setCoordinate(3, p5);
		north.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.0f));
		north.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 1.0f));
		north.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));
		north.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 0.0f));
		QuadArray south = new QuadArray(4, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
		south.setCoordinate(0, p3);
		south.setCoordinate(1, p1);
		south.setCoordinate(2, p0);
		south.setCoordinate(3, p2);
		south.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.0f));
		south.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 1.0f));
		south.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));
		south.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 0.0f));
		QuadArray east = new QuadArray(4, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
		east.setCoordinate(0, p2);
		east.setCoordinate(1, p0);
		east.setCoordinate(2, p6);
		east.setCoordinate(3, p4);
		east.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.0f));
		east.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 1.0f));
		east.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));
		east.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 0.0f));
		QuadArray west = new QuadArray(4, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
		west.setCoordinate(0, p5);
		west.setCoordinate(1, p7);
		west.setCoordinate(2, p1);
		west.setCoordinate(3, p3);
		west.setTextureCoordinate(0, 0, new TexCoord2f(1.0f, 0.0f));
		west.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 1.0f));
		west.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));
		west.setTextureCoordinate(0, 3, new TexCoord2f(0.0f, 0.0f));
		Appearance apTop = new Appearance();
		apTop.setTexture(topTex);
		Shape3D shapeTop = new Shape3D(top, apTop);
		
		Appearance apBottom = new Appearance();
		apBottom.setTexture(bottomTex);
		Shape3D shapeBottom = new Shape3D(bottom, apBottom);
		
		Appearance apNorth = new Appearance();
		apNorth.setTexture(northTex);
		Shape3D shapeNorth = new Shape3D(north, apNorth);

		Appearance apSouth = new Appearance();
		apSouth.setTexture(southTex);
		Shape3D shapeSouth = new Shape3D(south, apSouth);

		Appearance apWest = new Appearance();
		apWest.setTexture(westTex);
		Shape3D shapeWest = new Shape3D(west, apWest);

		Appearance apEast = new Appearance();
		apEast.setTexture(eastTex);
		Shape3D shapeEast = new Shape3D(east, apEast);
		
		BranchGroup bg = new BranchGroup();
		bg.addChild(shapeTop);
		bg.addChild(shapeBottom);
		bg.addChild(shapeNorth);
		bg.addChild(shapeSouth);
		bg.addChild(shapeWest);
		bg.addChild(shapeEast);
		
		setGeometry(bg);
	}
	
	public ImageComponent2D getNorthImage() {
		return (ImageComponent2D)northTex.getImage(0);
	}

	
	public ImageComponent2D getSouthImage() {
		return (ImageComponent2D)southTex.getImage(0);
	}

	
	public ImageComponent2D getEastImage() {
		return (ImageComponent2D)eastTex.getImage(0);
	}

	
	public ImageComponent2D getWestImage() {
		return (ImageComponent2D)westTex.getImage(0);
	}

	
	public ImageComponent2D getTopImage() {
		return (ImageComponent2D)topTex.getImage(0);
	}

	
	public ImageComponent2D getBottomImage() {
		return (ImageComponent2D)bottomTex.getImage(0);
	}
}
