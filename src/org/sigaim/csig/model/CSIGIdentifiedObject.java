package org.sigaim.csig.model;

import org.sigaim.siie.iso13606.rm.II;

public abstract class CSIGIdentifiedObject {

	private long id;
	private String root;
	
	protected CSIGIdentifiedObject(II siieObject) {
		id = Long.parseLong(siieObject.getExtension());		
		root = siieObject.getRoot();
	}
	
	protected CSIGIdentifiedObject() {
		id = -1;
		root = "";
	}
	
	public void setId(II ii) {
		id = Long.parseLong(ii.getExtension());
		root = ii.getRoot();
	}
	
	public long getId() {
		return id;
	}
	
	public String getIdRoot(){
		return root;
	}
	
	public String toString() {
		return root + "/" + id; 
	}
}
