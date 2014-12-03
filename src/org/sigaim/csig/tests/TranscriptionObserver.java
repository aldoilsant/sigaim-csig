package org.sigaim.csig.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.junit.internal.runners.statements.Fail;
import org.junit.Assert;

import es.udc.tic.rnasa.sigaim_transcriptor.client.ui.SwingClientFrame;
import es.udc.tic.rnasa.sigaim_transcriptor.client.util.TranscriptionStateEvent;
import es.udc.tic.rnasa.sigaim_transcriptor.client.ui.event.*;
import es.udc.tic.rnasa.sigaim_transcriptor.client.util.TranscriptionBundle;

public class TranscriptionObserver implements TranscriptionListener {

	/*@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("update("+arg0+", "+arg1+")");
		assert( arg1 instanceof String );
	}*/

	public TranscriptionObserver() {
		System.out.println("Observer instantiated");
	}

	@Override
	public void transcriptionActionPerformed(TranscriptionEvent event) {
		final TranscriptionBundle transcriptionBundle = event.getTranscriptionBundle();
		final TranscriptionStateEvent stateEvent = event.getTranscriptionStateEvent();

		switch(stateEvent)
		{
		case CONNECTION_CLOSED:
			System.out.println("CONNECTION_CLOSED");
			break;
		case CONNECTION_FAILED:
			System.out.println("CONNECTION_FAILED");
			break;
		case NOT_CONNECTED:
			System.out.println("NOT_CONNECTED");
			break;
		case PING_SENT:
			System.out.println("PING_SENT");
			break;
		case PONG_RECEIVED:
			System.out.println("PONG_RECEIVED");
			break;
		case READY_FOR_DATA:
			System.out.println("READY_FOR_DATA");
			break;
		case TRANSCRIPTION_FAILED:
			System.out.println("TRANSCRIPTION_FAILED");
			break;
		case TRANSCRIPTION_RECEIVED:
			// Read transcription from file
			try(BufferedReader reader = Files.newBufferedReader(transcriptionBundle.getTranscription(), Charset.forName("UTF-8")))
			{
				String line = null;
				StringBuilder transcription = new StringBuilder();
				while((line = reader.readLine()) != null)
				{
					transcription.append(line);
				}

				System.out.println(transcription);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				Assert.fail("Exception transcribing");
			}
			break;
		case AUDIO_SENT:
			System.out.println("AUDIO_SENT");
			break;
		case EXCEPTION:
			System.out.println("EXCEPTION");
			System.out.println(stateEvent.getDescription());
			break;
		default:
			Assert.fail("Unknown response");
			break;
		}
	}

}
