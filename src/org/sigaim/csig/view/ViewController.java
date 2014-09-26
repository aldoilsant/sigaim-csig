package org.sigaim.csig.view;

import java.util.List;
import java.util.ResourceBundle;

import javax.sound.sampled.Line;
import javax.sound.sampled.TargetDataLine;

import org.sigaim.csig.model.CSIGFacility;
import org.sigaim.csig.model.CSIGFacultative;
import org.sigaim.csig.model.IntCSIGModel;
import org.sigaim.csig.model.CSIGReport;
import org.sigaim.siie.clients.IntSIIE001EQLClient;
import org.sigaim.siie.clients.IntSIIE004ReportManagementClient;

public interface ViewController {

	IntCSIGModel getModelController();
	
	String getTranscriptionIP();
	int getTranscriptionPort();
	
	boolean doLogin(String user, String centre, char[] pass);
	List<CSIGReport> getReports();
	void newReport();
	void openReport(CSIGReport r);
	void showReport(CSIGReport r);
	long createFacultative();
	long createFacility();
	boolean createReport(String bias, String unbias, String impressions, String plan, String patient);
	ResourceBundle getLang();
	TargetDataLine getLine();

}