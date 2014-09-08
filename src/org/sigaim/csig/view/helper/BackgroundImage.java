package org.sigaim.csig.view.helper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;

public class BackgroundImage extends javax.swing.JComponent {

	private static final long serialVersionUID = 1L;
	private ImageIcon image;

	public BackgroundImage(String imageUrl) {
		image = new ImageIcon(getClass().getResource(imageUrl));
	}

	@Override
	public void paintComponent(Graphics g){
		Dimension size = getParent().getSize();
		/*ImageIcon image = new ImageIcon(getClass()
				.getResource(imageUrl));*/
		g.drawImage(image.getImage(), 0, 0,
				size.width, size.height, null);
		setOpaque(false);
		super.paintComponent(g);
	}
}