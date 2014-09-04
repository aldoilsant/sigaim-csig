package org.sigaim.csig.view;

import java.awt.EventQueue;

import javax.swing.JDialog;
import java.awt.GridLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JLabel;

public class WaitModal extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WaitModal dialog = new WaitModal();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public WaitModal() {
		setAlwaysOnTop(true);
		setModal(true);
		setResizable(false);
		setUndecorated(true);
		setBounds(100, 100, 426, 182);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JLabel imgLogo = new JLabel("[Loading animation]");
		getContentPane().add(imgLogo, "2, 2, center, center");
		
		JLabel lblPleaseWait = new JLabel("ESPERE, POR FAVOR...");
		getContentPane().add(lblPleaseWait, "2, 4, center, bottom");
		
		JLabel lblMessage = new JLabel("Esto puede tardar unos minutos");
		getContentPane().add(lblMessage, "2, 6");
		

	}

}
