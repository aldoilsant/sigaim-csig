package org.sigaim.csig.tests;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

import org.junit.Test;

import es.udc.tic.rnasa.sigaim_transcriptor.client.*;

/**
 * This test does not ensure that transcription is working even if successful run.
 * You must check output log to ensure the transcription is received.
 * @author siro.gonzalez
 *
 */
public class TranscriptionTest {
	

	@Test
	public void test() {
		InetSocketAddress serverAddr= new InetSocketAddress("193.144.33.85",8080);
		
		TranscriptionClientApi client = new TranscriptionClientApiImpl(serverAddr);
		System.out.println("Connecting to "+serverAddr);
		client.connect();
		System.out.println("Connected?");
		((TranscriptionClientApiImpl)client).addTranscriptionListener(new TranscriptionObserver());
		client.ping();
		//if( )
		//	fail("Could not connect");
	    Path path = Paths.get("C:/Users/siro.gonzalez/workspace/SIGAIM_csig/resources", "es-0020.flac");
	    
	    if( path.toFile().exists() != true )
	    	fail("File not found");
	    //client.addObserver(new TranscriptionObserver())
		client.transcribeFlac(path);
		try {
			Thread.sleep(120*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
