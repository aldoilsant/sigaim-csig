package org.sigaim.csig.theme;

import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

public abstract class CSIGTheme {

	public static void apply(){
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		defaults.put("Button.background",  new Color(255,0,0));
		defaults.put("Panel.background", new Color(251,251,253));
		defaults.put("InternalFrame.background", new Color(251,251,253));
		defaults.put("Viewport.background", new Color(251,251,253));
		defaults.put("OptionPane.background", new Color(251,251,253));
	}
}
