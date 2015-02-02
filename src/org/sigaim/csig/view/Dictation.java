package org.sigaim.csig.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.sigaim.csig.model.CSIGPatient;
import org.sigaim.csig.model.CSIGReport;
import org.sigaim.csig.persistence.PersistenceManager;
import org.sigaim.csig.persistence.PersistentObject;
import org.sigaim.csig.theme.CSIGDialog;
import org.sigaim.csig.theme.CSIGTheme;
import org.sigaim.csig.theme.ThemedWindow;

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
	
	private static final long serialVersionUID = 1L;
	private ResourceBundle lang;
	private long uuid = (new Date()).getTime();
	
	private ThemedWindow frame;
	DictationPanel panel;
	private CSIGReport report;
	private ViewController controller;
	
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
	
	private final Dictation self;
	
	private MantainCaretFocusListener mantainCaretFocusListener = new MantainCaretFocusListener();
	
	//Persistence manager
	/**
	 * @wbp.parser.constructor
	 */
	public Dictation(ViewController _controller){
		self = this;
		controller = _controller;
		lang = controller.getLang();
		
		frame = new ThemedWindow();
		//frame.setTitle(lang.getString("Dictation.TitleEdit"));
		initialize();
		connectTranscriptionService();
	}
	
	
	/**
	 * Create the panel.
	 */
	public Dictation(CSIGReport r, ViewController _controller) {
		self = this;
		controller = _controller;
		lang = controller.getLang();
		
		frame = new ThemedWindow();

		/*if(r != null)
			frame.setTitle(lang.getString("Dictation.TitleEdit"));
		else
			frame.setTitle(lang.getString("Dictation.TitleNew"));*/
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
		panel.txtBiased.setText(r.getBiased());
		panel.txtUnbiased.setText(r.getUnbiased());
		panel.txtPlan.setText(r.getPlan());
		panel.txtImpression.setText(r.getImpressions());
	}
	private void showSaveDialog(){
		int response;
		/*response = JOptionPane.showConfirmDialog(frame, lang.getString("Dictation.ConfirmExit"), lang.getString("TitleConfirmExit"),
		      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);*/
		response = CSIGDialog.showYesNo(lang.getString("Yes"), lang.getString("No"),
				lang.getString("Dictation.ConfirmExit"), CSIGTheme.iconHelp());
		
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
	
	public void saveReport(){
		final Dictation self = this;
		if(report == null) { //New report
			if(ddlPatient.getSelectedIndex() > 0) {
				
				SwingWorker<Boolean,Void> createWorker = new SwingWorker<Boolean,Void>(){

					@Override
					protected Boolean doInBackground() throws Exception {
						return controller.createReport(
								panel.txtBiased.getText(), panel.txtUnbiased.getText(), 
								panel.txtImpression.getText(), panel.txtPlan.getText(),
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
						cleanR.setBiased(panel.txtBiased.getText());
						cleanR.setUnbiased(panel.txtUnbiased.getText());
						cleanR.setImpressions(panel.txtImpression.getText());
						cleanR.setPlan(panel.txtPlan.getText());
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
		frame.setBounds(100, 100, 812, 554);
		
		JPanel pnlReportInfo = ThemedWindow.getDefaultTitleBar();;
		frame.setTitleBar(pnlReportInfo);
		pnlReportInfo.setLayout(new FormLayout(new ColumnSpec[] {
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.MINIMUM, Sizes.constant("5dlu", true), Sizes.constant("5dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:max(31dlu;min):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("75dlu", true), Sizes.constant("75dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.constant("5dlu", true), Sizes.constant("5dlu", true)), 0),},
			new RowSpec[] {
				RowSpec.decode("4px"),
				RowSpec.decode("max(11dlu;default)"),
				RowSpec.decode("max(4dlu;default)"),}));
		
		JLabel lblPatient = new JLabel(lang.getString("lblPatient"));
		pnlReportInfo.add(lblPatient, "3, 2, right, default");
		
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
		
		JButton btnExit = new JButton(lang.getString("btnClose"));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCloseAction();
			}
		});
		btnExit.setEnabled(true);
		pnlReportInfo.add(btnExit, "15, 2");
		
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
		
		panel = new DictationPanel(this, lang);
		frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
		frame.add(panel);
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		frame.getMainFrame().setMaximizedBounds(env.getMaximumWindowBounds());
		frame.getMainFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
		
		mantainCaretFocusListener.addTextArea(panel.txtBiased);
		mantainCaretFocusListener.addTextArea(panel.txtUnbiased);
		mantainCaretFocusListener.addTextArea(panel.txtImpression);
		mantainCaretFocusListener.addTextArea(panel.txtPlan);
		
		/*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(
				  ((int) (screenSize.getWidth()) - frame.getWidth())/2, 
				  ((int) (screenSize.getHeight()) - frame.getHeight())/2);*/
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
		
		//On exit ask saving if content changed
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
				    @Override
				    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				        onCloseAction();
				    }
				});
	}
	
	private void onCloseAction() {
		if(report != null) {
        	if( (!report.getBiased().equals(panel.txtBiased.getText())) ||
        		(!report.getUnbiased().equals(panel.txtUnbiased.getText())) ||
        		(!report.getPlan().equals(panel.txtPlan.getText())) ||
        		(!report.getImpressions().equals(panel.txtImpression.getText())) )
        		askSave = true;
        } else {
        	if( (!panel.txtBiased.getText().isEmpty()) ||
    			(!panel.txtUnbiased.getText().isEmpty()) ||
    			(!panel.txtPlan.getText().isEmpty()) ||
    			(!panel.txtImpression.getText().isEmpty()) )
        		askSave = true;
        }
        if(askSave)
        	showSaveDialog();
        else
        	frame.dispose();
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
		if(!panel.txtBiased.getText().isEmpty()) return true;
		if(!panel.txtUnbiased.getText().isEmpty()) return true;
		if(!panel.txtImpression.getText().isEmpty()) return true;
		if(!panel.txtPlan.getText().isEmpty()) return true;
		return false;
	}
	
	@Override
	public byte[] toData() {
		
		Hashtable<String, Object> status = new Hashtable<String,Object>();
		status.put("uuid", new Long(uuid));
		status.put("txtBiased", panel.txtBiased.getText());
		status.put("txtUnbiased", panel.txtUnbiased.getText());
		status.put("txtImpression", panel.txtImpression.getText());
		status.put("txtPlan", panel.txtPlan.getText());
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
		
		panel.txtBiased.setText((String)status.get("txtBiased"));
		panel.txtUnbiased.setText((String)status.get("txtUnbiased"));
		panel.txtImpression.setText((String)status.get("txtImpression"));
		panel.txtPlan.setText((String)status.get("txtPlan"));
		
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
