package org.sigaim.csig.view;

import java.awt.EventQueue;

import javax.swing.JDialog;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.sigaim.csig.model.IntCSIGModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginCreator extends JDialog {

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginCreator dialog = new LoginCreator();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	private ViewController controller;
	private JLabel lblNewMedic;
	private JLabel lblNewCentre;
	
	/**
	 * Create the dialog.
	 */
	public LoginCreator(ViewController c) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);		
		controller = c;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(167dlu;default)"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JButton btnCreateMedic = new JButton("Create Medic");
		btnCreateMedic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				long medicId = controller.createFacultative();
				if(medicId != -1)
					lblNewMedic.setText(lblNewMedic.getText()+" "+medicId);
			}
		});
		getContentPane().add(btnCreateMedic, "4, 4, default, bottom");
		
		lblNewMedic = new JLabel("New medics:");
		getContentPane().add(lblNewMedic, "6, 4");
		
		JButton btnCreateCentre = new JButton("Create Centre");
		btnCreateCentre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				long centreId = controller.createFacility();
				if(centreId != -1)
					lblNewCentre.setText(lblNewCentre.getText()+" "+centreId);
			}
		});
		getContentPane().add(btnCreateCentre, "4, 6");
		
		lblNewCentre = new JLabel("New centres:");
		getContentPane().add(lblNewCentre, "6, 6");

	}

}
