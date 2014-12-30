package org.sigaim.csig.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javaFlacEncoder.FLACFileWriter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;

import org.sigaim.csig.model.CSIGPatient;
import org.sigaim.csig.model.CSIGReport;
import org.sigaim.csig.persistence.PersistenceManager;
import org.sigaim.csig.persistence.PersistentObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import es.udc.tic.rnasa.sigaim_transcriptor.client.TranscriptionClientApi;
import es.udc.tic.rnasa.sigaim_transcriptor.client.TranscriptionClientApiImpl;
import es.udc.tic.rnasa.sigaim_transcriptor.client.util.TranscriptionBundle;
import es.udc.tic.rnasa.sigaim_transcriptor.client.util.TranscriptionStateEvent;
import es.udc.tic.rnasa.sigaim_transcriptor.client.ui.event.*;

public class Dictation extends JPanel implements TranscriptionListener, PersistentObject {
	
	//private String transcriptionServiceUrl = "193.147.36.199";

	private ResourceBundle lang;
	private long uuid = (new Date()).getTime();
	
	private JFrame frame;
	private CSIGReport report;
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
	
	//Persistence manager
	public Dictation(ViewController _controller){
		controller = _controller;
		lang = controller.getLang();
		
		frame = new JFrame();
		frame.setTitle(lang.getString("Dictation.TitleEdit"));
		initialize();
		connectTranscriptionService();
	}
	
	
	/**
	 * Create the panel.
	 */
	public Dictation(CSIGReport r, ViewController _controller) {
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
		
		connectTranscriptionService();
		
		PersistenceManager.watch(this);
		
		frame.requestFocus();
	}
	
	private void connectTranscriptionService(){
		final TranscriptionListener self = this;
		SwingWorker<Void,Void> connectTranscription = new SwingWorker<Void,Void>(){
			
			//boolean connected = false;

			@Override
			protected Void doInBackground() throws Exception {
				InetSocketAddress addr = new InetSocketAddress(
						controller.getTranscriptionIP(),
						controller.getTranscriptionPort());
				transcriptor = new TranscriptionClientApiImpl(addr);
				((TranscriptionClientApiImpl) transcriptor).addTranscriptionListener(self);
				/*connected = */transcriptor.connect();
				return null;
			}
			
			@Override
			protected void done(){
				/*if(connected) {
					btnRecord.setEnabled(true);
				} else
					System.out.println("Error connecting to transcription service");*/
			}
			
		};
		connectTranscription.execute();
	}
	
