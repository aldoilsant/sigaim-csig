package org.sigaim.csig.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;

import org.sigaim.siie.clients.IntSIIE001EQLClient;
import org.sigaim.siie.iso13606.rm.HealthcareFacility;

public class LoginDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblUsuario;
	private JPasswordField txtPassword;
	private JTextField txtUser;
	
	private ViewController controller;
	private JComboBox ddlCentre;

	/**
	 * Create the dialog.
	 */
	public LoginDialog(ViewController _controller, List<String> hospitals) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		controller = _controller;
		
		setBounds(100, 100, 338, 312);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("94px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("188px:grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				RowSpec.decode("29px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("29px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(13dlu;default)"),}));
		{
			JLabel lblLogo = new JLabel("[SIGAIM logo]");
			lblLogo.setIcon(new ImageIcon(LoginDialog.class.getResource("/org/sigaim/csig/resources/img/logo_trans.png")));
			contentPanel.add(lblLogo, "2, 2, 3, 1");
		}
		{
			lblUsuario = new JLabel("Usuario");
			contentPanel.add(lblUsuario, "2, 4, right, default");
		}
		{
			txtUser = new JTextField();
			contentPanel.add(txtUser, "4, 4, fill, default");
			txtUser.setColumns(10);
		}
		
		JLabel lblContrasea = new JLabel("Contraseña");
		contentPanel.add(lblContrasea, "2, 6, right, center");
		
		txtPassword = new JPasswordField();
		txtPassword.setColumns(10);
		contentPanel.add(txtPassword, "4, 6, fill, default");
		{
			JLabel lblCentro = new JLabel("Centro");
			contentPanel.add(lblCentro, "2, 8, right, center");
		}
		{
			ddlCentre = new JComboBox(hospitals.toArray());
			contentPanel.add(ddlCentre, "4, 8, fill, default");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						controller.doLogin(txtUser.getText(), ddlCentre.getSelectedItem().toString(),  txtPassword.getPassword());
					}
				});
				getRootPane().setDefaultButton(okButton);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.exit(0);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
