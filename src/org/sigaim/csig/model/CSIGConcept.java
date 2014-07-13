package org.sigaim.csig.model;

public class CSIGConcept {
	public String code;
	public String terminology;
	public String name;
	public String custom; //custom representation specified by user
	public String text;  //Original dictation text
	public int start;  //Start char of concept
	public int end;   //End char of concept
	
	public CSIGConcept(String _code, String _terminology, int _start, int _end){
		code = _code;
		terminology = _terminology;
		start = _start;
		end = _end;
	}
	
	public CSIGConcept(CSIGConcept original){
		code = original.code;
		name = original.name;
		text = original.text;
		custom = original.custom;
	}

	@Override
	public String toString() {
		if(custom != null)
			return custom;
		else
			return text;
	}
	
}
