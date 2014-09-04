package org.sigaim.csig.view;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import es.udc.tic.rnasa.sigaim_transcriptor_client.TranscriptionClientApi;
import es.udc.tic.rnasa.sigaim_transcriptor_client.TranscriptionClientApiImpl;

import javax.swing.JComboBox;

import org.sigaim.csig.model.CSIGPatient;
import org.sigaim.csig.model.Report;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javaFlacEncoder.FLACFileWriter;

import com.jgoodies.forms.layout.Sizes;

import java.awt.FlowLayout;

public class Dictation extends JPanel implements Observer {
	
	private String transcriptionServiceUrl = "193.147.36.199";

	private ResourceBundle lang;
	
	private JFrame frame;
	private Report report;
	private ViewController controller;
	private JTextArea txtUnbiased;
	private JTextArea txtBiased;
	private JTextArea txtImpression;
	private JTextArea txtPlan;
	
	private boolean isRecording = false;
	private boolean isPaused = false;
	private TargetDataLine line;
	private TranscriptionClientApi transcriptor;
	private CaptureThread captureThread;
	
	//If true dialog asks to save changes before closing
	boolean askSave = false;
	
	//String constants
	private JComboBox<String> ddlPatient;
	private JButton btnNewPatient;
	private JButton btnRecord;
	private JButton btnPause;
	
	private MantainCaretFocusListener mantainCaretFocusListener = new MantainCaretFocusListener();
	
	/**
	 * Create the panel.
	 */
	public Dictation(Report r, ViewController _controller) {
		controller = _controller;
		lang = controller.getLang();
		
		frame = new JFrame();
		if(r != null)
			frame.setTitle(lang.getString("Dictation.TitleEdit"));
		else
			frame.setTitle(lang.getString("Dictation.TitleNew"));
		report = r;
		initialize();
		if(r != null)
			updateReportView(r);
	}
	
