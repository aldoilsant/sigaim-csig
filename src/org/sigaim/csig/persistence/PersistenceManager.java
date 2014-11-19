package org.sigaim.csig.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class PersistenceManager {
	
	private FileOutputStream file_out;
	private File file;
	
	private PersistentObject object = null;
	private Timer timer = null;
	private static Hashtable<PersistentObject,PersistenceManager> watching =
			new Hashtable<PersistentObject,PersistenceManager>();

	private PersistenceManager(PersistentObject obj) {
		object = obj;
		timer = new Timer();
		file = new File("tmp/"+object.getClass().getName()+"/"+object.getUID());
		if(!file.exists()) {
			try {
				File folder = file.getParentFile();
				if(!folder.exists()) {
					folder.mkdirs();
				}
				file.createNewFile();
			} catch (IOException e) {
				System.err.println("Error, coult not create persistence file " + file.getAbsolutePath());
				e.printStackTrace();
				watching.remove(object);
				return;
			}
		}		
		try {
			file_out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			watching.remove(object);
			return;
		}
		TimerTask task = new TimerTask()
		{
			@Override
			public void run() {
				byte[] data = object.toData();
				try {
					FileLock lock = file_out.getChannel().lock();
					file_out.getChannel().position(0);
					file_out.getChannel().truncate(0);
					file_out.write(data);
					file_out.flush();
					file_out.getChannel().force(false);
					lock.release();
				} catch (IOException e) {
					System.err.println("Error saving status for class "+object.getClass().getName()+
							" with UID "+ object.getUID());
					e.printStackTrace();
				}
				System.out.println("Saved status in "+file.getAbsolutePath());				
			}
		}; 
		timer.scheduleAtFixedRate(task, 60000*2 /*2 min*/, 60000*2 /*2 min*/);
	}
	
	private void discard() {
		timer.cancel();
		try {
			file_out.close();
			file.delete();
		} catch (IOException e) {
			System.err.println("Could not delete status for class "+object.getClass().getName()+
					" with UID "+ object.getUID());
			//Probably not even saved
		}
		
	}
	
	public static void watch(PersistentObject obj) {
		//watching.put(obj, new PersistenceManager(obj));		
	}
	public static void discard(PersistentObject obj) {
		PersistenceManager man = watching.get(obj);
		if(man != null) {
			man.discard();
		}
	}
}
