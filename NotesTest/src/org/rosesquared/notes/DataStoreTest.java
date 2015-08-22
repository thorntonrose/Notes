package org.rosesquared.notes;

import android.test.*;
import java.util.*;

/**
 * DataStore tests.
 *
 * @author ethorro
 */
public class DataStoreTest extends AndroidTestCase {
   public static final String TAG = DataStoreTest.class.getName();
   private DataStore ds;

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      ds = new DataStore();
      ds.deleteAll();
   }

   //------------------------------------------------------------------------------------------------------------------

   public void testGetTitles_NoFiles() {
      List<String> titles = ds.getTitles();
      assertEquals("titles.size:", 0, titles.size());
   }

   public void testGetTitles() {
      ds.writeNote("Test GetTitles", "test");
      List<String> titles = ds.getTitles();
      assertEquals("titles.size:", 1, titles.size());
   }

   public void testWriteNote() {
      String title = "Test Write";
      ds.writeNote(title, "test");
      assertTrue("not exists: Test", ds.exists(title));
      String note = ds.readNote(title);
      assertEquals("note:", "test", note);
   }

   public void testReadNote() {
      String title = "Test Read";
      ds.writeNote(title, "test");
      String note = ds.readNote(title);
      assertEquals("note:", "test", note);
   }

   public void testExists() {
      String title = "Test Exists";
      ds.writeNote(title, "test");
      assertTrue("not exists: Test", ds.exists(title));
      assertFalse("exists: Test2", ds.exists("Test2"));
   }
}
