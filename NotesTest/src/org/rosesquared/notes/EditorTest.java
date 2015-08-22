package org.rosesquared.notes;

import android.app.*;
import android.test.*;
import android.widget.*;

public class EditorTest extends ActivityInstrumentationTestCase2<Editor> {
   public static final String TAG = EditorTest.class.getName();
   private Instrumentation instrumentation;
   private Editor editor;
   private EditText editView;
   
   public EditorTest() {
      super(Editor.class.getPackage().getName(), Editor.class);
   }
   
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      instrumentation = getInstrumentation();
      editor = getActivity();
      editView = (EditText) editor.findViewById(R.id.editor_view);
   }
   
   @Override
   protected void tearDown() throws Exception {
      editor.finish();
   }

   public void testDeleteLineMenuItemVisiblity() {
      assertFalse("deleteLineMenuItem.isVisible == true", editor.getDeleteLineMenuItem().isVisible());

      editor.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            editView.setText("a");
         }
      });

      instrumentation.waitForIdleSync();
      assertTrue("deleteLineMenuItem.isVisible == false", editor.getDeleteLineMenuItem().isVisible());

      editor.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            editView.setText("");
         }
      });

      instrumentation.waitForIdleSync();
      assertFalse("deleteLineMenuItem.isVisible == true", editor.getDeleteLineMenuItem().isVisible());
   }

   public void testDeleteLine() {
      editor.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            editView.setText("a");
         }
      });

      instrumentation.waitForIdleSync();
      editor.onOptionsItemSelected(editor.getDeleteLineMenuItem());
      assertEquals("editView.text:", "", editView.getText().toString());

      editor.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            editView.setText("a\nb\nc");
            editView.setSelection(2);
         }
      });

      instrumentation.waitForIdleSync();
      editor.onOptionsItemSelected(editor.getDeleteLineMenuItem());
      assertEquals("editView.text:", "a\nc", editView.getText().toString());
   }
}
