package org.sigaim.csig.view.helper;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

public abstract class Audio {
	public static TargetDataLine getLine() {
		AudioFormat af = new AudioFormat(16000, 16, 1, true, false);
		
		final String inputTypeString = "MICROPHONE"; // or COMPACT_DISC or MICROPHONE etc ...
		final Port.Info myInputType = new Port.Info((Port.class), inputTypeString, true);
		final DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, af);

	    //Go through the System audio mixer
	    for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
	    	try {
	    		Mixer targetMixer = AudioSystem.getMixer(mixerInfo);
	    		targetMixer.open();
	    		//Check if it supports the desired format
	    		if (targetMixer.isLineSupported(targetDataLineInfo)) {
	    			//System.out.println(mixerInfo.getName() + " supports recording @" + af);
	    			//now go back and start again trying to match a mixer to a port
	    			//the only way I figured how is by matching name, because 
	    			//the port mixer name is the same as the actual mixer with "Port " in front of it
	    			//there MUST be a better way
	    			for (Mixer.Info mifo : AudioSystem.getMixerInfo()) {
	    				String port_string = "Port ";
	    				if ((port_string + mixerInfo.getName()).equals(mifo.getName())) {
	    					System.out.println("Matched Port to Mixer:" + mixerInfo.getName());
	    					Mixer portMixer = AudioSystem.getMixer(mifo);
	    					portMixer.open();
	    					portMixer.isLineSupported((Line.Info) myInputType);
	    					//now check the mixer has the right input type eg LINE_IN
	    					if (portMixer.isLineSupported((Line.Info) myInputType)) {
	    						//OK we have a supported Port Type for the Mixer
	    						//This has all matched (hopefully)
	    						//now just get the record line
	    						//There should be at least 1 line, usually 32 and possible unlimited
	    						//which would be "AudioSystem.Unspecified" if we ask the mixer 
	    						//but I haven't checked any of this
	    						TargetDataLine line = (TargetDataLine) targetMixer.getLine(targetDataLineInfo);
	    						System.out.println("Got TargetDataLine from :" + targetMixer.getMixerInfo().getName());
	    						//targetMixer.close();
	    						return line;
	    					}
	    				}
	    			}
	    		}
	    		targetMixer.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		return null;
	}
}
