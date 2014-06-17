package view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import dataobject.Report;
import dataobject.SnomedConcept;


public class Main implements ViewController {

	public JFrame frame;
	private JDialog login;
	private ReportList reportList;
	
	
	private ArrayList<Object> biasedExample1(){
		ArrayList<Object> rtn = new ArrayList<Object>();
		rtn.add(new String("Acude a urgencias: refiere sensación distérmica de 3 días evolución sin "));
		rtn.add(new SnomedConcept(271897009, "fiebre", "fiebre"));
		rtn.add(new String(" termometrada\nMolestias faríngeas con tos improductiva."));
		return rtn;
	}
	
	
	public List<Report> getInforms() {
		ArrayList<Report> rtn = new ArrayList<Report>();
		Report i = new Report();
		i.setBiased(biasedExample1());
		i.setUnbiased(new ArrayList<Object>());
		i.setPlan(new ArrayList<Object>());
		i.setImpressions(new ArrayList<Object>());
		rtn.add(i);
		/*i = new Report();
		i.setBiased(t2);
		i.setUnbiased(t1);
		i.setPlan(t2);
		i.setImpressions(t1);
		rtn.add(i);*/
		return rtn;
	}
	
	public void doLogin(String user, char[] password) {
		login.setVisible(false);
		login.dispose();
		login = null;
		//frame.removeAll();
		if(reportList==null)
			reportList = new ReportList(frame, this);
		
		//reportList.show();
		
	}
	
	private Main(){
		frame = new JFrame();
		login = new LoginDialog(this);
		login.setVisible(true);
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
}
