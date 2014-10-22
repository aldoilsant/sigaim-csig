package org.sigaim.csig.view;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;

import java.awt.FlowLayout;

import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;

import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;

import org.sigaim.csig.model.CSIGReport;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class ReportList {

	
	private List<CSIGReport> reportList;
	private List<CSIGReport> versionList;
	private CSIGReport selectedReport;
	
	public JFrame frame;
	private JTextField txtFiltrePatient;
	private JTextField txtFiltreFacultative;
	private JTextField txtKeywords;
	private JPanel pnlVistaInformes;
	private JTable tblInformes;
	private ViewController controller;
	private JTextArea txtReport;
	private JButton btnShow;
	private JButton btnReview;
	private JButton btnNew;
	private JTable tblVersions;
	
	private TableModel reportTableModel;
	
	public ReportList(JFrame container, ViewController _controller) {
		frame = container;
		controller = _controller;
		reportList = controller.getReports();
		initialize();
	}
	
	public void updateList(List<CSIGReport> newList){
		reportList = newList;
		tblInformes.setModel(new ReportTableModel(reportList));
		tblInformes.getColumnModel().getColumn(0).setPreferredWidth(100);
		tblInformes.getColumnModel().getColumn(0).setMinWidth(100);
		tblInformes.getColumnModel().getColumn(0).setMaxWidth(100);
		tblInformes.getColumnModel().getColumn(1).setPreferredWidth(100);
		tblInformes.getColumnModel().getColumn(1).setMinWidth(100);
		tblInformes.getColumnModel().getColumn(1).setMaxWidth(100);
		tblInformes.getColumnModel().getColumn(2).setResizable(false);
		tblInformes.getColumnModel().getColumn(2).setPreferredWidth(200);
		tblInformes.getColumnModel().getColumn(2).setMinWidth(200);
		tblInformes.getColumnModel().getColumn(2).setMaxWidth(200);
		tblInformes.getColumnModel().getColumn(3).setPreferredWidth(200);
		tblInformes.getColumnModel().getColumn(3).setMinWidth(200);
		tblInformes.getColumnModel().getColumn(3).setMaxWidth(200);
	}
	public void updateVersionList(List<CSIGReport> newList){
		versionList = newList;
		tblVersions.setModel( new ReportVersionTableModel(versionList));
		tblVersions.getColumnModel().getColumn(0).setPreferredWidth(20);
		tblVersions.getColumnModel().getColumn(0).setMinWidth(20);
		int lastrow = tblVersions.getRowCount()-1;
		if(lastrow > 0)
			tblVersions.setRowSelectionInterval(lastrow, lastrow);
	}
	
	public void show() {
		frame.removeAll();
		frame.getContentPane().add(pnlVistaInformes);
		frame.setVisible(true);
	}

	private void initialize() {
		//frame = new JFrame();
		frame.getContentPane().removeAll();
		frame.setTitle("Informes");
		frame.setBounds(100, 100, 812, 554);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		pnlVistaInformes = new JPanel();
		pnlVistaInformes.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		pnlVistaInformes.add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel pnlInformList = new JPanel();
		panel.add(pnlInformList);
		
		JScrollPane scrListaInformes = new JScrollPane();
		scrListaInformes.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		//reportTableModel = ;
		
		tblInformes = new JTable();
		updateList(reportList);  //Set TableModel and column sizes
		tblInformes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		ListSelectionModel lsm = tblInformes.getSelectionModel();
		lsm.addListSelectionListener(new TblReportListSelectionHandler(tblInformes));
		
				scrListaInformes.setViewportView(tblInformes);
				GroupLayout gl_pnlInformList = new GroupLayout(pnlInformList);
				gl_pnlInformList.setHorizontalGroup(
					gl_pnlInformList.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnlInformList.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrListaInformes, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
							.addContainerGap())
				);
				gl_pnlInformList.setVerticalGroup(
					gl_pnlInformList.createParallelGroup(Alignment.LEADING)
						.addComponent(scrListaInformes, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
				);
				pnlInformList.setLayout(gl_pnlInformList);
				scrListaInformes.setVisible(true);
		
		JPanel pnlVersions = new JPanel();
		panel.add(pnlVersions);
		
		JLabel lblVersionesDelInforme = new JLabel("Versiones del informe:");
		
		JScrollPane scrVersions = new JScrollPane();
		GroupLayout gl_pnlVersions = new GroupLayout(pnlVersions);
		gl_pnlVersions.setHorizontalGroup(
			gl_pnlVersions.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlVersions.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlVersions.createParallelGroup(Alignment.LEADING)
						.addComponent(lblVersionesDelInforme)
						.addComponent(scrVersions, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_pnlVersions.setVerticalGroup(
			gl_pnlVersions.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlVersions.createSequentialGroup()
					.addComponent(lblVersionesDelInforme)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrVersions, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		tblVersions = new JTable();
		updateVersionList(new ArrayList<CSIGReport>());
		scrVersions.setViewportView(tblVersions);
		pnlVersions.setLayout(gl_pnlVersions);
		
		JPanel pnlInforme = new JPanel();
		panel.add(pnlInforme);
		
		JLabel lblInformeCompleto = new JLabel("Informe Completo:");
		lblInformeCompleto.setHorizontalAlignment(SwingConstants.LEFT);
		
		JScrollPane scrReport = new JScrollPane();
		GroupLayout glInforme = new GroupLayout(pnlInforme);
		glInforme.setHorizontalGroup(
			glInforme.createParallelGroup(Alignment.LEADING)
				.addGroup(glInforme.createSequentialGroup()
					.addContainerGap()
					.addGroup(glInforme.createParallelGroup(Alignment.LEADING)
						.addGroup(glInforme.createSequentialGroup()
							.addComponent(scrReport, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(glInforme.createSequentialGroup()
							.addComponent(lblInformeCompleto, GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
							.addGap(105))))
		);
		glInforme.setVerticalGroup(
			glInforme.createParallelGroup(Alignment.LEADING)
				.addGroup(glInforme.createSequentialGroup()
					.addGap(6)
					.addComponent(lblInformeCompleto)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrReport, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtReport = new JTextArea();
		scrReport.setViewportView(txtReport);
		txtReport.setEditable(false);
		txtReport.setLineWrap(true);
		pnlInforme.setLayout(glInforme);
		lblInformeCompleto.setHorizontalAlignment(SwingConstants.LEFT);
		
		JToolBar toolFilter = new JToolBar();
		toolFilter.setFloatable(false);
		toolFilter.setBounds(new Rectangle(10, 10, 1, 1));
		pnlVistaInformes.add(toolFilter, BorderLayout.SOUTH);
		
		JLabel lblFiltraPaciente = new JLabel("Paciente");
		toolFilter.add(lblFiltraPaciente);
		txtFiltrePatient = new JTextField();
		toolFilter.add(txtFiltrePatient);
		toolFilter.addSeparator();
		
		JLabel lblFacultativo = new JLabel("Facultativo");
		toolFilter.add(lblFacultativo);		
		txtFiltreFacultative = new JTextField();
		toolFilter.add(txtFiltreFacultative);
		toolFilter.addSeparator();
		
		JLabel lblPalabrasClave = new JLabel("Palabras Clave");
		toolFilter.add(lblPalabrasClave);
		
		txtKeywords = new JTextField();
		toolFilter.add(txtKeywords);
		txtKeywords.setColumns(10);
		
		frame.getContentPane().add(pnlVistaInformes);
		
		JPanel pnlActions = new JPanel();
		pnlVistaInformes.add(pnlActions, BorderLayout.NORTH);
		pnlActions.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		btnShow = new JButton("Ver");
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.showReport(selectedReport);
			}
		});
		btnShow.setEnabled(false);
		pnlActions.add(btnShow);
		
		btnReview = new JButton("Revisar/Editar");
		btnReview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.openReport(selectedReport);
			}
		});
		btnReview.setEnabled(false);
		pnlActions.add(btnReview);
		
		btnNew = new JButton("Nuevo");
		btnNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.newReport();
			}
		});
		pnlActions.add(btnNew);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(
				  ((int) (screenSize.getWidth()) - frame.getWidth())/2, 
				  ((int) (screenSize.getHeight()) - frame.getHeight())/2);
		frame.setVisible(true);		
		
	}
	
	private void setSelectedReport(CSIGReport i) {
		selectedReport = i;
		if(selectedReport == null) {
			txtReport.setText("");
			versionList = new ArrayList<CSIGReport>(0);
		} else {
			txtReport.setText(i.getFullText());
			versionList = controller.getModelController().getVersions(i);	
		}
		updateVersionList(versionList);
	}
	
	class TblReportListSelectionHandler implements ListSelectionListener {
		int selectedIndex = -1;
		JTable table;

		public TblReportListSelectionHandler(JTable parent) {
			super();
			table = parent;			
		}
		
		@Override
	    public void valueChanged(ListSelectionEvent e) {
			//ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if(selectedIndex != table.getSelectedRow()){
				if(table.getSelectedRow() < 0) {
					selectedIndex = table.getSelectedRow();
					setSelectedReport(null);
				}
				else {
					selectedIndex = table.getSelectedRow();
					setSelectedReport(reportList.get(selectedIndex));
					btnReview.setEnabled(true);
					btnShow.setEnabled(true);
				}
			}
	        
	    }
	}
}
