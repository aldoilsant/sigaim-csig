package org.sigaim.csig.model;

import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.ST;

public class CSIGConcept {	
	public String code;
	public String terminology;
	//public String name; 
	public String custom; //custom representation specified by user
	public String text;
	public int start;  //Start char of concept
	public int end;   //End char of concept
	//public boolean detectionError = false;
	
	public CSIGConcept(CDCV cdcv, int _start, int _end){
		code = cdcv.getCode();
		terminology = cdcv.getCodeSystemName();
		if(terminology.equals("SCTSPA"))
			terminology = "SNOMED-CT";
		text = cdcv.getDisplayName().getValue();
		start = _start;
		end = _end;
	}
	
	public CSIGConcept(CDCV cdcv){
		code = cdcv.getCode();
		terminology = cdcv.getCodeSystemName();
		if(terminology.equals("SCTSPA"))
			terminology = "SNOMED-CT";
		text = cdcv.getDisplayName().getValue();
	}
	
	public CSIGConcept(CSIGConcept original){
		code = original.code;
		//name = original.name;
		text = original.text;
		custom = original.custom;
	}
	
	public String getConceptId(){
		return terminology+code;
	}
	
	//Replace for a synonym, this should not affect this concept in the synonyms list
	public CSIGConcept replace(CSIGConcept c){
		this.code = c.code;
		this.terminology = c.terminology;
		this.text = c.text;
		return this;
	}
	
	static public String getConceptId(CDCV cdcv){
		if(cdcv.getCodeSystemName().equals("SCTSPA")){
			return "SNOMED-CT" + cdcv.getCode();
		} else
		return cdcv.getCodeSystemName() + cdcv.getCode();
	}

	@Override
	public String toString() {
		return text;
	}
	
	public CDCV getCDCV(){
		CDCV rtn = new CDCV();
		rtn.setCode(this.code);
		rtn.setCodeSystemName(this.terminology);
		ST st_text = new ST();
		st_text.setValue(text);
		rtn.setDisplayName(st_text);
		
		return rtn;
	}
	
}
