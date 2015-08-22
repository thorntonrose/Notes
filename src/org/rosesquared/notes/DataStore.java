package org.rosesquared.notes;

import android.os.*;
import android.util.*;
import java.io.*;
import java.util.*;

/**
 * Data storage interface.
 *
 * @author Thornton Rose
 */
public final class DataStore {
   public final String TAG = DataStore.class.getName();
   public File notesDir;

   public DataStore() {
      notesDir = new File(Environment.getExternalStorageDirectory() + "/Documents/Notes" +
         (BuildConfig.DEBUG ? ".debug" : ""));
      Log.d(TAG, "<clinit>: notesDir: " + notesDir);

      if (! notesDir.exists()) { 
         notesDir.mkdirs(); 
         Log.d(TAG, "<clinit>: created " + notesDir);
      }
   }

   //------------------------------------------------------------------------------------------------------------------

   public String encode(String s) {
      return s.replaceAll("/", "%2F");
   }

   public String decode(String s) {
      return s.replaceAll("%2F", "/");
   }

   public File getFile(String title) {
      return new File(notesDir + "/" + encode(title) + ".txt");
   }

   public List<String> getTitles() {
      List<String> titles = new ArrayList<String>();

      for (String fileName : notesDir.list()) {
         if (fileName.endsWith(".txt")) {
            titles.add(decode(fileName.substring(0, fileName.lastIndexOf(".txt"))));
         }
      }

      return titles;
   }

   public String readNote(String title) {
      try {
         StringBuilder note = new StringBuilder();
         File file = getFile(title);
         Log.d(TAG, "readNote: file: " + file);
         BufferedReader reader = new BufferedReader(new FileReader(file));

         try {
            String line;

            while ((line = reader.readLine()) != null) {
               if (note.length() > 0) { note.append("\n"); }
               note.append(line);
            }
         } finally {
            reader.close();
         }

         return note.toString();
      } catch(Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void writeNote(String title, String note) {
      try {
         File file = getFile(title);
         Log.d(TAG, "writeNote: file: " + file);
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         try {
            writer.write(note);
         } finally {
            writer.close();
         }
      } catch(Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void deleteNote(String title) {
      File file = getFile(title);
      Log.d(TAG, "deleteNote: file: " + file);
      file.delete();
   }

   public void deleteAll() {
      Log.d(TAG, "deleteAll...");
      File[] files = notesDir.listFiles();

      for (File file : files) { 
         if (file.getName().endsWith(".txt")) { file.delete(); }
      }
   }
   
   public boolean exists(String title) {
      Log.d(TAG, "exists: title: " + title);
      return getFile(title).exists();
   }
}
