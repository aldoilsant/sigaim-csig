package org.sigaim.csig.theme;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.pushingpixels.substance.api.colorscheme.BaseLightColorScheme;

public class ColorScheme {
	
	public static final Color textColor = new Color(74, 89, 107);
	public static final Color backgroundColor = new Color(255,255,255);
	public static final Color titleBackground = new Color(242, 242, 242);
	
	private static BufferedImage titleBarLogo = null;
	public static BufferedImage titleBarLogo(){
		if(titleBarLogo == null){
			try {
				titleBarLogo = ImageIO.read(
						ColorScheme.class.getResource("/org/sigaim/csig/theme/images/titlebarLogo.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return titleBarLogo;
	}
}
