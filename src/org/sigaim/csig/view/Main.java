package org.sigaim.csig.view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.sigaim.csig.model.CSIGFacility;
import org.sigaim.csig.model.CSIGFacultative;
import org.sigaim.csig.model.CSIGModel;
import org.sigaim.csig.model.IntCSIGModel;
import org.sigaim.csig.model.Report;
import org.sigaim.csig.model.CSIGConcept;
import org.sigaim.siie.clients.IntSIIE001EQLClient;
import org.sigaim.siie.clients.IntSIIE004ReportManagementClient;
import org.sigaim.siie.clients.ws.WSIntSIIE001EQLClient;
import org.sigaim.siie.clients.ws.WSIntSIIE004ReportManagementClient;
import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.FunctionalRole;
import org.sigaim.siie.iso13606.rm.HealthcareFacility;
import org.sigaim.siie.iso13606.rm.II;
import org.sigaim.siie.rm.exceptions.RejectException;


public class Main implements ViewController {

	private class Session {
		boolean logged = false;
		public long /*CSIGFacultative*/ user;
		public long /*CSIGFacility*/ centre;
	}
	
	private Session session;
	private IntCSIGModel model;
	static public String wsurl = "http://localhost:8080/SIIEWS";
	
	public JFrame frame;
	private JDialog login;
	private ReportList reportList;
	
	public ResourceBundle lang;
	
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
	
	private Main(){
		getLang();
		
		model = new CSIGModel(wsurl);
		
		List<String> facs  = model.getFacilities();
		
		session = new Session();
		
		frame = new JFrame();
		login = new LoginDialog(this, facs);
		login.setVisible(true);
		
		//Testing window
		JDialog creator = new LoginCreator(this);
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
            			+ "-wsurl url\n\t\turl is the route to SIIE Web Services server (example: http://localhost:8080/SIIEWS)\n\t-help");
            	return;
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
		model.createReport(bias, unbias, impressions, plan, composer, ehr, reportStatus);
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
}
