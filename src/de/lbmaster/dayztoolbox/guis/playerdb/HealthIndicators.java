package de.lbmaster.dayztoolbox.guis.playerdb;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class HealthIndicators {
	
	public static final ImageIcon image_pristine, image_worn, image_damaged, image_badly_damaged, image_ruined, image_none;

	private static final Color COLOR_PRISTINE = new Color(0x0040FF00);
	private static final Color COLOR_WORN = new Color(0x00BFFF00);
	private static final Color COLOR_DAMAGED = new Color(0x00FFFF00);
	private static final Color COLOR_BADLY_DAMAGED = new Color(0x00FFBF00);
	private static final Color COLOR_RUINED = new Color(0x00FF0000);
	private static final Color COLOR_NONE = new Color(0x00000000);
	
	public static ImageIcon getHealthImage(float health) {
		if (health < 0) {
			return image_none;
		} else if (health == 0) {
			return image_ruined;
		} else if (health > 0 && health <= 30) {
			return image_badly_damaged;
		} else if (health > 30 && health <= 50) {
			return image_damaged;
		} else if (health > 50 && health <= 70) {
			return image_worn;
		} else {
			return image_pristine;
		}
	}
	
	static {
		image_pristine = new ImageIcon(createColoredImage(COLOR_PRISTINE));
		image_worn = new ImageIcon(createColoredImage(COLOR_WORN));
		image_damaged = new ImageIcon(createColoredImage(COLOR_DAMAGED));
		image_badly_damaged = new ImageIcon(createColoredImage(COLOR_BADLY_DAMAGED));
		image_ruined = new ImageIcon(createColoredImage(COLOR_RUINED));
		image_none = new ImageIcon(createColoredImage(COLOR_NONE));
	}
	
	private static BufferedImage createColoredImage(Color color) {
		BufferedImage bimg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimg.createGraphics();
		g.setColor(color);
		g.fillOval(3, 3, 9, 9);
		g.dispose();
		return bimg;
	}
}
