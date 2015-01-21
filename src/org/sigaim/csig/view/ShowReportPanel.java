package org.sigaim.csig.view;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentListener;

/*
 * The separation betweeen ShowReport & ShowReportPanel is for simplifying and to help graphical editors
 * to understand the panes for faster edition.
 */
public class ShowReportPanel extends JPanel {

	public JTextPane txtPlan;
	public JTextPane txtBiased;
	public JTextPane txtUnbiased;
	public JTextPane txtImpression;

	/**
	 * Create the panel.
	 */
	public ShowReportPanel(DocumentListener textChangeListener) {
		setLayout(new GridLayout(4, 1, 5, 0));

		/*BIASED*/
		JPanel pnlBiased = new JPanel();		
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
		txtBiased.getDocument().addDocumentListener(textChangeListener);
		scrBiased.setViewportView(txtBiased);
		pnlBiased.setLayout(gl_pnlBiased);

		JPanel pnlUnbiased = new JPanel();

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
		txtUnbiased.getDocument().addDocumentListener(textChangeListener);
		scrUnbiased.setViewportView(txtUnbiased);
		pnlUnbiased.setLayout(gl_pnlUnbiased);

		JPanel pnlImpression = new JPanel();

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
		txtImpression.getDocument().addDocumentListener(textChangeListener);
		scrImpression.setViewportView(txtImpression);
		pnlImpression.setLayout(gl_pnlImpression);

		JPanel pnlPlan = new JPanel();

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
		txtPlan.getDocument().addDocumentListener(textChangeListener);
		scrPlan.setViewportView(txtPlan);
		pnlPlan.setLayout(gl_pnlPlan);

		add(pnlBiased);
		add(pnlUnbiased);
		add(pnlImpression);
		add(pnlPlan);
	}

}
