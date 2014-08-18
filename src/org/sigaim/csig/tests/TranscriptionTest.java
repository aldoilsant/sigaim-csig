package org.sigaim.csig.tests;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import es.udc.tic.rnasa.sigaim_transcriptor_client.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TranscriptionTest {
	

	@Test
	public void test() {
		TranscriptionClientApiImpl client = new TranscriptionClientApiImpl();
		if( client.connect(new InetSocketAddress("193.144.33.81",8000)) != true)
			fail("Could not connect");
	    Path path = Paths.get("C:/Users/siro.gonzalez/workspace/SIGAIM_csig/resources", "es-0020.flac");
	    
	    if( path.toFile().exists() != true )
	    	fail("File not found");
	    client.addObserver(new TranscriptionObserver());
		client.transcribeFlac(path);
		try {
			Thread.sleep(120*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
