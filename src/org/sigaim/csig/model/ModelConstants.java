package org.sigaim.csig.model;

public abstract class ModelConstants {

	public static final String CD_BIAS = "at0003";
	public static final String CD_UNBIAS = "at0004";
	public static final String CD_IMPRESSION = "at0005";
	public static final String CD_PLAN = "at0006";
	
	public static final String CD_CONCEPT_CODE = "at0010";
	public static final String CD_CONCEPT_START = "at0011";
	public static final String CD_CONCEPT_END = "at0012";
	public static final String CD_CONCEPT_STATUS = "at0015";
	
	//Offset between real note start andd concept position reported
	public static final int OFFSET_BIAS = 38;
	public static final int OFFSET_UNBIAS = 14;
	public static final int OFFSET_IMPRESSION = 15;
	public static final int OFFSET_PLAN = 10;
	
}
