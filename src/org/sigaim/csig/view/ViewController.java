package org.sigaim.csig.view;

import java.util.List;
import java.util.ResourceBundle;

import org.sigaim.csig.model.CSIGFacility;
import org.sigaim.csig.model.CSIGFacultative;
import org.sigaim.csig.model.IntCSIGModel;
import org.sigaim.csig.model.Report;
import org.sigaim.siie.clients.IntSIIE001EQLClient;
import org.sigaim.siie.clients.IntSIIE004ReportManagementClient;

public interface ViewController {

	IntCSIGModel getModelController();
	
	boolean doLogin(String user, String centre, char[] pass);
	List<Report> getReports();
	void newReport();
	void openReport(Report r);
	void showReport(Report r);
	long createFacultative();
	long createFacility();
	void createReport(String bias, String unbias, String impressions, String plan, String patient);
	ResourceBundle getLang();

}