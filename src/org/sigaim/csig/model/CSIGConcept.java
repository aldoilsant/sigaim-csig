package org.sigaim.csig.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.sigaim.csig.CSIGFactory;
import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.Cluster;
import org.sigaim.siie.iso13606.rm.Element;
import org.sigaim.siie.iso13606.rm.Item;

public class CSIGConcept implements Serializable {	
	private static final long serialVersionUID = 1L;
	//public String code;
	public CDCV cdcv;
	//public String terminology;
	//public String name; 
	public String custom; //custom representation specified by user
	public String text;
	public int start;  //Start char of concept
	public int end;   //End char of concept
	//public boolean detectionError = false;
	public Cluster cluster; //associated element in SIIE
	
	private void readCluster() {
		for(Item param : cluster.getParts()){
			Element el = (Element)param;
			if(param.getMeaning().getCode().equals(ModelConstants.CD_CONCEPT_CODE)){
				cdcv = (CDCV)el.getValue();
			}
		}
		//code = cdcv.getCode();
		//terminology = cdcv.getCodeSystemName();
		/*if(terminology.equals("SCTSPA"))
			terminology = "SNOMED-CT";*/
		text = cdcv.getDisplayName().getValue();
	}
	
	public CSIGConcept(Cluster _cluster, int _start, int _end){
		cluster = _cluster;
		readCluster();
		start = _start;
		end = _end;
	}
	
	public String getTerminology(){
		return cdcv.getCodeSystemName();
	}
	public String getCode() {
		return cdcv.getCode();
	}
	
	
	public CSIGConcept(CDCV cdcv){
		/*code = cdcv.getCode();
		terminology = cdcv.getCodeSystemName();
		if(terminology.equals("SCTSPA"))
			terminology = "SNOMED-CT";*/
		this.cdcv = cdcv;
		text = cdcv.getDisplayName().getValue();
	}
	
	public CSIGConcept(CSIGConcept original){
		//code = original.code;
		cdcv = original.cdcv;
		cluster = original.cluster;
		//name = original.name;
		text = original.text;
		custom = original.custom;
	}
	
	public String getConceptId(){
		return this.getTerminology()+this.getCode();
	}
	
	//Replace for a synonym, this should not affect this concept in the synonyms list
	public CSIGConcept replace(CSIGConcept c){
		/*this.code = c.code;
		this.terminology = c.terminology;*/
		this.cdcv = c.cdcv;
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
		return cdcv;
	}
	
	public Cluster getCluster(){
		return cluster;
	}
	
	/*Custom serialization, we don't want to serialize all data*/
	private void writeObject(ObjectOutputStream oos) throws IOException {
		// default serialization 
		//oos.defaultWriteObject();
		oos.writeInt(start);
		oos.writeInt(end);
		oos.writeObject(CSIGFactory.getModel().serialize(cluster));
	}
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        start = (int)stream.readInt();
        end = (int)stream.readInt();
        cluster = CSIGFactory.getModel().deserializeCluster((String)stream.readObject());
        readCluster();
    }
	
}
