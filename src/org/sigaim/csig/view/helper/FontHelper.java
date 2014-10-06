package org.sigaim.csig.view.helper;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public abstract class FontHelper {

	public static Font getTTF(String file, int size) {
		InputStream is = FontHelper.class.getResourceAsStream("/org/sigaim/csig/resources/font/"+file);
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			font = font.deriveFont(Font.PLAIN,size);
	        GraphicsEnvironment ge =
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(font);
	        return font;
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
