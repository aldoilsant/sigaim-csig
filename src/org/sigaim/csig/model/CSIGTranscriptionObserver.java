package org.sigaim.csig.model;

import java.util.Observable;
import java.util.Observer;

public class CSIGTranscriptionObserver implements Observer {

	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("update("+arg0+", "+arg1+")");
		assert( arg1 instanceof String );
	}
	
	public CSIGTranscriptionObserver() {
		System.out.println("Observer instantiated");
	}
	
	//public void setCallback()

}
