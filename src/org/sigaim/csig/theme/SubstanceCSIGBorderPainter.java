package org.sigaim.csig.theme;

import java.awt.Color;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.border.StandardBorderPainter;

public class SubstanceCSIGBorderPainter extends StandardBorderPainter {
	@Override
	public String getDisplayName() {
		return "CSIG";
	}

	@Override
	public Color getMidBorderColor(SubstanceColorScheme borderScheme) {
		return super.getTopBorderColor(borderScheme);
	}

	@Override
	public Color getBottomBorderColor(SubstanceColorScheme borderScheme) {
		return super.getTopBorderColor(borderScheme);
	}

}
