package org.sigaim.csig;

import org.sigaim.csig.model.CSIGModel;
import org.sigaim.csig.model.IntCSIGModel;

//Currently this factory performs a "singleton" of all interfaces.
public abstract class CSIGFactory {

	private static CSIGModel model = null;
	private static String baseUrl = null;
	
	public static void setBaseUrl(String url){
		if(url == null)
			throw new IllegalStateException("You must call CSIGFactory.setBaseUrl() with a valid non null string");
		baseUrl = url;
	}
	
	public static IntCSIGModel getModel(){
		if(model == null) {
			if(baseUrl == null)
				throw new IllegalStateException("You must call CSIGFactory.setBaseUrl() before using this.");
			model = new CSIGModel(baseUrl);
		}
		return model;
	}
}
