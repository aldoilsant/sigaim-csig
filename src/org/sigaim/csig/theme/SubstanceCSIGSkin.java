package org.sigaim.csig.theme;

import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.colorscheme.LightAquaColorScheme;
import org.pushingpixels.substance.api.colorscheme.LightGrayColorScheme;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.CompositeBorderPainter;
import org.pushingpixels.substance.api.painter.border.DelegateBorderPainter;
import org.pushingpixels.substance.api.painter.border.FlatBorderPainter;
import org.pushingpixels.substance.api.painter.border.GlassBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.ArcDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.api.skin.CremeSkin;

public class SubstanceCSIGSkin extends SubstanceSkin {

	public static final String NAME = "CSIGTheme";

	public SubstanceCSIGSkin () {
		//super();
		
		SubstanceColorScheme activeScheme = new ColorScheme()
		.tint(0.3).named("CSIG");
		SubstanceColorScheme enabledScheme = new ColorScheme();
		SubstanceColorScheme disabledScheme = new ColorScheme().tint(
				0.35).named("CSIG Disabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, enabledScheme, disabledScheme);
		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		this.registerAsDecorationArea(enabledScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
				DecorationAreaType.GENERAL, DecorationAreaType.TOOLBAR);

		/*this.registerAsDecorationArea(disabledScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE_INACTIVE,
				DecorationAreaType.SECONDARY_TITLE_PANE_INACTIVE);*/

		/*setSelectedTabFadeStart(0.2);
		setSelectedTabFadeEnd(0.4);*/

		this.decorationPainter = new FlatDecorationPainter();
		this.borderPainter = new FlatBorderPainter();
		
		//this.fillPainter = new SubstanceCSIGFillPainter();
		
		this.buttonShaper = new ClassicButtonShaper();
		this.fillPainter = new ClassicFillPainter();
		this.highlightPainter = new ClassicHighlightPainter();
		
	}

	@Override
	public String getDisplayName() {
		return NAME;
	}
}
