package org.helllabs.android.xmp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

class PlayListFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return name.endsWith(".playlist");
	}
}

public class PlayList extends ListActivity {
	String name;
	
	@Override
	public void onCreate(Bundle icicle) {		
		super.onCreate(icicle);
		setContentView(R.layout.playlist);
		
		Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;
		
		name = extras.getString("name");
		setTitle(name + " - " + "No comment");
		
		updateList();
	}
	
	void updateList() {
		List<PlaylistInfo> list = new ArrayList<PlaylistInfo>();
		File file = new File(Settings.dataDir, name + ".playlist");
		String line;
		
		list.clear();
		
	     //Open the file for reading
	     try {
	    	 BufferedReader in = new BufferedReader(new FileReader(file));
	    	 while ((line = in.readLine()) != null) {
	    		 Log.v("asd", "line=" + line);
	    		 String[] fields = line.split(":", 3);
	    		 list.add(new PlaylistInfo(fields[2], fields[1], fields[0]));
	    	 }
	    	 in.close();
	     } catch (IOException e) {
	    	 
	     }		
		
	     final PlaylistInfoAdapter plist = new PlaylistInfoAdapter(PlayList.this,
    			R.layout.playlist_item, R.id.plist_info, list);
        
	     setListAdapter(plist);
	}
	
	
	// Static methods
	
	public static String[] list() {		
		return Settings.dataDir.list(new PlayListFilter());
	}
	
	public static String[] listNoSuffix() {
		String[] pList = list();
		for (int i = 0; i < pList.length; i++) {
			pList[i] = pList[i].substring(0, pList[i].lastIndexOf(".playlist"));
		}
		return pList;
	}
	
	public static void deleteList(Context context, int index) {
		String list = listNoSuffix()[index];
		File file = new File(Settings.dataDir, list + ".playlist");
		file.delete();
	}
	
	public static void addNew(Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);		  
		alert.setTitle("New playlist");  
		alert.setMessage("Enter the new playlist name:");  
		     
		final EditText input = new EditText(context);  
		alert.setView(input);  
		  
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				String value = input.getText().toString();
				File file = new File(Settings.dataDir, value + ".playlist");
				try {
					file.createNewFile();
				} catch (IOException e) {
					Toast.makeText(input.getContext(), "Error", Toast.LENGTH_SHORT)
						.show();
				}
			}  
		});  
		  
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				// Canceled.  
			}  
		});  
		  
		alert.show(); 
	}
	
	public static void addToList(Context context, String list, String line) {
		File file = new File(Settings.dataDir, list);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write(line);
			out.newLine();
			out.close();
		} catch (IOException e) {
			Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
		}
	}
}