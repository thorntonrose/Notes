package org.rosesquared.notes;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;

/**
 * Note editor.
 *
 * @author Thornton Rose
 */
public class Editor extends Activity implements TextWatcher {
   public static final String TAG = Editor.class.getName();
   private DataStore ds = new DataStore();
   private static boolean isStarted;
   private EditText editView;
   private MenuItem deleteLineMenuItem;
   private Uri noteUri;
   private String title;
   private boolean isNew;
   private boolean isChanged;

   public static boolean isStarted() {
      return isStarted;
   }

   public MenuItem getDeleteLineMenuItem() {
      return deleteLineMenuItem;
   }

   //------------------------------------------------------------------------------------------------------------------

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      try {
         noteUri = getIntent().getData();
         Log.d(TAG, "onCreate: noteUri: " + noteUri);
         title = getTitle(noteUri);
         isNew = title.equals("");

         if (! isNew) { getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); }
         setContentView(R.layout.editor);
         getActionBar().setTitle(title);

         editView = (EditText) findViewById(R.id.editor_view);
         editView.setText(isNew ? "" : ds.readNote(title));
         editView.addTextChangedListener(this);
      } catch(Exception e) {
         Log.e(TAG, e.toString());
         Messages.error(this, e);
      }
   }

   @Override
   protected void onStart() {
      Log.d(TAG, "onStart");
      isStarted = true;
      super.onStart();
   }

   @Override
   protected void onStop() {
      Log.d(TAG, "onStop");
      isStarted = false;
      super.onStop();
   }

   /*
   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      Log.d(TAG, "onConfigurationChanged: orientation: " + newConfig.orientation);
      super.onConfigurationChanged(newConfig);
   }
   */
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.editor, menu);
      deleteLineMenuItem = menu.findItem(R.id.menu_delete_line);
      setMenuItemVisibility();

      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      try {
         switch (item.getItemId()) {
            case R.id.menu_accept:
               Log.d(TAG, "onOptionsItemSelected: menu_accept");
               onAccept();
               break;

            case R.id.menu_delete:
               Log.d(TAG, "onOptionsItemSelected: menu_delete");
               onDelete();
               break;

            case R.id.menu_delete_line:
               Log.d(TAG, "onOptionsItemSelected: menu_delete_line");
               onDeleteLine();
               break;
         }
      } catch(Exception e) {
         Log.e(TAG, e.toString());
         Messages.error(this, e);
      }

      return super.onOptionsItemSelected(item);
   }
   
   public void setMenuItemVisibility() {
      deleteLineMenuItem.setVisible(hasText());
   }

   @Override
   public void beforeTextChanged(CharSequence s, int start, int count, int after) {
   }

   @Override
   public void onTextChanged(CharSequence s, int start, int before, int count) {
   }

   @Override
   public void afterTextChanged(Editable s) {
      isChanged = true;
      getActionBar().setTitle(getTitle(editView.getText().toString()));
      setMenuItemVisibility();
   }

   @Override
   public void onBackPressed() {
      Log.d(TAG, "onBackPressed: isChanged: " + isChanged);
      if (isChanged) { onAccept(); } else { finish(); }
   }
   
   public void onDeleteLine() {
      Log.d(TAG, "onDeleteLine");
      Layout layout = editView.getLayout();
      int lineNum = layout.getLineForOffset(editView.getSelectionStart());
      int start = layout.getLineStart(lineNum);
      int end = layout.getLineEnd(lineNum);
      Editable text = editView.getText().delete(start, end);
      editView.setTextKeepState(text);
   }

   public void onAccept() {
      try {
         Log.d(TAG, "onAccept");
         hideKeyboard();
         String note = editView.getText().toString();
         String newTitle = getTitle(note);

         if (note.trim().equals("")) {
            deleteNote();
            finish();
         } else if (!title.equals(newTitle) && ds.exists(newTitle)) {
            confirmOverwrite(newTitle, note);
         } else {
            saveNote(newTitle, note);
            finish();
         }
      } catch(Exception e) {
         Log.e(TAG, e.toString());
         Messages.error(this, e);
      }
   }

   public void confirmOverwrite(final String newTitle, final String note) {
      Messages.confirm(
         this,
         "Note \"" + newTitle + "\" already exists. Do you want to overwrite it?",
         new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Log.d(TAG, "confirmOverwrite: positive: onClick");
               saveNote(newTitle, note);
               finish();
            }
         },
         new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Log.d(TAG, "confirmOverwrite: negative: onClick");
            }
         });
   }

   public void saveNote(String newTitle, String note) {
      if (!isNew && !title.equals(newTitle)) { deleteNote(); }
      ds.writeNote(newTitle, note);
   }
   
   public void onDelete() {
      Log.d(TAG, "onDelete");

      if (hasText()) {
         confirmDelete();
      } else {
         deleteNote();
         finish();
      }
   }

   public void confirmDelete() {
      Messages.confirm(
         this,
         "Are you sure you want to delete this note?",
         new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Log.d(TAG, "confirmDelete: positive: onClick");
               deleteNote();
               finish();
            }
         },
         new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Log.d(TAG, "confirmDelete: negative: onClick");
            }
         });
   }

   public void deleteNote() {
      ds.deleteNote(title);
   }

   public void hideKeyboard() {
      InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
   }

   public String getTitle(String note) {
      int newlineIndex = note.indexOf("\n");
      return newlineIndex == -1 ? note : note.substring(0, newlineIndex);
   }

   public String getTitle(Uri noteUri) {
      String path = noteUri == null ? "" : noteUri.getPath();
      return path.startsWith("/") ? path.substring(1) : path;
   }
   
   public boolean hasText() {
      return editView.getText().toString().length() > 0;
   }
}
