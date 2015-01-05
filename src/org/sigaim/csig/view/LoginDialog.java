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

import org.sigaim.csig.view.helper.TextPrompt;
import java.awt.GridLayout;

public class LoginDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
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

		setBounds(100, 100, 331, 361);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("188px:grow"),
				ColumnSpec.decode("12dlu"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblLogo = new JLabel("[SIGAIM logo]");
		lblLogo.setIcon(new ImageIcon(LoginDialog.class.getResource("/org/sigaim/csig/resources/img/logo_trans.png")));
		contentPanel.add(lblLogo, "2, 2, center, center");

		txtUser = new JTextField();
		TextPrompt tpUser = new TextPrompt("Usuario", txtUser);
		contentPanel.add(txtUser, "2, 6, fill, default");
		txtUser.setColumns(10);

		txtPassword = new JPasswordField();
		TextPrompt tpPassword = new TextPrompt("Contraseña", txtPassword);
		txtPassword.setColumns(10);
		contentPanel.add(txtPassword, "2, 8, fill, default");

		ddlCentre = new JComboBox(hospitals.toArray());
		contentPanel.add(ddlCentre, "2, 10, fill, default");

		JPanel buttonPane = new JPanel();
		contentPanel.add(buttonPane, "2, 12, fill, fill");

		JButton okButton = new JButton("OK");
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.doLogin(txtUser.getText(), ddlCentre.getSelectedItem().toString(),  txtPassword.getPassword());
			}
		});
		buttonPane.setLayout(new GridLayout(0, 2, 0, 0));
		getRootPane().setDefaultButton(okButton);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setName("Button.Gray");
		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		okButton.requestFocus();
	}
}