	private void updateReportView(Report r) {
		txtBiased.setText(r.getBiased());
		txtUnbiased.setText(r.getUnbiased());
		txtPlan.setText(r.getPlan());
		txtImpression.setText(r.getImpressions());
	}
	private void showSaveDialog(){
		int response;
		if(report != null)
			response = JOptionPane.showConfirmDialog(frame, new String[]{lang.getString("Dictation.AskSave"),
					lang.getString("Dictation.NewVersion")}, lang.getString("TitleAskSave"),
		        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		else
			response = JOptionPane.showConfirmDialog(frame, lang.getString("Dictation.AskSave"), lang.getString("TitleAskSave"),
			        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		switch(response) {
			case JOptionPane.YES_OPTION:
				frame.dispose();
				saveReport();				
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
			if(ddlPatient.getSelectedIndex() > 0)
			controller.createReport(txtBiased.getText(), txtUnbiased.getText(), txtImpression.getText(), txtPlan.getText(),
					(String)ddlPatient.getSelectedItem());
			
		}
	}
	
	private void startRecord() {
		if(line == null)
			line = controller.getLine();
		try{
			captureThread = new CaptureThread();
			captureThread.start();
			isRecording = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void stopRecord() {
		line.stop();
		line.close();
		isRecording = false;
       
        Path path = Paths.get("session.flac").toAbsolutePath();
        //Path path = Paths.get("C:/Users/siro.gonzalez/workspace/SIGAIM_csig/resources", "es-0020.flac");
        System.out.println("Sending audio and waiting for transcription...");
        transcriptor.transcribeFlac(path);
	}
	
	private void switchRecord(){
		if(transcriptor == null) {
			transcriptor = new TranscriptionClientApiImpl();
			if( transcriptor.connect(new InetSocketAddress(transcriptionServiceUrl,8000)) != true) {
				System.out.println("Error connecting to transcription service");
				return;
			}
			transcriptor.addObserver(this);	
		}
		if(isRecording) {
			stopRecord();
			btnRecord.setText(lang.getString("Dictation.btnStartRecord"));
			btnPause.setEnabled(false);
			btnPause.setText(lang.getString("Dictation.btnPauseRecord"));
			isPaused = false;
		} else {
			startRecord();
			btnRecord.setText(lang.getString("Dictation.btnStopRecord"));
			//btnPause.setEnabled(true);
		}
	}
	
	private void switchPause() {
		if(isPaused) {
			startRecord();
			isPaused = false;
			btnPause.setText(lang.getString("Dictation.btnPauseRecord"));
		} else {
			isPaused = true;
			stopRecord();
			btnPause.setText(lang.getString("Dictation.btnContinueRecord"));
		}
	}

	private void initialize() {
		//On exit ask saving if content changed
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if(report != null) {
		        	if( (!report.getBiased().equals(txtBiased.getText())) ||
		        		(!report.getUnbiased().equals(txtUnbiased.getText())) ||
		        		(!report.getPlan().equals(txtPlan.getText())) ||
		        		(!report.getImpressions().equals(txtImpression.getText())) )
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
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.constant("5dlu", true), Sizes.constant("5dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(31dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.MINIMUM, Sizes.constant("100dlu", true), Sizes.constant("200dlu", true)), 1),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(90dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(90dlu;default)"),
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.constant("5dlu", true), Sizes.constant("5dlu", true)), 0),},
			new RowSpec[] {
				RowSpec.decode("4px"),
				RowSpec.decode("max(11dlu;default)"),
				RowSpec.decode("max(4dlu;default)"),}));
		
		JLabel lblPatient = new JLabel(lang.getString("lblPatient"));
		pnlReportInfo.add(lblPatient, "3, 2, fill, fill");
		
		ddlPatient = new JComboBox<String>();
		pnlReportInfo.add(ddlPatient, "5, 2, fill, default");
		
		btnNewPatient = new JButton(lang.getString("Dictation.btnCreatePatient"));
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
		
		btnPause = new JButton(lang.getString("Dictation.btnPauseRecord"));
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {switchPause();}
		});
		btnPause.setEnabled(false);
		pnlReportInfo.add(btnPause, "11, 2");
		
		btnRecord = new JButton(lang.getString("Dictation.btnStartRecord"));
		btnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchRecord();
			}
		});
		pnlReportInfo.add(btnRecord, "13, 2");
		JPanel pnlVistaInformes = new JPanel();
		frame.getContentPane().add(pnlVistaInformes);
		pnlVistaInformes.setLayout(new GridLayout(4, 1, 5, 5));
		
		JPanel pnlBiased = new JPanel();
		pnlVistaInformes.add(pnlBiased);
		
		JLabel lblSubjetivo = new JLabel(lang.getString("lblBiased"));
		
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
		
		JPanel pnlUnbiased = new JPanel();
		pnlVistaInformes.add(pnlUnbiased);
		
		JLabel lblObjetivo = new JLabel(lang.getString("lblUnbiased"));
		
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
		
		JPanel pnlImpression = new JPanel();
		pnlVistaInformes.add(pnlImpression);
		
		JLabel lblImpression = new JLabel(lang.getString("lblImpression"));
		
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
		
		JLabel lblPlan = new JLabel(lang.getString("lblPlan"));
		
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
		
		mantainCaretFocusListener.addTextArea(txtBiased);
		mantainCaretFocusListener.addTextArea(txtUnbiased);
		mantainCaretFocusListener.addTextArea(txtImpression);
		mantainCaretFocusListener.addTextArea(txtPlan);
		
		JPanel pnlActions = new JPanel();
		FlowLayout fl_pnlActions = (FlowLayout) pnlActions.getLayout();
		fl_pnlActions.setAlignment(FlowLayout.RIGHT);
		frame.getContentPane().add(pnlActions, BorderLayout.SOUTH);
		
		final JButton btnAnalyze = new JButton(lang.getString("Dictation.btnAnalyze"));
		btnAnalyze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if(ddlPatient.getSelectedIndex() > 0) {
					btnAnalyze.setEnabled(false);
					saveReport();
					frame.dispose();
				} //TODO: else show error
			}
		});
		pnlActions.add(btnAnalyze);
		
		frame.setVisible(true);
	}
	
	private void translate() {
		
	}
	
	//Observer for transcription service
	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("Receiving transcription");
		Path transcription = (Path) arg1;
		Charset charset = Charset.forName("UTF-8");
		
		try (BufferedReader reader = Files.newBufferedReader(transcription, charset))
		{
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				//System.out.println(line);
				JTextArea txt = mantainCaretFocusListener.getLastTextArea();
				if(txt == null)
					System.err.println("No text area selected");
				else {
					txt.insert(line, txt.getCaretPosition());
				}
			}
		} catch (IOException x)
		{
			System.err.println("Error reading transcription file");
			x.printStackTrace();
		}
	}
	
	class CaptureThread extends Thread{
		private boolean isPaused;
		
		public void run(){
			btnPause.setEnabled(true);
			try{
				line.open(line.getFormat());
				line.start();
				AudioSystem.write(
						new AudioInputStream(line),
						FLACFileWriter.FLAC,//fileType,
						new File("session.flac"));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}
}
