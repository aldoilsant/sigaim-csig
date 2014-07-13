package org.sigaim.csig.model;

import java.util.ArrayList;
import java.util.List;

import org.sigaim.csig.view.ReportList;
import org.sigaim.siie.clients.IntSIIE001EQLClient;
import org.sigaim.siie.clients.IntSIIE004ReportManagementClient;
import org.sigaim.siie.clients.IntSIIEReportSummary;
import org.sigaim.siie.clients.ws.WSIntSIIE001EQLClient;
import org.sigaim.siie.clients.ws.WSIntSIIE004ReportManagementClient;
import org.sigaim.siie.iso13606.rm.EHRExtract;
import org.sigaim.siie.iso13606.rm.Element;
import org.sigaim.siie.iso13606.rm.HealthcareFacility;
import org.sigaim.siie.iso13606.rm.HealthcareProfessionalRole;
import org.sigaim.siie.iso13606.rm.Performer;
import org.sigaim.siie.iso13606.rm.ST;
import org.sigaim.siie.iso13606.rm.SubjectOfCare;
import org.sigaim.siie.rm.exceptions.RejectException;

public class CSIGModel implements IntCSIGModel {

	private IntSIIE001EQLClient eqlClient;
	private IntSIIE004ReportManagementClient reportClient;
	
	public CSIGModel() {
		eqlClient = new WSIntSIIE001EQLClient();
		reportClient = new WSIntSIIE004ReportManagementClient();
	}	
	
	@Override
	public List<Report> getReports() {
		ArrayList<Report> rtn = new ArrayList<Report>();
		try {
			List<IntSIIEReportSummary> list = eqlClient.getAllReportSummaries();
			for(IntSIIEReportSummary rs : list){
				Report r = new Report(this);
				r.setCreation(rs.getCreationDate().toGregorianCalendar());
				r.setPatient(new CSIGPatient(rs.getSubject()));
				r.setFacultative(rs.getPerformer().getRoot() + "/" + rs.getPerformer().getExtension());
				r.setId(rs.getId());
				rtn.add(r);
			}
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return rtn;
	}

	@Override
	public Report fillSoip(Report report) {
		List<Element> soip = new ArrayList<Element>();
		String bias=null, unbias=null, impress=null, plan=null;
		
		try {
			soip = eqlClient.getReportSoip(report.getId());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		
		for(Element e : soip){
			String code = e.getMeaning().getCode();
			if(code.equals("at0003"))
				bias = ((ST)e.getValue()).getValue();
			else if(code.equals("at0004"))
				unbias = ((ST)e.getValue()).getValue();
			else if(code.equals("at0005"))
				impress = ((ST)e.getValue()).getValue();
			else if(code.equals("at0006"))
				plan = ((ST)e.getValue()).getValue();
		}
		
		if(bias != null)
			report.setDictationBiased(bias);
		else
			report.setDictationBiased("");
		
		if(unbias != null)
			report.setDictationUnbiased(unbias);
		else
			report.setDictationUnbiased("");
		
		if(impress != null)
			report.setDictationImpressions(impress);
		else
			report.setDictationImpressions("");
		
		if(plan != null)
			report.setDictationPlan(plan);
		else
			report.setDictationPlan("");			
		
		return report;
	}


	@Override
	public boolean checkLoginInfo(long user, String centre, char[] password) {
		try {
			return eqlClient.getUserExists(user);
		} catch (RejectException re) {
			System.out.println("Error connecting");
			return false;
		}
	}


	@Override
	public List<String> getFacilities() {
		ArrayList<String> rtn = new ArrayList<String>();
		List<HealthcareFacility> facs;
		try {
			facs = eqlClient.getAllHealthcareFacilities();
			for(HealthcareFacility f:facs) {
				rtn.add(f.getIdentifier().getRoot()+"/"+f.getIdentifier().getExtension());
			}
		} catch (RejectException re) {
			re.printStackTrace();
			System.out.print("Connection refused");
		}	
		return rtn;
	}

	@Override
	public List<CSIGPatient> getPatients() {
		ArrayList<CSIGPatient> rtn = new ArrayList<CSIGPatient>();
		try {
			List<SubjectOfCare> subjects = eqlClient.getAllSubjectsOfCare();
			for(SubjectOfCare s : subjects)
				rtn.add(new CSIGPatient(s.getIdentifier()));
		} catch (RejectException e) {
			e.printStackTrace();
		}
		
		return rtn;
	}

	@Override
	public List<CSIGFacultative> getFacultatives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CSIGFacultative createFacultative() {
		try {
			Performer p = reportClient.createPerformer("");
			return new CSIGFacultative(p.getIdentifier());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CSIGFacility createFacility() {
		try {
			HealthcareFacility fac = reportClient.createHealthcareFacility("");
			return new CSIGFacility(fac.getIdentifier());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CSIGPatient createPatient() {
		try {
			EHRExtract subject = reportClient.createSubjectOfCare("");
			return new CSIGPatient(subject.getSubjectOfCare());
		} catch (RejectException e) {
			e.printStackTrace();
		}
		return null;
	}

}
