package org.sigaim.csig.model;

import java.util.List;

import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.FunctionalRole;
import org.sigaim.siie.iso13606.rm.II;

public interface IntCSIGModel {

	boolean checkLoginInfo(long user, String centre, char[] password);
	
	List<Report> getReports();
	List<String> getFacilities();
	List<CSIGFacultative> getFacultatives();
	List<CSIGPatient> getPatients();
	Report fillSoip(Report report);
	Report fillSoipConcepts(Report report);
	
	/*
	 * Returns a new facultative after creating it in SIIE.
	 * Null if not created.
	 */
	CSIGFacultative createFacultative();
	CSIGFacility createFacility();
	CSIGPatient createPatient();
	II getEHRIdFromPatient(long patientId);
	Report createReport(String bias, String unbias, String impressions, String plan, FunctionalRole composer,
			II ehrId, CDCV status);
	
}
