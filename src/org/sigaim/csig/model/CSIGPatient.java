package org.sigaim.csig.model;

import org.sigaim.siie.iso13606.rm.II;

public class CSIGPatient extends CSIGIdentifiedObject {

	CSIGPatient(II siieObject) {
		super(siieObject);
	}
	
	//TODO: this patch is ugly, best case if we can delete this constructor and the super(string)
	public CSIGPatient(String id){
		super(id);
	}

}
