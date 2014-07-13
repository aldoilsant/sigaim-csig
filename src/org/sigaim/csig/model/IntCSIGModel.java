package org.sigaim.csig.model;

import java.util.List;

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
	
}
