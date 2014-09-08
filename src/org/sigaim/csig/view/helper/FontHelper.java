package org.sigaim.csig.view.helper;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public abstract class FontHelper {

	public static Font getTTF(String file) {
		InputStream is = FontHelper.class.getResourceAsStream("/org/sigaim/csig/resources/font/"+file);
		try {
			return Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
