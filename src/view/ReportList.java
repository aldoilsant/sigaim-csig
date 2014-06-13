package view;

import javax.swing.JFrame;
import dataobject.Report;

import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;

import java.awt.FlowLayout;

import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

import java.awt.Rectangle;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class ReportList {

	
	private List<Report> reportList;
	private Report selectedReport;
	
	public JFrame frame;
	private JTextField txtFiltrePatient;
	private JTextField txtFiltreFacultative;
	private JTextField txtKeywords;
	private JPanel pnlVistaInformes;
	private JTable tblInformes;
	private ViewController controller;
	private JTextArea txtReport;
	private JTextArea txtNotes;
	private JButton btnShow;
	private JButton btnReview;
	private JButton btnNew;
	
	public ReportList(JFrame container, ViewController _controller) {
		frame = container;
		controller = _controller;
		reportList = controller.getInforms();
		initialize();
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
		
		JScrollPane pnlListaInformes = new JScrollPane();
		pnlListaInformes.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(pnlListaInformes);
		
		tblInformes = new JTable(new ReportTableModel(reportList));
		/*tblInformes.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null, null},
			},
			new String[] {
				"Creaci\u00F3n", "Ult. versi\u00F3n", "Paciente", "Facultativo", "Informe", "Notas"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				true, true, false, true, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});*/
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
		tblInformes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		ListSelectionModel lsm = tblInformes.getSelectionModel();
		lsm.addListSelectionListener(new TblReportListSelectionHandler());

		pnlListaInformes.setViewportView(tblInformes);
		
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
							.addComponent(scrReport, GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(glInforme.createSequentialGroup()
							.addComponent(lblInformeCompleto, GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
							.addGap(105))))
		);
		glInforme.setVerticalGroup(
			glInforme.createParallelGroup(Alignment.LEADING)
				.addGroup(glInforme.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblInformeCompleto)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrReport, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtReport = new JTextArea();
		scrReport.setViewportView(txtReport);
		txtReport.setEditable(false);
		txtReport.setLineWrap(true);
		pnlInforme.setLayout(glInforme);
		
		
		JPanel pnlNotas = new JPanel();
		panel.add(pnlNotas);
		
		JLabel lblNotas = new JLabel("Notas:");
		lblInformeCompleto.setHorizontalAlignment(SwingConstants.LEFT);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout glNotas = new GroupLayout(pnlNotas);
		glNotas.setHorizontalGroup(
			glNotas.createParallelGroup(Alignment.LEADING)
				.addGroup(glNotas.createSequentialGroup()
					.addContainerGap()
					.addGroup(glNotas.createParallelGroup(Alignment.LEADING)
						.addGroup(glNotas.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(glNotas.createSequentialGroup()
							.addComponent(lblNotas, GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
							.addGap(105))))
		);
		glNotas.setVerticalGroup(
			glNotas.createParallelGroup(Alignment.LEADING)
				.addGroup(glNotas.createSequentialGroup()
					.addComponent(lblNotas)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		txtNotes = new JTextArea();
		scrollPane.setViewportView(txtNotes);
		txtNotes.setEditable(false);
		pnlNotas.setLayout(glNotas);
		
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
		frame.setVisible(true);		
		pnlListaInformes.setVisible(true);
		
	}
	
	private void setSelectedReport(Report i) {
		selectedReport = i;
		txtReport.setText(i.getFullText());
		txtNotes.setText("Notas");
	}
	
	class TblReportListSelectionHandler implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
	        //ListSelectionModel lsm = (ListSelectionModel)e.getSource();

	        int firstIndex = e.getFirstIndex();
	        setSelectedReport(reportList.get(firstIndex));
	        btnReview.setEnabled(true);
	        btnShow.setEnabled(true);
	        
	    }
	}
}
