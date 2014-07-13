package org.sigaim.csig.view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

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
import org.sigaim.siie.iso13606.rm.HealthcareFacility;
import org.sigaim.siie.rm.exceptions.RejectException;


public class Main implements ViewController {

	private class Session {
		boolean logged = false;
		public long user = 0;
		public long centre = 0;
	}
	
	private Session session;
	private IntCSIGModel model;
	
	public JFrame frame;
	private JDialog login;
	private ReportList reportList;	
	
	public List<Report> getReports() {
		return model.getReports();
	}
	
	public boolean doLogin(String user, String centre, char[] password) {
		long id = -1;
		boolean valid = false;
		try {
			session.user = id = Long.parseLong(user);
			//session.centre = Long.parseLong(centre);
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
		model = new CSIGModel();
		
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
}
