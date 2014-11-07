package org.sigaim.csig.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.sigaim.csig.model.CSIGPatient;
import org.sigaim.csig.model.CSIGReport;

public class ReportTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -5826567357696416470L;
	
	SimpleDateFormat sdf;
	
	private String[] columnNames = {"Ult. versi\u00F3n", "Paciente", "Facultativo", "Informe"};
	private List<CSIGReport> data; 

	public ReportTableModel(List<CSIGReport> _data)
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
    			return data.get(row).getPatient();
    		case 2:
    			return data.get(row).getFacultative();
    		case 3:
    			return data.get(row).getId();
    		default:
    			return new Object();
    	}
    }
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
    	switch(c) {
			case 0:
				return String.class;
			case 1:
				return CSIGPatient.class;
			case 2:
				return String.class;
			case 3:
				return Long.class;
			default:
				return Object.class;
    	}
    }
}