	private void updateReportView(CSIGReport r) {
		txtBiased.setText(r.getBiased());
		txtUnbiased.setText(r.getUnbiased());
		txtPlan.setText(r.getPlan());
		txtImpression.setText(r.getImpressions());
	}
	private void showSaveDialog(){
		int response;
		response = JOptionPane.showConfirmDialog(frame, lang.getString("Dictation.ConfirmExit"), lang.getString("TitleConfirmExit"),
		      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		switch(response) {
			/*case JOptionPane.YES_OPTION: before, it was a save? dialog
				if(saveReport())
					frame.dispose();
				//saveReport();				
				break;*/
			case JOptionPane.YES_OPTION:
				PersistenceManager.discard(this);
				frame.dispose();
			/*case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:*/// Do nothing				
		}
	}
	
	private void saveReport(){
		final Dictation self = this;
		if(report == null) { //New report
			if(ddlPatient.getSelectedIndex() > 0) {
				
				SwingWorker<Boolean,Void> createWorker = new SwingWorker<Boolean,Void>(){

					@Override
					protected Boolean doInBackground() throws Exception {
						return controller.createReport(txtBiased.getText(), txtUnbiased.getText(), txtImpression.getText(), txtPlan.getText(),
								(String)ddlPatient.getSelectedItem());
					}
					
					@Override
					protected void done(){
						try {
							if(this.get())
								frame.dispose();
							else{
								WaitModal.close();
								self.setVisible(true);
								JOptionPane.showMessageDialog(frame, lang.getString("Error.CouldNotCreateReport"), "Error", JOptionPane.ERROR_MESSAGE);
							}
						} catch (HeadlessException | InterruptedException
								| ExecutionException e) {
							e.printStackTrace();
							WaitModal.close();
							self.setVisible(true);
							JOptionPane.showMessageDialog(frame, lang.getString("Error.InternalError"), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				};
				
				WaitModal.open("Creando informe");
				this.setVisible(false);
				createWorker.execute();
			} else {
				JOptionPane.showMessageDialog(frame, lang.getString("Warning.PatientNotSelected"), "Aviso", JOptionPane.WARNING_MESSAGE);
			}		
		} else { //Existing report
			SwingWorker<Boolean,Void> updateWorker = new SwingWorker<Boolean,Void>(){

				@Override
				protected Boolean doInBackground() throws Exception {
					CSIGReport cleanR = null;
					try {
						//Update CSIGReport in memory
						cleanR = report.clone();
						cleanR.setBiased(txtBiased.getText());
						cleanR.setUnbiased(txtUnbiased.getText());
						cleanR.setImpressions(txtImpression.getText());
						cleanR.setPlan(txtPlan.getText());
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frame, lang.getString("Error.InternalError"), "Error", JOptionPane.ERROR_MESSAGE);
						return false;
					}
					
					return controller.updateReport(cleanR, false);
				}
				
				@Override
				protected void done(){
					try {
						if(this.get())
							frame.dispose();
						else{
							WaitModal.close();
							self.setVisible(true);
							JOptionPane.showMessageDialog(frame, lang.getString("Error.CouldNotUpdateReport"), "Error", JOptionPane.ERROR_MESSAGE);
						}
					} catch (HeadlessException | InterruptedException
							| ExecutionException e) {
						e.printStackTrace();
						WaitModal.close();
						self.setVisible(true);
						JOptionPane.showMessageDialog(frame, lang.getString("Error.InternalError"), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			
			WaitModal.open("Actualizando informe");
			this.setVisible(false);
			//And send it to update in SIIE
			updateWorker.execute();
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
        System.out.println("Sending audio and waiting for transcription...");
        //transcriptor.transcribeFlac(path);
	}
	
	private void switchRecord(){
		if(isRecording) {
			stopRecord();
			btnRecord.setText(lang.getString("Dictation.btnStartRecord"));
			btnPause.setEnabled(false);
			btnPause.setText(lang.getString("Dictation.btnPauseRecord"));
			isPaused = false;
		} else if(transcriptor != null){
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
		btnRecord.setEnabled(false);
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
				saveReport();
			}
		});
		pnlActions.add(btnAnalyze);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(
				  ((int) (screenSize.getWidth()) - frame.getWidth())/2, 
				  ((int) (screenSize.getHeight()) - frame.getHeight())/2);
		frame.setVisible(true);
	}
	
	//Listener for transcription service
	@Override
	public void transcriptionActionPerformed(TranscriptionEvent event) {
		final TranscriptionBundle transcriptionBundle = event.getTranscriptionBundle();
		final TranscriptionStateEvent stateEvent = event.getTranscriptionStateEvent();
		
		switch(stateEvent)
		{
		case CONNECTION_CLOSED:
		case CONNECTION_FAILED:
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					btnRecord.setEnabled(false);
					btnPause.setEnabled(false);
					if(isRecording) {
						line.stop();
						line.close();
						isRecording = false;
					}
				}
			});
			System.err.println("[Transcription] Connection failed.");
			break;
		case NOT_CONNECTED:
			System.err.println("[Transcription] NOT_CONNECTED");
			break;
		case PING_SENT:
			System.out.println("[Transcription] PING_SENT");
			break;
		case PONG_RECEIVED:
			System.out.println("[Transcription] PONG_RECEIVED");
			break;
		case READY_FOR_DATA:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					btnRecord.setEnabled(true);
				}
			});
			break;
		case TRANSCRIPTION_FAILED:
			System.err.println("[Transcription] TRANSCRIPTION_FAILED");
			break;
		case TRANSCRIPTION_RECEIVED:
			// Read transcription from file
			try(BufferedReader reader = Files.newBufferedReader(transcriptionBundle.getTranscription(), Charset.forName("UTF-8")))
			{
				String line = null;
				StringBuilder transcription = new StringBuilder();
				while((line = reader.readLine()) != null)
				{
					JTextArea txt = mantainCaretFocusListener.getLastTextArea();
					if(txt == null) {
						System.err.println("No text area selected");
						break;
					} else {
						txt.insert(line, txt.getCaretPosition());
						txt.setCaretPosition(txt.getCaretPosition()+line.length());
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			break;
		case AUDIO_SENT:
			System.out.println("AUDIO_SENT");
			break;
		case EXCEPTION:
			System.out.println("EXCEPTION");
			System.out.println(stateEvent.getDescription());
			break;
		default:
			System.err.println("[Transcription] Unknown response, is jar same version?");
			break;
		}		
	}
	
	//Audio capture thread
	class CaptureThread extends Thread{
		//private boolean isPaused;
		
		public void run(){
			btnPause.setEnabled(true);
			try{
				line.open(line.getFormat());
				line.start();
				
				AudioInputStream ais = new AudioInputStream(line);
				AudioSystem.write(
						ais, //new AudioInputStream(line),
						FLACFileWriter.FLAC,//fileType,
						new File("session.flac"));
				AudioSystem.write(
						ais, //new AudioInputStream(line), 
						FLACFileWriter.FLAC,
						new File("session2.flac"));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		public void terminate(){
			
		}
	}

	/*Persistence manager*/
	@Override
	public boolean changed(){
		if(!txtBiased.getText().isEmpty()) return true;
		if(!txtUnbiased.getText().isEmpty()) return true;
		if(!txtImpression.getText().isEmpty()) return true;
		if(!txtPlan.getText().isEmpty()) return true;
		return false;
	}
	
	@Override
	public byte[] toData() {
		
		Hashtable<String, Object> status = new Hashtable<String,Object>();
		status.put("uuid", new Long(uuid));
		status.put("txtBiased", txtBiased.getText());
		status.put("txtUnbiased", txtUnbiased.getText());
		status.put("txtImpression", txtImpression.getText());
		status.put("txtPlan", txtPlan.getText());
		if(report != null)
			status.put("reportId", new Long(report.getId()));
		else //If not enabled, patient is already set
			status.put("ddlPatient", ddlPatient.getSelectedItem());
			
		ByteArrayOutputStream bs= new ByteArrayOutputStream();
		try {			
			ObjectOutputStream os = new ObjectOutputStream (bs);
			os.writeObject(status);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bs.toByteArray();
	}

	@Override
	public void restore(byte[] data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		Hashtable<String, Object> status;
		
		try {
			ObjectInputStream os = new ObjectInputStream(is);
			status = (Hashtable<String, Object>) os.readObject();
		} catch(IOException e){
			e.printStackTrace();
			return;
		} catch(ClassNotFoundException e){
			e.printStackTrace();
			//TODO message, discard object & wrong version?
			return;
		}
		this.uuid = ((Long)status.get("uuid")).longValue();
		Long reportId = (Long) status.get("reportId");
		if(reportId != null){
			report = controller.getReport(reportId.longValue());
			ddlPatient.setEnabled(false);
			btnNewPatient.setEnabled(false);
			ddlPatient.setSelectedItem(report.getPatient().toString());
		} else {
			String patientSelected = (String) status.get("ddlPatient");
			if(patientSelected != null)
				ddlPatient.setSelectedItem(patientSelected);
		}
		
		txtBiased.setText((String)status.get("txtBiased"));
		txtUnbiased.setText((String)status.get("txtUnbiased"));
		txtImpression.setText((String)status.get("txtImpression"));
		txtPlan.setText((String)status.get("txtPlan"));
		
		frame.requestFocus();
	}

	@Override
	public String getUID() {
		if(report != null)
			return "RID"+report.getId()/*+"TS"+uuid Want to differentiate windows from same report?*/;
		else
			return "TS"+uuid;
	}
}
