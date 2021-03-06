package org.sigaim.csig.theme;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.sigaim.csig.view.helper.ComponentMover;
import org.sigaim.csig.view.helper.ComponentResizer;

public class ThemedWindow extends javax.swing.JComponent {

	private static final long serialVersionUID = 1L;
	private BufferedImage image = null;
	private boolean titleMode = false;
	
	//Title bar support
	private JFrame mainFrame = null;

	//private Point initialClick;
	private Component parent;
	
	public ThemedWindow(){
		this(null);
	}
	public ThemedWindow(java.awt.Component _parent, boolean move){
		this(null, _parent, move);
	}
	
	public ThemedWindow(boolean move){
		this(null, null, move);
	}

	public ThemedWindow(String imageUrl) {
		this(imageUrl, null, false);
	}
	public ThemedWindow(String imageUrl, java.awt.Component p, boolean move) {
		if(imageUrl != null)
			try {
				image = ImageIO.read(getClass().getResource(imageUrl));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		if(p instanceof Frame)
			((Frame)p).setUndecorated(true);
		
		setOpaque(false);
		setBackground(new Color(100,100,100,0));
		if(p != null) {
			p.setBackground(new Color(100,100,100,0));
			parent = p;
		} else
			parent = this;
		
		if(move){
			Component container = parent.getParent();
			if(container == null)
				container = parent;
			new ComponentMover(container.getClass(), parent);
		}
	}
	
	//When true image will be printed in top as if it was a title bar
	public void setTitleBarMode(boolean b){
		titleMode = true;
	}
	
	public void setTitleBar(JPanel titleBar){
		mainFrame = new JFrame();
		//mainFrame.setUndecorated(true);
		mainFrame.setResizable(true);
		ThemedWindow contentPanel = new ThemedWindow(mainFrame, false);
		contentPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		mainFrame.setContentPane(contentPanel);
		mainFrame.setLayout(new BorderLayout(10,10));
        
        mainFrame.getContentPane().add(titleBar, BorderLayout.NORTH);
		mainFrame.getContentPane().add(parent, BorderLayout.CENTER);
		
		parent = mainFrame;
		new ComponentMover(mainFrame, titleBar);
		ComponentResizer cr = new ComponentResizer();
        cr.setMinimumSize(new Dimension(300, 300));
        //cr.setMaximumSize(new Dimension(800, 600));
        cr.registerComponent(mainFrame);
        cr.setSnapSize(new Dimension(10, 10));
	}

	
	protected void paintComponent(Graphics g) {
		if(mainFrame != null) return;
		if(!titleMode && image != null){
				Dimension size = getParent().getSize();
				g.drawImage(image, 0, 0,
						size.width, size.height, null);
				super.paintComponent(g);
		} else {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(ColorScheme.backgroundColor);
			g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

			g2.setStroke(new BasicStroke(1f));
			g2.setColor(getForeground());
			if(titleMode)
				titleBarPainter(image, g);
			g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
			g2.dispose();
		}
	}
	
	private void titleBarPainter(BufferedImage titleImage, Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(this.getBackground());
		g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

		if (titleImage != null) {
            //int x = (getWidth() - logo.getWidth()) / 2;
            //int y = (getHeight() - logo.getHeight()) / 2;
            g2.drawImage(titleImage, 0, 0, titleImage.getWidth(), titleImage.getHeight(), null);
        }
		
		g2.dispose();		
	}
	@Override
	public void setVisible(boolean b){
		if(mainFrame != null){
			ComponentMover.center(mainFrame);
			mainFrame.setVisible(b);
		} else {
			super.setVisible(b);
		}
	}
	
	@Override
	public void setSize(int a, int b){
		if(mainFrame != null)
			mainFrame.setSize(a,b);
		else
			super.setSize(a,b);
	}
	
	public void toFront(){
		if(mainFrame != null)
			mainFrame.toFront();
		//TODO: else exception
	}
	
	public void dispose() {
		if(mainFrame != null)
			mainFrame.dispose();
		//TODO: else exception		
	}
	
	public JFrame getMainFrame(){
		return mainFrame;
	}
	
	public static JPanel getDefaultTitleBar(){
		
		JPanel titleBar = new JPanel() {
			BufferedImage logo = ColorScheme.titleBarLogo();
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(this.getBackground());
				g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

				if (logo != null) {
		            //int x = (getWidth() - logo.getWidth()) / 2;
		            //int y = (getHeight() - logo.getHeight()) / 2;
		            g2.drawImage(logo, 0, 0, logo.getWidth(), logo.getHeight(), null);
		        }
				
				g2.dispose();	
			}
		};
		titleBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		titleBar.setBackground(ColorScheme.titleBackground);
		return titleBar;
	}
	public void addWindowListener(WindowAdapter windowAdapter) {
		mainFrame.addWindowListener(windowAdapter);
	}
	
}