package org.sigaim.csig.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.GridLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;

import org.sigaim.csig.view.helper.BackgroundImage;

public class WaitModal extends JDialog {


	private static WaitModal modal;
	private static Thread thread;
	
	
	public static void open() {
		if(modal == null) {
			thread = new Thread() {
				public void run() {
					try {
						modal = new WaitModal();
						modal.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} 
			};
			thread.start();
		}
	}
	public static void close(){
		if(modal != null)
			modal.dispose();
	}
	
	/**
	 * Create the dialog.
	 */
	private WaitModal() {
		setContentPane(new BackgroundImage("/org/sigaim/csig/resources/img/WaitModal.png"));
		setAlwaysOnTop(true);
		setModal(true);
		setResizable(false);
		setUndecorated(true);
		setBackground(new Color(100,100,100,0));
		setBounds(100, 100, 436, 300);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("436px"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("fill:185px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		/*JLabel imgLogo = new JLabel("...");
		getContentPane().add(imgLogo, "1, 2, center, center");*/
		
		JLabel lblPleaseWait = new JLabel("ESPERE, POR FAVOR...");
		getContentPane().add(lblPleaseWait, "1, 4, center, bottom");
		
		JLabel lblMessage = new JLabel("Esto puede tardar unos minutos");
		getContentPane().add(lblMessage, "1, 6, center, bottom");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
				  ((int) (screenSize.getWidth()) - this.getWidth())/2, 
				  ((int) (screenSize.getHeight()) - this.getHeight())/2);
	}
}
