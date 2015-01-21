package org.sigaim.csig.theme;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;

import org.sigaim.csig.view.helper.ComponentMover;

public class CSIGDialog extends JDialog {

	private final ThemedWindow contentPanel;
	private JLabel lblMessage;
	private JLabel icon;
	
	private int result = -1;
	private JButton btn1;
	private JButton btn2;
	private JButton btn3;
	/**
	 * Create the dialog.
	 */
	public CSIGDialog() {
		final CSIGDialog self = this;
		setUndecorated(true);
		setResizable(true);
		setModal(true);
		contentPanel = new ThemedWindow(this.getRootPane(), true);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setBounds(0, 0, 470, 250);
		//getContentPane().setLayout(new BorderLayout());
		setContentPane(contentPanel);
		//getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(20, 20));
		{
			icon = new JLabel();
			icon.setHorizontalAlignment(SwingConstants.CENTER);
			Border paddingBorder = BorderFactory.createEmptyBorder(20,0,0,0);
			icon.setBorder(paddingBorder);
			contentPanel.add(icon, BorderLayout.NORTH);
		}
		{
			Border paddingBorder = BorderFactory.createEmptyBorder(0,10,10,10);
			lblMessage = new JLabel("",SwingConstants.CENTER);
			lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
			lblMessage.setVerticalAlignment(SwingConstants.CENTER);
			lblMessage.setBorder(paddingBorder);
			contentPanel.add(lblMessage, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
			fl_buttonPane.setVgap(10);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btn1 = new JButton("Yes");
				btn1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						self.result = JOptionPane.YES_OPTION;
						self.dispose();
					}
				});
				btn1.setActionCommand("Yes");
				buttonPane.add(btn1);
				getRootPane().setDefaultButton(btn1);
			}
			{
				btn2 = new JButton("No");
				btn2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						self.result = JOptionPane.NO_OPTION;
						self.dispose();
					}
				});
				btn2.setActionCommand("NO");
				buttonPane.add(btn2);
			}
			{
				btn3 = new JButton("Cancel");
				btn3.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						self.result = JOptionPane.CANCEL_OPTION;
						self.dispose();
					}
				});
				btn3.setActionCommand("Cancel");
				btn3.setName("Button.Gray");
				buttonPane.add(btn3);
			}
		}
	}

	
	public static int showOptions(String[] options, String message, Icon icon) {
		CSIGDialog dia = new CSIGDialog();
		dia.icon.setIcon(icon);
		dia.lblMessage.setText("<html><div style=\"text-align: center;\">" + message + "</html>");
		if(options.length >= 1)
			dia.btn1.setText(options[0]);
		if(options.length >= 2)
			dia.btn2.setText(options[1]);
		if(options.length >= 3)
			dia.btn3.setText(options[2]);
		else
			dia.btn3.setVisible(false);
		ComponentMover.center(dia);
		dia.setVisible(true);
			
		return dia.result;
	}
	
	public static int showYesNo(String txtYes, String txtNo, String message, Icon icon){
		CSIGDialog dia = new CSIGDialog();
		dia.icon.setIcon(icon);
		dia.lblMessage.setText("<html><div style=\"text-align: center;\">" + message + "</html>");
		dia.btn1.setText(txtYes);
		dia.btn2.setText(txtNo);
		dia.btn3.setVisible(false);
		ComponentMover.center(dia);
		dia.setVisible(true);
			
		return dia.result;
	}
	
	public static int showError(String message, String txtButton){
		CSIGDialog dia = new CSIGDialog();
		dia.icon.setIcon(CSIGTheme.iconHelp());
		dia.lblMessage.setText("<html><div style=\"text-align: center;\">" + message + "</html>");
		if(txtButton != null && !txtButton.isEmpty())
			dia.btn1.setText(txtButton);
		else
			dia.btn1.setText("OK");
		dia.btn2.setVisible(false);
		dia.btn3.setVisible(false);
		ComponentMover.center(dia);
		dia.setVisible(true);
		
		return dia.result;
	}


	public static int showYesNoCancel(String txtYes, String txtNo, String txtCancel, String message, Icon icon) {
		CSIGDialog dia = new CSIGDialog();
		dia.icon.setIcon(icon);
		dia.lblMessage.setText("<html><div style=\"text-align: center;\">" + message + "</html>");
		dia.btn1.setText(txtYes);
		dia.btn2.setText(txtNo);
		dia.btn3.setText(txtCancel);
		dia.btn3.setVisible(true);
		ComponentMover.center(dia);
		dia.setVisible(true);
		return 0;
	}
}
