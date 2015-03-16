package org.sigaim.csig.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;






import org.sigaim.siie.iso13606.rm.CDCV;
//import org.openehr.rm.datastructure.itemstructure.representation.Item;
import org.sigaim.siie.iso13606.rm.Cluster;
import org.sigaim.siie.iso13606.rm.Element;
import org.sigaim.siie.iso13606.rm.II;
import org.sigaim.siie.iso13606.rm.Item;
import org.sigaim.siie.iso13606.rm.ST;


public class CSIGReport extends CSIGIdentifiedObject implements Cloneable {
	
	private List<CSIGConcept> biasedConcepts;
	private List<CSIGConcept> unbiasedConcepts;
	private List<CSIGConcept> impressionsConcepts;
	private List<CSIGConcept> planConcepts;
	private Calendar creation;
	//private int versionNumber;
	//private ArrayList<CSIGReport> versions;
	private CSIGPatient patient;
	private String facultative;
	private long ehr;
	private II versionSet;
	//private String reportId;
	private boolean lastVersion = false;
	
	private String biased;
	private String unbiased;
	private String impressions;
	private String plan;
	
	private HashMap<String, List<CSIGConcept>> synonyms;
	
	private IntCSIGModel modelController;
	
	/**
	 * Does not copy referenced objects.
	 */
	public CSIGReport clone() throws CloneNotSupportedException {
		return (CSIGReport) super.clone();
	}
	
	public CSIGReport(IntCSIGModel controller) {
		modelController = controller;
		//versionNumber = 1;
		creation = Calendar.getInstance();
		//versions = new ArrayList<CSIGReport>();
		//versions.add(this);
	}
	public CSIGReport(CSIGReport prev) {
		modelController = prev.modelController;
		//versionNumber = prev.versionNumber + 1;
		creation = Calendar.getInstance();
		//versions = prev.versions;
		/*biased = copySoip(prev.biased);
		unbiased = copySoip(prev.unbiased);
		impressions = copySoip(prev.impressions);
		plan = copySoip(prev.plan);*/
		
		patient = prev.patient;
		facultative = prev.facultative;
		//versions.add(this);
	}
	
	public void fillConcepts(){
		if(biasedConcepts == null)
			modelController.fillSoipConcepts(this);
	}
	
	
	/* GETERS & SETTERS */
	public List<CSIGConcept> getBiasedConcepts() {
		fillConcepts();
		return biasedConcepts;
	}
	public void setBiasedConcepts(ArrayList<CSIGConcept> biased) {
		this.biasedConcepts = biased;
	}

	public List<CSIGConcept> getUnbiasedConcepts() {
		fillConcepts();
		return unbiasedConcepts;
	}
	public void setUnbiasedConcepts(ArrayList<CSIGConcept> unbiased) {
		this.unbiasedConcepts = unbiased;
	}
	public List<CSIGConcept> getImpressionsConcepts() {
		fillConcepts();
		return impressionsConcepts;
	}
	public void setImpressionsConcepts(ArrayList<CSIGConcept> impressions) {
		this.impressionsConcepts = impressions;
	}
	public List<CSIGConcept> getPlanConcepts() {
		fillConcepts();
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
	/*public List<CSIGReport> getVersions() {
		return versions;
	}*/
	public String getFullText() {
		return "S: "+getBiased()+ "\n\nO: "+getUnbiased()+ "\n\nI: "+getImpressions()+ "\n\nP: "+getPlan();
	}
	
	public CSIGPatient getPatient() {
		return patient;
	}
	public void setPatient(CSIGPatient patient) {
		//for(CSIGReport i : versions)
			this.patient = patient;
	}
	public String getFacultative() {
		return facultative;
	}
	public void setFacultative(String facultative) {
		this.facultative = facultative;
	}
	
	public String getBiased() {
		if(biased == null) {
			modelController.fillSoip(this);
		}
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
	
	/*public int getVersion(){
		return this.versionNumber;
	}*/
	
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
	public void setVersionSet(II versionset){
		versionSet = versionset;
	}
	public II getVersionSet(){
		return versionSet;
	}
	
	public void isLastVersion(boolean isit){
		lastVersion = isit;
	}
	public boolean isLastVersion(){
		return lastVersion;
	}
	
	/* SERIALIZATION */
	
	public Cluster toCluster(){
		Cluster rtn = new Cluster();
		List<Item> items = rtn.getParts();
		
		Element biased = new Element();
		CDCV meaning = new CDCV();
		meaning.setCode(ModelConstants.CD_BIAS);
		biased.setMeaning(meaning);
		ST st_biased = new ST();
		st_biased.setValue(this.biased.trim());
		biased.setValue(st_biased);
		items.add(biased);
		
		Element unbiased = new Element();
		CDCV meaning0 = new CDCV();
		meaning0.setCode(ModelConstants.CD_UNBIAS);
		unbiased.setMeaning(meaning0);
		ST st_unbiased = new ST();
		st_unbiased.setValue(this.unbiased.trim());
		unbiased.setValue(st_unbiased);
		items.add(unbiased);
		
		Element impression = new Element();
		CDCV meaning1 = new CDCV();
		meaning1.setCode(ModelConstants.CD_IMPRESSION);
		impression.setMeaning(meaning1);
		ST st_impression = new ST();
		st_impression.setValue(this.impressions.trim());
		impression.setValue(st_impression);
		items.add(impression);
		
		Element plan = new Element();
		CDCV meaning2 = new CDCV();
		meaning2.setCode(ModelConstants.CD_PLAN);
		plan.setMeaning(meaning2);
		ST st_plan = new ST();
		st_plan.setValue(this.plan.trim());
		plan.setValue(st_plan);
		items.add(plan);
		
		return rtn;
	}
	
}
