package org.sigaim.csig.theme;

import java.awt.Color;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.fill.StandardFillPainter;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

public class SubstanceCSIGFillPainter extends StandardFillPainter {
		/**
		 * Reusable instance of this painter.
		 */
		public static final ClassicFillPainter INSTANCE = new ClassicFillPainter();

		public SubstanceCSIGFillPainter() {
		}

		@Override
		public String getDisplayName() {
			return "CSIG";
		}

		@Override
		public Color getTopFillColor(SubstanceColorScheme fillScheme) {
			return new Color(0,0,0);
		}

		@Override
		public Color getMidFillColorTop(SubstanceColorScheme fillScheme) {
			return new Color(0,0,0);
			/*return SubstanceColorUtilities.getInterpolatedColor(super
					.getMidFillColorTop(fillScheme), super
					.getBottomFillColor(fillScheme), 0.7);*/
		}
}
