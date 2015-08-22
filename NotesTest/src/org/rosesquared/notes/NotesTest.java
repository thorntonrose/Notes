package org.rosesquared.notes;

import android.app.*;
import android.test.*;
import android.widget.*;

public class NotesTest extends ActivityInstrumentationTestCase2<Notes> {
   public static final String TAG = NotesTest.class.getName();
   private DataStore ds;
   private Instrumentation instrumentation;
   private Notes notes;
   private ListView listView;
   private ListAdapter listAdapter;
   
   public NotesTest() {
      super(Notes.class.getPackage().getName(), Notes.class);
   }

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      instrumentation = getInstrumentation();
      ds = new DataStore();
      ds.deleteAll();
   }
   
   public void start() {
      notes = getActivity();
      listView = notes.getListView();
      listAdapter = listView.getAdapter();
   }

   //------------------------------------------------------------------------------------------------------------------

   public void testStart_NoFiles() {
      start();
      assertTrue("listView.isShown == false", listView.isShown());
      assertEquals("count:", 0, listAdapter.getCount());
   }

   public void testStart_Files() {
      ds.writeNote("Note 1", "test");
      start();
      assertTrue("listView.isShown == false", listView.isShown());
      assertEquals("count:", 1, listAdapter.getCount());
   }

   public void testItemClick() throws Exception {
      ds.writeNote("Note 1", "test");
      start();
      Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(Editor.class.getName(), null, false);

      notes.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            listView.performItemClick(listView, 0, 0);
         }
      });

      instrumentation.waitForIdleSync();
      Activity editor = instrumentation.waitForMonitorWithTimeout(monitor, 3*1000);
      notes.finishActivity(1);
      assertNotNull("Editor not started in 3 sec.", editor);
   }
}
