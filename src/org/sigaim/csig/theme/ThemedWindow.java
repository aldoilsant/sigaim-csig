package org.sigaim.csig.theme;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.sigaim.csig.view.helper.ComponentMover;
import org.sigaim.csig.view.helper.ComponentResizer;

public class ThemedWindow extends javax.swing.JComponent {

	private static final long serialVersionUID = 1L;
	private ImageIcon image;

	private Point initialClick;
	private final JComponent parent;

	public ThemedWindow(String imageUrl) {
		this(imageUrl, null, false);
	}
	public ThemedWindow(String imageUrl, JComponent p, boolean move) {
		image = new ImageIcon(getClass().getResource(imageUrl));
		
		if(p != null)
			parent = p;
		else
			parent = this;
		
		if(move)
			new ComponentMover(parent.getParent().getClass(), parent);
	}
	
	/*public void onInit(){
	 *         ComponentResizer cr = new ComponentResizer();
        cr.setMinimumSize(new Dimension(300, 300));
        cr.setMaximumSize(new Dimension(800, 600));
        cr.registerComponent(parent);
        cr.setSnapSize(new Dimension(5, 5));
	}*/

	@Override
	public void paintComponent(Graphics g) {
		
		Dimension size = getParent().getSize();
		/*ImageIcon image = new ImageIcon(getClass()
				.getResource(imageUrl));*/
		g.drawImage(image.getImage(), 0, 0,
				size.width, size.height, null);
		setOpaque(false);
		super.paintComponent(g);
	}

}