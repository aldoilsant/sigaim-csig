package org.sigaim.csig.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.sigaim.csig.model.CSIGReport;
import org.sigaim.csig.model.CSIGConcept;

import net.java.balloontip.*;
import net.java.balloontip.styles.BalloonTipStyle;

public class ShowReport extends JPanel {

	private JFrame frame;
	private CSIGReport report;
	private ViewController controller;
	
	private ResourceBundle lang;
	
	private Font conceptFont, conceptFontRed;
	
	private boolean edited = false; //Is any concept edited?
	
	//String constants
	static String strAskSave = "¿Quiere guardar los cambios realizados en el informe antes de cerrarlo?";
	static String strNewVersion = "Esto creará una nueva versión del informe"; 
	static String strTitleAskSave = "Confirme antes de cerrar el informe";
	static String strTitle = "Ver informe";
	private JTextPane txtUnbiased;
	private JTextPane txtBiased;
	private JTextPane txtImpression;
	private JTextPane txtPlan;
	
	/**
	 * Create the panel.
	 */
	public ShowReport(CSIGReport r, ViewController _controller) {
		frame = new JFrame();
		frame.setVisible(false);
		frame.setAutoRequestFocus(false);
		frame.setTitle(strTitle);
		controller = _controller;
		lang = controller.getLang();
		report = r;
		initialize();
		if(r != null)
			updateReportView(r);
		WaitModal.close(this);
		frame.setVisible(true);
		//frame.toFront();
		//this.requestFocus();
	}
	
	private void updateReportView(CSIGReport r) {
		setTextPane(txtBiased, r.getBiased(), r.getBiasedConcepts());
		setTextPane(txtUnbiased, r.getUnbiased(), r.getUnbiasedConcepts());
		setTextPane(txtImpression, r.getImpressions(), r.getImpressionsConcepts());
		setTextPane(txtPlan, r.getPlan(), r.getPlanConcepts());
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
	}
	
	private void setTextPane(JTextPane pane, String text, List<CSIGConcept>concepts){
		int textPointer = 0;
		if(text.length() <= 0)
			return;
//		concepts.sort(new ConceptsOrderer()); 
		pane.setText("");
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
		Map attributes = f.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        conceptFont = f.deriveFont(attributes);
        //conceptFontRed = f.deriveFont(attributes);
		StyledDocument doc = pane.getStyledDocument();
		pane.setFont(f);
		//Style style = doc.addStyle("JLabel", null);
		try{
			for(CSIGConcept c : concepts){
				if(textPointer < c.start)
					doc.insertString(doc.getLength(), text.substring(textPointer, c.start), null);
				pane.insertComponent(createConceptLabel(c, text.substring(c.start, c.end)));
				textPointer = c.end;
			}
			if(textPointer < text.length())
				doc.insertString(doc.getLength(), text.substring(textPointer, text.length()), null);
		} catch(BadLocationException ble) {
			throw new IllegalStateException("Trying to append text to JTextPane", ble);
		}
	}
	
	private JLabel createConceptLabel(final CSIGConcept concept, final String originalText) {
		final JLabel rtn = new JLabel(concept.text);
		rtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rtn.setFont(conceptFont);
		if(!originalText.toLowerCase().equals(concept.text.toLowerCase()))
			rtn.setForeground(new Color(124, 90, 0));
		rtn.setAlignmentY(.80f);
		rtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CSIGConcept con = concept;
				ConceptView c = new ConceptView(concept, originalText, rtn, 
						report.getSynonyms().get(concept.getConceptId()));
			}
		});
		return rtn;
	}

	private void initialize() {
		//On exit ask saving if content changed
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if(edited)
		        	showSaveDialog();
		        else
		        	frame.dispose();
		    }
		});
		frame.setBounds(100, 100, 812, 554);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().removeAll();
		
		JPanel pnlVistaInformes = new JPanel();
		frame.getContentPane().add(pnlVistaInformes);
		pnlVistaInformes.setLayout(new GridLayout(4, 1, 5, 5));
		
		/*BIASED*/
		JPanel pnlBiased = new JPanel();
		pnlVistaInformes.add(pnlBiased);
		
		JLabel lblBiased = new JLabel("Subjetivo");
		
		JScrollPane scrBiased = new JScrollPane();
		GroupLayout gl_pnlBiased = new GroupLayout(pnlBiased);
		gl_pnlBiased.setHorizontalGroup(
			gl_pnlBiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlBiased.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlBiased.createParallelGroup(Alignment.LEADING)
						.addComponent(scrBiased, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(lblBiased))
					.addContainerGap())
		);
		gl_pnlBiased.setVerticalGroup(
			gl_pnlBiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlBiased.createSequentialGroup()
					.addComponent(lblBiased)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrBiased, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtBiased = new JTextPane();
		txtBiased.setEditable(false);
		scrBiased.setViewportView(txtBiased);
		pnlBiased.setLayout(gl_pnlBiased);
		
		JPanel pnlUnbiased = new JPanel();
		pnlVistaInformes.add(pnlUnbiased);
		
		JLabel lblUnbiased = new JLabel("Objetivo");
		
		JScrollPane scrUnbiased = new JScrollPane();
		GroupLayout gl_pnlUnbiased = new GroupLayout(pnlUnbiased);
		gl_pnlUnbiased.setHorizontalGroup(
			gl_pnlUnbiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlUnbiased.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlUnbiased.createParallelGroup(Alignment.LEADING)
						.addComponent(scrUnbiased, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
						.addComponent(lblUnbiased))
					.addContainerGap())
		);
		gl_pnlUnbiased.setVerticalGroup(
			gl_pnlUnbiased.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlUnbiased.createSequentialGroup()
					.addComponent(lblUnbiased)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrUnbiased, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtUnbiased = new JTextPane();
		txtUnbiased.setEditable(false);
		scrUnbiased.setViewportView(txtUnbiased);
		pnlUnbiased.setLayout(gl_pnlUnbiased);
		
		JPanel pnlImpression = new JPanel();
		pnlVistaInformes.add(pnlImpression);
		
		JLabel lblImpression = new JLabel("Impresión médica");
		
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
		
		txtImpression = new JTextPane();
		txtImpression.setEditable(false);
		scrImpression.setViewportView(txtImpression);
		pnlImpression.setLayout(gl_pnlImpression);
		
		JPanel pnlPlan = new JPanel();
		pnlVistaInformes.add(pnlPlan);
		
		JLabel lblPlan = new JLabel("Plan terapéutico");
		
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
		
		txtPlan = new JTextPane();
		txtPlan.setEditable(false);
		scrPlan.setViewportView(txtPlan);
		pnlPlan.setLayout(gl_pnlPlan);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		JPanel pnlActions = new JPanel();
		FlowLayout fl_pnlActions = (FlowLayout) pnlActions.getLayout();
		fl_pnlActions.setAlignment(FlowLayout.RIGHT);
		frame.getContentPane().add(pnlActions, BorderLayout.SOUTH);
		
		final JButton btnAnalyze = new JButton(lang.getString("btnSave"));
		btnAnalyze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				//if(saveReport() == true)
					frame.dispose();
			}
		});
		pnlActions.add(btnAnalyze);
		
		frame.setLocation(
				  ((int) (screenSize.getWidth()) - frame.getWidth())/2, 
				  ((int) (screenSize.getHeight()) - frame.getHeight())/2);
		//frame.setVisible(true);
	}
}
