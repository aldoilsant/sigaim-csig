package org.sigaim.csig.view.helper;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class BackgroundImage extends javax.swing.JComponent {

	private static final long serialVersionUID = 1L;
	private ImageIcon image;

	private Point initialClick;
	private final JComponent parent;

	public BackgroundImage(String imageUrl) {
		parent = this;
		image = new ImageIcon(getClass().getResource(imageUrl));
		onInit();
	}
	public BackgroundImage(String imageUrl, JComponent p) {
		parent = this;
		image = new ImageIcon(getClass().getResource(imageUrl));
		onInit();
	}
	
	public void onInit(){
		/*addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	        	initialClick = e.getPoint();
	        }
	    });

	    addMouseMotionListener(new MouseAdapter() {
	        public void mouseDragged(MouseEvent evt) {
	            //sets frame position when mouse dragged            
	            Rectangle rectangle = getBounds();
	            parent.setBounds(evt.getXOnScreen() - initialClick.x, evt.getYOnScreen() - initialClick.y, rectangle.width, rectangle.height);
	        }
	    });
		
		*/addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            initialClick = e.getPoint();
	            getComponentAt(initialClick);
	        }
	    });

	    addMouseMotionListener(new MouseMotionAdapter() {
	        @Override
	        public void mouseDragged(MouseEvent e) {

	            // get location of Window
	            int thisX = parent.getLocation().x;
	            int thisY = parent.getLocation().y;

	            // Determine how much the mouse moved since the initial click
	            int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
	            int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

	            // Move window to this position
	            int X = thisX + xMoved;
	            int Y = thisY + yMoved;
	            parent.setLocation(thisX+e.getX(), thisY+e.getY());
	        }
	    });
	}

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