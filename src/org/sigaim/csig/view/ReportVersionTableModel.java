package org.sigaim.csig.view;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.sigaim.csig.model.CSIGReport;

public class ReportVersionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -5826567355596416470L;
	
	private String[] columnNames = {"Versi\u00F3n", "Creaci\u00F3n", "Facultativo"};
	private List<CSIGReport> data;

	public ReportVersionTableModel(List<CSIGReport> _data)
	{
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
    			//return data.get(row).getVersion();
    			return row+1; //FIXME: get actual version
    		case 1:
    			return data.get(row).getCreation().getTime();
    		case 2:
    			return data.get(row).getFacultative();
    		default:
    			return new Object();
    	}
    }
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
    	switch(c) {
			case 0:
				return int.class;
			case 1:
				return Date.class;
			case 2:
				return String.class;
			default:
				return Object.class;
    	}
    }
}