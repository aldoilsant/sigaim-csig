package view;

import java.util.List;

import dataobject.Report;

public interface ViewController {

	void doLogin(String user, char[] pass);
	public List<Report> getInforms();
	public void newReport();
	public void openReport(Report r);
	public void showReport(Report r);
}