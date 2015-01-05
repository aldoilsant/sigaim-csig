package org.sigaim.csig.view;

import java.awt.Dimension;

import javax.swing.UIManager.*;
import javax.swing.plaf.synth.SynthLookAndFeel;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;
import org.sigaim.csig.CSIGFactory;
import org.sigaim.csig.model.CSIGFacility;
import org.sigaim.csig.model.CSIGFacultative;
import org.sigaim.csig.model.CSIGModel;
import org.sigaim.csig.model.CSIGPatient;
import org.sigaim.csig.model.IntCSIGModel;
import org.sigaim.csig.model.CSIGReport;
import org.sigaim.csig.persistence.PersistenceManager;
import org.sigaim.csig.theme.CSIGTheme;
import org.sigaim.csig.theme.SubstanceCSIGLookAndFeel;
import org.sigaim.csig.theme.SubstanceCSIGSkin;
import org.sigaim.csig.view.helper.Audio;
import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.FunctionalRole;
import org.sigaim.siie.iso13606.rm.II;
import org.sigaim.siie.rm.exceptions.RejectException;

public class Main implements ViewController {
	
	private final ViewController self; 

	private class Session {
		boolean logged = false;
		public long /*CSIGFacultative*/ user;
		public long /*CSIGFacility*/ centre;
	}
	
	private Session session;
	private IntCSIGModel model;
	static public String wsurl = "http://sigaim.siie.cesga.es:8080/SIIEWS3";
	//static public String wsurl = "http://localhost:8080/SIIEWS3";
	
	//static public String transip = "193.147.36.199";
	static public String transip = "193.144.33.85";
	static public int transport = 8080;
	
	public JFrame frame;
	private JDialog login;
	private ReportList reportListWindow;
	
	List<CSIGReport> reportList;
	
	public ResourceBundle lang;
	
	private TargetDataLine line;
	
	public List<CSIGReport> getReports() {
		reportList = model.getReports(); //Keep local reference to reportList updated 
		return reportList;
	}
	
