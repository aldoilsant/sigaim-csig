package org.sigaim.csig.tests;

import java.util.Observable;
import java.util.Observer;

public class TranscriptionObserver implements Observer {

	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("update("+arg0+", "+arg1+")");
		assert( arg1 instanceof String );
	}
	
	public TranscriptionObserver() {
		System.out.println("Observer instantiated");
	}

}
