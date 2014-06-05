package dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Report {

	public Report() {
		versionNumber = 1;
		creation = new Date();
		versions = new ArrayList<Report>();
		versions.add(this);
	}
	public Report(Report prev) {
		versionNumber = prev.versionNumber + 1;
		creation = new Date();
		versions = prev.versions;
		biased = prev.biased;
		unbiased = prev.unbiased;
		impressions = prev.impressions;
		plan = prev.plan;
		patient = prev.patient;
		facultative = prev.facultative;
		versions.add(this);
	}
	
	public String getBiased() {
		return biased;
	}
	public void setBiased(String biased) {
		this.biased = biased;
	}
	public String getUnbiased() {
		return unbiased;
	}
	public void setUnbiased(String unbiased) {
		this.unbiased = unbiased;
	}
	public String getImpressions() {
		return impressions;
	}
	public void setImpressions(String impressions) {
		this.impressions = impressions;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public Date getCreation() {
		return creation;
	}
	public List<Report> getVersions() {
		return versions;
	}
	public String getFullText() {
		return "S: "+getBiased()+ "\n\nO: "+getUnbiased()+ "\n\nI: "+getImpressions()+ "\n\nP: "+getPlan();
	}

	private String biased;
	private String unbiased;
	private String impressions;
	private String plan;
	private Date creation;
	private int versionNumber;
	private ArrayList<Report> versions;
	private String patient;
	private String facultative;
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
	
}
