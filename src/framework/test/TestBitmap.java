package framework.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class TestBitmap extends JFrame {
	public static void main(String[] args) {
		TestBitmap test = new TestBitmap();

		test.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		test.setBounds(0, 0, 200, 200);
		test.setVisible(true);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		BufferedImage readImage = null;
		try {
			readImage = ImageIO.read(new File("data\\MageGame\\block.gif"));
		} catch (Exception e) {
			e.printStackTrace();
			readImage = null;
		}

		if (readImage != null) {
			g2.drawImage(readImage, 0, 30, this);
		}

//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_ON);
//
//		BasicStroke wideStroke = new BasicStroke(4.0f);
//		g2.setStroke(wideStroke);
//
//		g2.setPaint(Color.black);
//		g2.draw(new Ellipse2D.Double(30, 40, 50, 50));
//
//		g2.setPaint(Color.blue);
//		g2.draw(new Ellipse2D.Double(70, 40, 50, 50));
//
//		g2.setPaint(Color.red);
//		g2.draw(new Ellipse2D.Double(110, 40, 50, 50));
//
//		g2.setPaint(Color.yellow);
//		g2.fill(new Arc2D.Double(50, 100, 110, 110, 330, 100, Arc2D.PIE));
//		g2.setPaint(Color.gray);
//		g2.draw(new Arc2D.Double(50, 100, 110, 110, 330, 100, Arc2D.PIE));
	}
}