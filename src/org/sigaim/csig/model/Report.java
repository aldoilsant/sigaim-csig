package org.sigaim.csig.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.sigaim.csig.dataobject.SnomedConcept;
import org.sigaim.siie.clients.IntSIIE001EQLClient;
import org.sigaim.siie.clients.IntSIIEReportSummary;
import org.sigaim.siie.rm.ReferenceModelManager;
import org.sigaim.siie.rm.ReflectorReferenceModelManager;
import org.sigaim.siie.rm.exceptions.RejectException;
import org.sigaim.siie.seql.model.SEQLResultSet;

public class Report {
	
	private ArrayList<Object> biased;
	private ArrayList<Object> unbiased;
	private ArrayList<Object> impressions;
	private ArrayList<Object> plan;
	private Calendar creation;
	private int versionNumber;
	private ArrayList<Report> versions;
	private String patient;
	private String facultative;
	private Long ehr;
	private String reportId;
	
	private String dictationBiased;
	private String dictationUnbiased;
	private String dictationImpressions;
	private String dictationPlan;
	
	private IntCSIGModel modelController;

	public String getDictationBiased() {
		return dictationBiased;
	}

	private ArrayList<Object> copySoip(ArrayList<Object> list) {
		ArrayList<Object> rtn = new ArrayList<Object>(list.size());
		for(Object o : list){
			if(o instanceof String)
				rtn.add(o);
			else if (o instanceof SnomedConcept)
				rtn.add((new SnomedConcept( (SnomedConcept)o )));
			else
				throw new IllegalStateException("Soip containing Object that is not String nor SnomeConcept");
		}
		return rtn;
	}
	
	private String getPlainText(ArrayList<Object> soipNote) {
		String rtn = new String();
		for(Object o : soipNote){
			if(o instanceof String)
				rtn = rtn.concat((String)o);
			else if (o instanceof SnomedConcept)
				rtn = rtn.concat(((SnomedConcept)o).text);
			else
				throw new IllegalStateException("Soip containing Object that is not String nor SnomeConcept");
		}
		return rtn;		
	}

	
	public Report(IntCSIGModel controller) {
		modelController = controller;
		versionNumber = 1;
		creation = Calendar.getInstance();
		versions = new ArrayList<Report>();
		versions.add(this);
	}
	public Report(Report prev) {
		modelController = prev.modelController;
		versionNumber = prev.versionNumber + 1;
		creation = Calendar.getInstance();
		versions = prev.versions;
		biased = copySoip(prev.biased);
		unbiased = copySoip(prev.unbiased);
		impressions = copySoip(prev.impressions);
		plan = copySoip(prev.plan);
		patient = prev.patient;
		facultative = prev.facultative;
		versions.add(this);
	}
	
	public ArrayList<Object> getBiased() {
		return biased;
	}
	public void setBiased(ArrayList<Object> biased) {
		this.biased = biased;
		this.dictationBiased = getPlainText(biased);
	}

	public ArrayList<Object> getUnbiased() {
		return unbiased;
	}
	public void setUnbiased(ArrayList<Object> unbiased) {
		this.unbiased = unbiased;
		this.dictationUnbiased = getPlainText(unbiased);
	}
	public ArrayList<Object> getImpressions() {
		return impressions;
	}
	public void setImpressions(ArrayList<Object> impressions) {
		this.impressions = impressions;
		this.dictationImpressions = getPlainText(impressions);
	}
	public ArrayList<Object> getPlan() {
		return plan;
	}
	public void setPlan(ArrayList<Object> plan) {
		this.plan = plan;
		this.dictationPlan = getPlainText(plan);
	}
	public Calendar getCreation() {
		return creation;
	}
	public void setCreation(Calendar creation) {
		this.creation = creation;
	}
	public List<Report> getVersions() {
		return versions;
	}
	public String getFullText() {
		return "S: "+getDictationBiased()+ "\n\nO: "+getDictationUnbiased()+ "\n\nI: "+getDictationImpressions()+ "\n\nP: "+getDictationPlan();
	}
	
	public String getPatient() {
		return patient;
	}
	public void setPatient(String patient) {
		for(Report i : versions)
			i.patient = patient;
	}
	public String getFacultative() {
		return facultative;
	}
	public void setFacultative(String facultative) {
		this.facultative = facultative;
	}
	
	public void setDictationBiased(String dictationBiased) {
		this.dictationBiased = dictationBiased;
	}

	public String getDictationUnbiased() {
		return dictationUnbiased;
	}

	public void setDictationUnbiased(String dictationUnbiased) {
		this.dictationUnbiased = dictationUnbiased;
	}

	public String getDictationImpressions() {
		return dictationImpressions;
	}

	public void setDictationImpressions(String dictationImpressions) {
		this.dictationImpressions = dictationImpressions;
	}

	public String getDictationPlan() {
		return dictationPlan;
	}

	public void setDictationPlan(String dictationPlan) {
		this.dictationPlan = dictationPlan;
	}
	
	public int getVersion(){
		return this.versionNumber;
	}
	
	public long getEhr() {
		return this.ehr;
	}
	public void setEhr(long par) {
		this.ehr = par;
	}
	public String getId() {
		return this.reportId;
	}
	public void setId(String id) {
		this.reportId = new String(id);
	}
}
