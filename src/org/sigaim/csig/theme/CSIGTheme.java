package org.sigaim.csig.theme;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.sigaim.csig.view.LoginDialog;

public abstract class CSIGTheme {

	private static Icon icnHelp = null;
	
	public static void apply(){
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		defaults.put("Button.background",  new Color(255,0,0));
		defaults.put("Panel.background", new Color(251,251,253));
		defaults.put("InternalFrame.background", new Color(251,251,253));
		defaults.put("Viewport.background", new Color(251,251,253));
		defaults.put("OptionPane.background", new Color(251,251,253));
	}
	
	public static Icon iconHelp(){
		if(icnHelp == null)
			icnHelp = new ImageIcon(CSIGTheme.class.getResource(
					"/org/sigaim/csig/theme/images/icon_help.png"));
		return icnHelp;
	}
}
