package org.sigaim.csig.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class Report extends CSIGIdentifiedObject {
	
	private List<CSIGConcept> biasedConcepts;
	private List<CSIGConcept> unbiasedConcepts;
	private List<CSIGConcept> impressionsConcepts;
	private List<CSIGConcept> planConcepts;
	private Calendar creation;
	private int versionNumber;
	private ArrayList<Report> versions;
	private CSIGPatient patient;
	private String facultative;
	private Long ehr;
	private String reportId;
	
	private String biased;
	private String unbiased;
	private String impressions;
	private String plan;
	
	private HashMap<String, List<CSIGConcept>> synonyms;
	
	private IntCSIGModel modelController;

	/*private ArrayList<Object> copySoip(ArrayList<Object> list) {
		ArrayList<Object> rtn = new ArrayList<Object>(list.size());
		for(Object o : list){
			if(o instanceof String)
				rtn.add(o);
			else if (o instanceof CSIGConcept)
				rtn.add((new CSIGConcept( (CSIGConcept)o )));
			else
				throw new IllegalStateException("Soip containing Object that is not String nor SnomeConcept");
		}
		return rtn;
	}*/
	
	/*private String getPlainText(ArrayList<Object> soipNote) {
		String rtn = new String();
		for(Object o : soipNote) {
			if(o instanceof String)
				rtn = rtn.concat((String)o);
			else if (o instanceof CSIGConcept)
				rtn = rtn.concat(((CSIGConcept)o).text);
			else
				throw new IllegalStateException("Soip containing Object that is not String nor SnomeConcept");
		}
		return rtn;		
	}*/
	
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
		/*biased = copySoip(prev.biased);
		unbiased = copySoip(prev.unbiased);
		impressions = copySoip(prev.impressions);
		plan = copySoip(prev.plan);*/
		
		patient = prev.patient;
		facultative = prev.facultative;
		versions.add(this);
	}
	
	public List<CSIGConcept> getBiasedConcepts() {
		if(biasedConcepts == null)
			modelController.fillSoipConcepts(this);
		return biasedConcepts;
	}
	public void setBiasedConcepts(ArrayList<CSIGConcept> biased) {
		this.biasedConcepts = biased;
	}

	public List<CSIGConcept> getUnbiasedConcepts() {
		if(unbiasedConcepts == null)
			modelController.fillSoipConcepts(this);
		return unbiasedConcepts;
	}
	public void setUnbiasedConcepts(ArrayList<CSIGConcept> unbiased) {
		this.unbiasedConcepts = unbiased;
	}
	public List<CSIGConcept> getImpressionsConcepts() {
		if(impressionsConcepts == null)
			modelController.fillSoipConcepts(this);
		return impressionsConcepts;
	}
	public void setImpressionsConcepts(ArrayList<CSIGConcept> impressions) {
		this.impressionsConcepts = impressions;
	}
	public List<CSIGConcept> getPlanConcepts() {
		if(planConcepts == null)
			modelController.fillSoipConcepts(this);
		return planConcepts;
	}
	public void setPlanConcepts(ArrayList<CSIGConcept> plan) {
		this.planConcepts = plan;
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
		return "S: "+getBiased()+ "\n\nO: "+getUnbiased()+ "\n\nI: "+getImpressions()+ "\n\nP: "+getPlan();
	}
	
	public CSIGPatient getPatient() {
		return patient;
	}
	public void setPatient(CSIGPatient patient) {
		for(Report i : versions)
			i.patient = patient;
	}
	public String getFacultative() {
		return facultative;
	}
	public void setFacultative(String facultative) {
		this.facultative = facultative;
	}
	
	public String getBiased() {
		return biased;
	}
	
	public void setBiased(String dictationBiased) {
		this.biased = dictationBiased;
	}

	public String getUnbiased() {
		if(biased == null) {
			modelController.fillSoip(this);
		}
		return unbiased;
	}

	public void setUnbiased(String dictationUnbiased) {
		this.unbiased = dictationUnbiased;
	}

	public String getImpressions() {
		if(impressions == null) {
			modelController.fillSoip(this);
		}
		return impressions;
	}

	public void setImpressions(String dictationImpressions) {
		this.impressions = dictationImpressions;
	}

	public String getPlan() {
		if(plan == null) {
			modelController.fillSoip(this);
		}
		return plan;
	}

	public void setPlan(String dictationPlan) {
		this.plan = dictationPlan;
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
	public void setSynonyms(HashMap<String, List<CSIGConcept>> list){
		synonyms = list;
	}
	public HashMap<String, List<CSIGConcept>> getSynonyms(){
		return synonyms;
	}
}
