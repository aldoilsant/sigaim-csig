package org.sigaim.csig.persistence;

public interface PersistentObject {

	byte[] toData();
	void restore();
	String getUID();
}
