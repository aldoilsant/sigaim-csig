package org.sigaim.csig.view;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JComboBox;

import org.sigaim.csig.model.CSIGPatient;
import org.sigaim.csig.model.Report;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Dictation extends JPanel {

	private JFrame frame;
	private Report report;
	private ViewController controller;
	private JTextArea txtUnbiased;
	private JTextArea txtBiased;
	private JTextArea txtImpression;
	private JTextArea txtPlan;
	
	//If true dialog asks to save changes before closing
	boolean askSave = false;
	
	//String constants
	static String strAskSave = "¿Quiere guardar los cambios realizados en el informe antes de cerrarlo?";
	static String strNewVersion = "Esto creará una nueva versión del informe"; 
	static String strTitleAskSave = "Confirme antes de cerrar el informe";
	static String strTitleNew = "Dictando nuevo informe";
	static String strTitleEdit = "Revisando informe";
	private JComboBox<String> ddlPatient;
	private JButton btnNewPatient;
	
	/**
	 * Create the panel.
	 */
	public Dictation(Report r, ViewController _controller) {
		frame = new JFrame();
		if(r != null)
			frame.setTitle(strTitleEdit);
		else
			frame.setTitle(strTitleNew);
		controller = _controller;
		report = r;
		initialize();
		if(r != null)
			updateReportView(r);
	}
	
	private void updateReportView(Report r) {
		txtBiased.setText(r.getDictationBiased());
		txtUnbiased.setText(r.getDictationUnbiased());
		txtPlan.setText(r.getDictationPlan());
		txtImpression.setText(r.getDictationImpressions());
	}
	private void showSaveDialog(){
		int response;
		if(report != null)
			response = JOptionPane.showConfirmDialog(frame, new String[]{strAskSave, strNewVersion}, strTitleAskSave,
		        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		else
			response = JOptionPane.showConfirmDialog(frame, strAskSave, strTitleAskSave,
			        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		switch(response) {
			case JOptionPane.YES_OPTION:
				saveReport();
				frame.dispose();
				break;
			case JOptionPane.NO_OPTION:
				frame.dispose();
			/*case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:*/// Do nothing				
		}
	}
	
	private void saveReport(){
		//TODO: implement
		if(report == null) { //New report
			
		}
	}
	
	private void initialize() {
		//On exit ask saving if content changed
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if(report != null) {
		        	if( (!report.getDictationBiased().equals(txtBiased.getText())) ||
		        		(!report.getDictationUnbiased().equals(txtUnbiased.getText())) ||
		        		(!report.getDictationPlan().equals(txtPlan.getText())) ||
		        		(!report.getDictationImpressions().equals(txtImpression.getText())) )
		        		askSave = true;
		        } else {
		        	if( (!txtBiased.getText().isEmpty()) ||
	        			(!txtUnbiased.getText().isEmpty()) ||
	        			(!txtPlan.getText().isEmpty()) ||
	        			(!txtImpression.getText().isEmpty()) )
		        		askSave = true;
		        }
		        if(askSave)
		        	showSaveDialog();
		        else
		        	frame.dispose();
		    }
		});
		//frame.setTitle("Informes");
		frame.setBounds(100, 100, 812, 554);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().removeAll();
		
		/*JPanel pnlDictation = new JPanel();		
		frame.getContentPane().add(pnlDictation);*/
		
		JPanel pnlReportInfo = new JPanel();
		frame.getContentPane().add(pnlReportInfo, BorderLayout.NORTH);
		pnlReportInfo.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(5dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(31dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("411px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(175dlu;default)"),},
			new RowSpec[] {
				RowSpec.decode("4px"),
				RowSpec.decode("max(11dlu;default)"),
				RowSpec.decode("max(4dlu;default)"),}));
		
		JLabel lblPatient = new JLabel("Paciente");
		pnlReportInfo.add(lblPatient, "3, 2, fill, fill");
		
		ddlPatient = new JComboBox<String>();
		pnlReportInfo.add(ddlPatient, "5, 2, fill, default");
		
		btnNewPatient = new JButton("Crear paciente");
		btnNewPatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CSIGPatient newPat = controller.getModelController().createPatient();
				if(newPat != null) {
					ddlPatient.addItem(newPat.toString());
					ddlPatient.setSelectedItem(newPat.toString());
					ddlPatient.setEnabled(false);
					btnNewPatient.setEnabled(false);
					askSave = true;
				}
			}
		});
		pnlReportInfo.add(btnNewPatient, "7, 2, default, bottom");
		JPanel pnlVistaInformes = new JPanel();
		frame.getContentPane().add(pnlVistaInformes);
		pnlVistaInformes.setLayout(new GridLayout(4, 1, 5, 5));
		
		JPanel pnlUnbiased = new JPanel();
		pnlVistaInformes.add(pnlUnbiased);
		
		JLabel lblObjetivo = new JLabel("Objetivo");
		
		JScrollPane scrUnbiased = new JScrollPane();
		GroupLayout gl_pnlUnbiased = new GroupLayout(pnlUnbiased);
		gl_pnlUnbiased.setHorizontalGroup(
			gl_pnlUnbiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlUnbiased.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlUnbiased.createParallelGroup(Alignment.LEADING)
						.addComponent(scrUnbiased, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(lblObjetivo))
					.addContainerGap())
		);
		gl_pnlUnbiased.setVerticalGroup(
			gl_pnlUnbiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlUnbiased.createSequentialGroup()
					.addComponent(lblObjetivo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrUnbiased, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtUnbiased = new JTextArea();
		txtUnbiased.setLineWrap(true);
		scrUnbiased.setViewportView(txtUnbiased);
		pnlUnbiased.setLayout(gl_pnlUnbiased);
		
		JPanel pnlBiased = new JPanel();
		pnlVistaInformes.add(pnlBiased);
		
		JLabel lblSubjetivo = new JLabel("Subjetivo");
		
		JScrollPane scrBiased = new JScrollPane();
		GroupLayout gl_pnlBiased = new GroupLayout(pnlBiased);
		gl_pnlBiased.setHorizontalGroup(
			gl_pnlBiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlBiased.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlBiased.createParallelGroup(Alignment.LEADING)
						.addComponent(scrBiased, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(lblSubjetivo))
					.addContainerGap())
		);
		gl_pnlBiased.setVerticalGroup(
			gl_pnlBiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlBiased.createSequentialGroup()
					.addComponent(lblSubjetivo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrBiased, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtBiased = new JTextArea();
		txtBiased.setLineWrap(true);
		scrBiased.setViewportView(txtBiased);
		pnlBiased.setLayout(gl_pnlBiased);
		
		JPanel pnlImpression = new JPanel();
		pnlVistaInformes.add(pnlImpression);
		
		JLabel lblImpression = new JLabel("Impresión");
		
		JScrollPane scrImpression = new JScrollPane();
		GroupLayout gl_pnlImpression = new GroupLayout(pnlImpression);
		gl_pnlImpression.setHorizontalGroup(
			gl_pnlImpression.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlImpression.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlImpression.createParallelGroup(Alignment.LEADING)
						.addComponent(scrImpression, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(lblImpression))
					.addContainerGap())
		);
		gl_pnlImpression.setVerticalGroup(
			gl_pnlImpression.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlImpression.createSequentialGroup()
					.addComponent(lblImpression)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrImpression, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtImpression = new JTextArea();
		txtImpression.setLineWrap(true);
		scrImpression.setViewportView(txtImpression);
		pnlImpression.setLayout(gl_pnlImpression);
		
		JPanel pnlPlan = new JPanel();
		pnlVistaInformes.add(pnlPlan);
		
		JLabel lblPlan = new JLabel("Plan");
		
		JScrollPane scrPlan = new JScrollPane();
		GroupLayout gl_pnlPlan = new GroupLayout(pnlPlan);
		gl_pnlPlan.setHorizontalGroup(
			gl_pnlPlan.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlPlan.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlPlan.createParallelGroup(Alignment.LEADING)
						.addComponent(scrPlan, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(lblPlan))
					.addContainerGap())
		);
		gl_pnlPlan.setVerticalGroup(
			gl_pnlPlan.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlPlan.createSequentialGroup()
					.addComponent(lblPlan)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrPlan, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtPlan = new JTextArea();
		txtPlan.setLineWrap(true);
		scrPlan.setViewportView(txtPlan);
		pnlPlan.setLayout(gl_pnlPlan);
		
		if(report != null) {
			ddlPatient.setEnabled(false);
			btnNewPatient.setEnabled(false);
			ddlPatient.addItem(report.getPatient().toString());
		} else {
			List<CSIGPatient> patients = controller.getModelController().getPatients();
			ddlPatient.addItem("");
			for(CSIGPatient pat : patients)
				ddlPatient.addItem(pat.toString());
		}
		
		frame.setVisible(true);
	}
}
