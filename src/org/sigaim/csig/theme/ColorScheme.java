package org.sigaim.csig.theme;

import java.awt.Color;

import org.pushingpixels.substance.api.colorscheme.BaseLightColorScheme;

public class ColorScheme extends BaseLightColorScheme {
	/**
	 * The main ultra-light color.
	 */
	private static final Color mainUltraLightColor = new Color(254, 254, 252);

	/**
	 * The main extra-light color.
	 */
	private static final Color mainExtraLightColor = new Color(238, 243, 230);

	/**
	 * The main light color.
	 */
	private static final Color mainLightColor = new Color(0, 0, 0);

	/**
	 * The main medium color.
	 */
	private static final Color mainMidColor = new Color(227, 228, 219);

	/**
	 * The main dark color.
	 */
	private static final Color mainDarkColor = new Color(179, 182, 176);

	/**
	 * The main ultra-dark color.
	 */
	private static final Color mainUltraDarkColor = new Color(178, 168, 153);

	/**
	 * The foreground color.
	 */
	private static final Color foregroundColor = Color.black;

	/**
	 * Creates a new <code>Creme</code> color scheme.
	 */
	public ColorScheme() {
		super("Creme");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getForegroundColor()
	 */
	@Override
    public Color getForegroundColor() {
		return ColorScheme.foregroundColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getUltraLightColor()
	 */
	@Override
    public Color getUltraLightColor() {
		return ColorScheme.mainUltraLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getExtraLightColor()
	 */
	@Override
    public Color getExtraLightColor() {
		return ColorScheme.mainExtraLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getLightColor()
	 */
	@Override
    public Color getLightColor() {
		return ColorScheme.mainLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getMidColor()
	 */
	@Override
    public Color getMidColor() {
		return ColorScheme.mainMidColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getDarkColor()
	 */
	@Override
    public Color getDarkColor() {
		return ColorScheme.mainDarkColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getUltraDarkColor()
	 */
	@Override
    public Color getUltraDarkColor() {
		return ColorScheme.mainUltraDarkColor;
	}
}
