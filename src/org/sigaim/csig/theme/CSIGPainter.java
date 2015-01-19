package org.sigaim.csig.theme;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;

public class CSIGPainter extends SynthPainter {

    private static final int ARC = 22;

    public void paintTableHeaderBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
    	
    	Graphics2D g2 = (Graphics2D) g;
    	g2.setStroke(new BasicStroke(2));
    	g.setColor(ColorScheme.tableBorderColor);
    	g2.drawLine(0, 0, w, 0);
    	g2.drawLine(0, h, w, h);
    	g2.dispose();
    }
    
    public void paintTextFieldBorder(Component component, Graphics g, int a,
            int b, int c, int d){
    	paintBorder(component, g, a, b, c, d);
    }
    
    public void paintBorder(Component component, Graphics g, int a,
                                    int b, int c, int d) {

        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Double r =
            new RoundRectangle2D.Double (a+(ARC / 2), b+(ARC/2), c-ARC, d-ARC,
            ARC, ARC);

        if (component instanceof JScrollPane) {
            component = ((JScrollPane) component).getViewport().getView();
        }
        Color bg = component.getBackground();

        Color outer = UIManager.getColor("control");

        g2d.setRenderingHints(HINTS);

        g2d.setColor (outer);
        g2d.fillRect (a, b, c, d);

        g2d.setPaint (bg);
        g2d.fill(r);

        Shape clip = g2d.getClip();

        Rectangle withoutBottom = new Rectangle (a, b, c, d - ARC);

        if (clip != null) {
            Area area = new Area (clip);
            area.intersect(new Area(withoutBottom));
            g.setClip (area);
        } else {
            g.setClip (withoutBottom);
        }

        double amt = 0.65d;
        int blu = 5;

        int[] colors = new int[] { 125, 160, 180, 215, 232, 242 };
        int red = bg.getRed();
        int green = bg.getGreen();
        int blue = bg.getBlue();
        double ttl = 0;
        Color first = null;
        for (int i=0; i < colors.length; i++) {
            g2d.translate (0d, amt);
            ttl += amt;
            int ra = red - (255 - colors[i]);
            int ga = green - (255 - colors[i]);
            int ba = blue - (255 - colors[i]) + blu;
            Color col = new Color (ra, ga, ba);
            g2d.setPaint (col);
            g2d.draw(r);
            if (i == colors.length /2) {
                first = col;
            }
        }
        g2d.translate (0d, -ttl);

        Rectangle bottom = new Rectangle (a, b + (d - ARC), c, ARC/2);

        if (clip != null) {
            Area area = new Area (clip);
            area.intersect(new Area(bottom));
            g.setClip (area);
        } else {
            g.setClip (bottom);
        }
        g2d.translate (0, -amt);
        g2d.setPaint (new GradientPaint (0, (b+d)-ARC, first, 0, b+(d-(ARC/2)), Color.WHITE));
        g2d.draw(r);

        Composite comp = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        g2d.translate (0, -amt);
        g2d.setPaint (new GradientPaint (0, (b+d)-ARC, first, 0, b+(d-(ARC/2)), Color.WHITE));
        g2d.draw(r);

        g2d.setClip (clip);
        g2d.translate (0, amt * 2);
        g2d.setComposite (comp);
    }

    private static final Map HINTS = new HashMap();
    static {
        HINTS.put (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    
}

