package org.sigaim.csig.model;

import org.sigaim.siie.iso13606.rm.II;

public abstract class CSIGIdentifiedObject {
	
	private long id;
	private String root;
	private II ii;
	
	protected CSIGIdentifiedObject(II siieObject) {
		id = Long.parseLong(siieObject.getExtension());		
		root = siieObject.getRoot();
		ii = siieObject;
	}
	
	protected CSIGIdentifiedObject(String code) {
		id = Long.parseLong(code.split("/")[1]);
		root = code.split("/")[0];
		ii = new II();
		ii.setExtension(code);
		ii.setRoot(root);
	}
	
	protected CSIGIdentifiedObject() {
		id = -1;
		root = "";
		ii = null;
	}
	
	public void setId(II ii) {
		id = Long.parseLong(ii.getExtension());
		root = ii.getRoot();
		this.ii = ii;
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
	
	public II getII(){
		return ii;
	}
}
