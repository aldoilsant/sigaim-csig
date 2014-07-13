package org.sigaim.csig.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.sigaim.csig.model.Report;

public class ReportTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -5826567357696416470L;
	
	SimpleDateFormat sdf;
	
	private String[] columnNames = {"Creaci\u00F3n", "Ult. versi\u00F3n", "Paciente", "Facultativo", "Informe", "Notas"};
	private List<Report> data; 

	public ReportTableModel(List<Report> _data)
	{
		sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		//sdf.setTimeZone();
		data = _data;
	}

	public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
    	switch(col) {
    		case 0:
    			return sdf.format(data.get(row).getCreation().getTime());
    		case 1:
    			List<Report> versions = data.get(row).getVersions();
    			return sdf.format(versions.get(versions.size()-1).getCreation().getTime());
    		case 2:
    			return data.get(row).getPatient();
    		case 3:
    			return data.get(row).getFacultative();
    		case 4:
    			return data.get(row).getId();
    		case 5:
    			return new String("");
    		default:
    			return new Object();
    	}
    }
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
    	switch(c) {
			case 0:
			case 1:
				return String.class;
			case 2:
			case 3:
			case 4:
			case 5:
				return String.class;
			default:
				return Object.class;
    	}
    }
}