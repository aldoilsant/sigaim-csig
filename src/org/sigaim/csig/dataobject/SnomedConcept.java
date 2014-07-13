package org.sigaim.csig.dataobject;

import java.util.Dictionary;
import java.util.Hashtable;

public class SnomedConcept {
	public int code;
	public String name;
	//TODO: sinónimos
	public String custom;  //custom representation specified by user
	public String text; //Original dictation text
	
	//static private Hashtable<Integer, SnomedConcept> loadedConcepts = new Hashtable<Integer, SnomedConcept>();
	
	public SnomedConcept(int _code, String _name, String _text){
		code = _code;
		name = _name;
		text = _text;
		/*if(!loadedConcepts.contains(_code)){
			code = _code;
			name = _name;
			loadedConcepts.put(code, this);
		}*/		
	}
	
	public SnomedConcept(SnomedConcept original){
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
