package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

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

public class LoginDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblUsuario;
	private JPasswordField txtPassword;
	private JTextField txtUser;
	
	private ViewController controller;

	/**
	 * Create the dialog.
	 */
	public LoginDialog(ViewController _controller) {
		controller = _controller;
		setBounds(100, 100, 338, 164);
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
				FormFactory.PARAGRAPH_GAP_ROWSPEC,
				RowSpec.decode("29px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("29px"),}));
		{
			lblUsuario = new JLabel("Usuario");
			contentPanel.add(lblUsuario, "2, 2, right, default");
		}
		{
			txtUser = new JTextField();
			contentPanel.add(txtUser, "4, 2, fill, default");
			txtUser.setColumns(10);
		}
		
		JLabel lblContrasea = new JLabel("Contraseña");
		contentPanel.add(lblContrasea, "2, 4, right, center");
		
		txtPassword = new JPasswordField();
		txtPassword.setColumns(10);
		contentPanel.add(txtPassword, "4, 4, fill, default");
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						controller.doLogin(txtUser.getText(), txtPassword.getPassword());
					}
				});
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
