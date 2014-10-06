package org.sigaim.csig.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
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
import org.sigaim.csig.view.helper.FontHelper;

public class WaitModal extends JDialog {


	private static WaitModal modal;
	private static Thread thread;
	
	private JLabel lblMessage;
	private static String message = "Esto puede tardar unos minutos";
	
	
	public static void open(String msg){
		message = msg;
		if(modal==null)
			open();
		else
			setMessage(msg);
	};
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
		if(modal != null){
			modal.dispose();
			modal = null;
		}
	}
	public static void close(JComponent src){
		if(modal!=null){
			modal.setLocationRelativeTo(src);
			close();
		}
	}
	public static void setMessage(String msg){
		message = msg;
		if(modal!=null && modal.lblMessage!=null) {
			modal.lblMessage.setText(msg);
		}
	}
	public static JDialog getModal(){
		return modal;
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
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		ImageIcon imgWait = new ImageIcon("prueba.gif");
		JLabel lblwait = new JLabel("");
		lblwait.setIcon(imgWait);
		getContentPane().add(lblwait, "1, 2, center, center");
		
		Font lato = FontHelper.getTTF("Lato-Regular.ttf", 16);
		Font lato_small = FontHelper.getTTF("Lato-Regular.ttf", 12);
		JLabel lblPleaseWait = new JLabel("ESPERE, POR FAVOR");
		lblPleaseWait.setFont(lato);
		getContentPane().add(lblPleaseWait, "1, 4, center, bottom");
		
		lblMessage = new JLabel(message);
		lblMessage.setFont(lato_small);
		getContentPane().add(lblMessage, "1, 6, center, bottom");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
				  ((int) (screenSize.getWidth()) - this.getWidth())/2, 
				  ((int) (screenSize.getHeight()) - this.getHeight())/2);
	}
}
