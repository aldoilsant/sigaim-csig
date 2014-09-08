package org.sigaim.csig.view;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.sigaim.csig.model.CSIGFacility;
import org.sigaim.csig.model.CSIGFacultative;
import org.sigaim.csig.model.CSIGModel;
import org.sigaim.csig.model.IntCSIGModel;
import org.sigaim.csig.model.Report;
import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.FunctionalRole;
import org.sigaim.siie.iso13606.rm.II;

public class Main implements ViewController {

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
	static public int transport = 8000;
	
	public JFrame frame;
	private JDialog login;
	private ReportList reportList;
	
	public ResourceBundle lang;
	
	private TargetDataLine line;
	
	public List<Report> getReports() {
		return model.getReports();
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
			if(reportList==null)
				reportList = new ReportList(frame, this);
			return true;
		} else {
			return false;
		}		
	}
	
	private Main() {
		getLang();
		
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		model = new CSIGModel(wsurl);
		
		List<String> facs  = model.getFacilities();
		
		session = new Session();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		frame = new JFrame();
		login = new LoginDialog(this, facs);
		login.setLocation(
				  ((int) (screenSize.getWidth()) - login.getWidth())/2, 
				  ((int) (screenSize.getHeight()) - login.getHeight())/2);
		login.setVisible(true);
		
		WaitModal.close();
		
		//Testing window
		JDialog creator = new LoginCreator(this);
		creator.setLocation(0, 0);
		creator.setVisible(true);
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
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WaitModal.open();
					new Main();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void newReport() {
		new Dictation(null,this);
	}

	@Override
	public void openReport(Report r) {
		new Dictation(r, this);		
	}

	@Override
	public void showReport(Report r) {
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

	@Override
	public void createReport(String bias, String unbias, String impressions,
			String plan, String patient) {
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
		II rootArchetypeId = new II();
		rootArchetypeId.setRoot("CEN-EN13606-COMPOSITION.InformeClinicoNotaSOIP.v1");
		CDCV reportStatus=new CDCV();
		reportStatus.setCode("RSTA02");
		II ehr = model.getEHRIdFromPatient(Long.parseLong(patient.split("/")[1]));
		
		Report r = model.createReport(bias, unbias, impressions, plan, composer, ehr, reportStatus);
		
		model.fillSoip(r);
		model.fillSoipConcepts(r);
		this.showReport(r);
		
		if(reportList != null) {
			reportList.updateList(this.getReports());
		}
	}
	
	@Override
	public ResourceBundle getLang() {
		if(lang == null)
			lang = ResourceBundle.getBundle("org.sigaim.csig.resources.lang.Text",new Locale("es"));
		return lang;
	}
	
	@Override
	public TargetDataLine getLine() {
		AudioFormat af = new AudioFormat(16000, 16, 1, true, false);
		
		final String inputTypeString = "MICROPHONE"; // or COMPACT_DISC or MICROPHONE etc ...
		final Port.Info myInputType = new Port.Info((Port.class), inputTypeString, true);
		final DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, af);

	    //Go through the System audio mixer
	    for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
	    	try {
	    		Mixer targetMixer = AudioSystem.getMixer(mixerInfo);
	    		targetMixer.open();
	    		//Check if it supports the desired format
	    		if (targetMixer.isLineSupported(targetDataLineInfo)) {
	    			//System.out.println(mixerInfo.getName() + " supports recording @" + af);
	    			//now go back and start again trying to match a mixer to a port
	    			//the only way I figured how is by matching name, because 
	    			//the port mixer name is the same as the actual mixer with "Port " in front of it
	    			//there MUST be a better way
	    			for (Mixer.Info mifo : AudioSystem.getMixerInfo()) {
	    				String port_string = "Port ";
	    				if ((port_string + mixerInfo.getName()).equals(mifo.getName())) {
	    					System.out.println("Matched Port to Mixer:" + mixerInfo.getName());
	    					Mixer portMixer = AudioSystem.getMixer(mifo);
	    					portMixer.open();
	    					portMixer.isLineSupported((Line.Info) myInputType);
	    					//now check the mixer has the right input type eg LINE_IN
	    					if (portMixer.isLineSupported((Line.Info) myInputType)) {
	    						//OK we have a supported Port Type for the Mixer
	    						//This has all matched (hopefully)
	    						//now just get the record line
	    						//There should be at least 1 line, usually 32 and possible unlimited
	    						//which would be "AudioSystem.Unspecified" if we ask the mixer 
	    						//but I haven't checked any of this
	    						line = (TargetDataLine) targetMixer.getLine(targetDataLineInfo);
	    						System.out.println("Got TargetDataLine from :" + targetMixer.getMixerInfo().getName());
	    						//targetMixer.close();
	    						return line;
	    					}
	    				}
	    			}
	    		}
	    		targetMixer.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		return null;
	}

	@Override
	public String getTranscriptionIP() {
		return transip;
	}

	@Override
	public int getTranscriptionPort() {
		return transport;
	}
	
}