	public boolean doLogin(String user, String centre, char[] password) {
		long id = -1;
		boolean valid = false;
		try {
			session.user = id = Long.parseLong(user);
			session.centre = Long.parseLong(centre.split("/")[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		valid = model.checkLoginInfo(id, centre, password);
		if(valid){
			session.logged = true;
			login.setVisible(false);
			login.dispose();
			login = null;
			//frame.removeAll();
			if(reportListWindow==null)
				reportListWindow = new ReportList(frame, this);
			
			PersistenceManager.check(this);
			
			return true;
		} else {
			JOptionPane.showMessageDialog(null, lang.getString("Error.InvalidLoginInfo"), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}		
	}
	
	private Main() {
		self = this;
		getLang();
		
		CSIGFactory.setBaseUrl(wsurl);
		model = CSIGFactory.getModel();
		
		//WaitModal.setMessage("Conectando con el servidor SIIE...");
		
		session = new Session();
		
		List<String> facs;
		do{
			facs = model.getFacilities();
			if(facs == null) {
				WaitModal.close();
				try {
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run() {					
							JOptionPane.showMessageDialog(
									WaitModal.getModal(), 
									lang.getString("Error.SIIENotResponding"), 
									"Error", 
									JOptionPane.ERROR_MESSAGE);
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		} while(facs == null);
		
		final List<String> finalFacs = facs;
		
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run() {					
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					
					frame = new JFrame();
					login = new LoginDialog(self, finalFacs);
					login.setLocation(
							  ((int) (screenSize.getWidth()) - login.getWidth())/2, 
							  ((int) (screenSize.getHeight()) - login.getHeight())/2);
					login.setVisible(true);

					WaitModal.close();
					
					//Testing window
					JDialog creator = new LoginCreator(self);
					creator.setLocation(0, 0);
					creator.setVisible(true);
				}			

			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		

	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		for(int i = 0; i < args.length; i++) {

            if(args[i].equals("-wsurl")) {
            	if(args.length == i)
            		System.out.println("Incorrect parameter -wsurl has no value");
                wsurl = args[i+1];
            } else if(args[i].equals("-help")) {
            	System.out.println("Available commands:\n\t"
            			+ "-wsurl url\n\t\turl is the route to SIIE Web Services server (example: http://localhost:8080/SIIEWS)\n\t-help"
            			+ "\n\t-tip Transcription service IP (default: 193.144.33.85)"
            			+ "\n\t-tport Transcription service Port (default: 8000)");
            	return;
            } else if(args[i].equals("-tip")) {
            	if(args.length == i)
            		System.out.println("Incorrect parameter -tip has no value");
            	else
            		transip = args[i+1];
            } else if(args[i].equals("-tport")) {
            	if(args.length == i)
            		System.out.println("Incorrect parameter -tip has no value");
            	else {
            		transport = Integer.parseInt(args[i+1]);
            	}
            }
		}
		System.out.println("Conecting to web services in "+wsurl+" (change with -wsurl param)");

		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	try {
					/*UIManager.setLookAndFeel(new SubstanceCSIGLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
					JDialog.setDefaultLookAndFeelDecorated(true);*/
		    		SynthLookAndFeel lookAndFeel = new SynthLookAndFeel();
		    		lookAndFeel.load(CSIGTheme.class.getResourceAsStream("synth.xml"), CSIGTheme.class);
		    		UIManager.setLookAndFeel(lookAndFeel);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (UnsupportedLookAndFeelException e1) {
						e1.printStackTrace();
					}
				}		    	
		    	EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							WaitModal.open();
							Thread mainThread = new Thread() {
								public void run() {
									new Main();
								}								
							};
							mainThread.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		    }
		} );
	}

	@Override
	public void newReport() {
		new Dictation(null,this);
	}

	@Override
	public void openReport(CSIGReport r) {
		new Dictation(r, this);		
	}

	@Override
	public void showReport(CSIGReport r) {
		if(r != null)
			WaitModal.open("Abriendo Informe...");
		new ShowReport(r, this);
	}

	@Override
	public long createFacultative() {
		CSIGFacultative facultative = model.createFacultative();
		if(facultative != null)
			return facultative.getId();
		else
			return -1;
	}

	@Override
	public long createFacility() {
		CSIGFacility facility = model.createFacility();
		if(facility != null) {
			if(login != null) {
				//reload login window information
				login.setVisible(false);
				login.dispose();
				login = new LoginDialog(this, model.getFacilities());
				login.setVisible(true);
			}
			return facility.getId();
		}
		else
			return -1;
	}

	@Override
	public IntCSIGModel getModelController() {
		return model;
	}
	
	private FunctionalRole currentComposer(){
		//FIXME: use stored II in session
		FunctionalRole composer=new FunctionalRole();
		II facultative = new II();
		facultative.setRoot("org.sigaim");
		facultative.setExtension(Long.toString(session.user));
		II facility = new II();
		facility.setRoot("org.sigaim");
		facility.setExtension(Long.toString(session.centre));
		composer.setHealthcareFacility(facility);
		composer.setPerformer(facultative);
		return composer;
	}

	@Override
	public boolean createReport(String bias, String unbias, String impressions,
			String plan, String patient) {
		FunctionalRole composer = currentComposer();
		II rootArchetypeId = new II();
		rootArchetypeId.setRoot("CEN-EN13606-COMPOSITION.InformeClinicoNotaSOIP.v1");
		CDCV reportStatus=new CDCV();
		reportStatus.setCode("RSTA02");
		II ehr = model.getEHRIdFromPatient(Long.parseLong(patient.split("/")[1]));
		
		
		final CSIGReport r = model.createReport(bias, unbias, impressions, plan, composer, ehr, reportStatus);
		
		if(r==null)
			return false;
		else
			r.setPatient(new CSIGPatient(patient));
		
		WaitModal.setMessage("Recuperando análisis del servidor");
		model.fillSoip(r);
		model.fillSoipConcepts(r);
		if(SwingUtilities.isEventDispatchThread())
			this.showReport(r);
		else
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					showReport(r);
				}
				
			});
		
		if(reportListWindow != null) {
			reportListWindow.updateList(this.getReports());
		}
		return true;
	}
	
	@Override
	public boolean updateReport(CSIGReport report, boolean confirmed) {
		FunctionalRole composer = currentComposer();
		CSIGReport newReport = model.updateReport(report, composer, confirmed);
		return newReport != null;
	}	
	
	@Override
	public ResourceBundle getLang() {
		if(lang == null)
			lang = ResourceBundle.getBundle("org.sigaim.csig.resources.lang.Text",new Locale("es"));
		return lang;
	}

	@Override
	public String getTranscriptionIP() {
		return transip;
	}

	@Override
	public int getTranscriptionPort() {
		return transport;
	}

	@Override
	public CSIGReport getReport(long reportId) {
		//Currently that should be faster than calling back SIIE client again
		//TODO: this should change with pagination
		for(CSIGReport r : reportList) {
			if(r.getId() == reportId)
				return r;
		}
		return null;
	}

	@Override
	public TargetDataLine getLine() {
		return Audio.getLine();
	}
	
}
