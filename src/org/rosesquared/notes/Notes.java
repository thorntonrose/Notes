package org.rosesquared.notes;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

// ???: Possible preferences:
// * Text appearance
// * Notes directory
// * Theme

/**
 * Notes main class.
 *
 * @author Thornton Rose
 */
@SuppressWarnings("unchecked")
public class Notes extends ListActivity {
   public static final String TAG = Notes.class.getName();
   private DataStore ds = new DataStore();
   private Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority("Notes");
   private List<String> titles;
   private ArrayAdapter<String> listAdapter;

   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

      try {
         setContentView(R.layout.list);
         listAdapter = new ArrayAdapter<String>(this, R.layout.row);
         setListAdapter(listAdapter);
         loadTitles();
      } catch(Exception e) {
         Log.e(TAG, e.toString());
         Messages.error(this, e);
      }
   }

   public void loadTitles() {
      titles = ds.getTitles();
      listAdapter.setNotifyOnChange(false);
      listAdapter.clear();
      listAdapter.addAll(titles);
      listAdapter.setNotifyOnChange(true);
   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}

   @Override
   protected void onResume() {
      loadTitles();
      listAdapter.notifyDataSetChanged();
      super.onResume();
   }

   @Override
   protected void onListItemClick(ListView l, View v, int position, long rowId) {
      editNote(titles.get((int) rowId));
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      try {
         switch (item.getItemId()) {
            case R.id.menu_add:
               Log.d(TAG, "onOptionsItemSelected: menu_add");
               editNote("");
               break;

            case R.id.menu_about:
               Log.d(TAG, "onOptionsItemSelected: menu_about");
               Messages.about(this);
               break;
         }
      } catch(Exception e) {
         Log.e(TAG, e.toString());
         Messages.error(this, e);
      }

      return super.onOptionsItemSelected(item);
   }

   public void editNote(String title) {
      try {
         Uri noteUri = uriBuilder.path(title).build();
         startActivityForResult(new Intent(Intent.ACTION_EDIT, noteUri, this, Editor.class), 1);
      } catch(Exception e) {
         Log.e(TAG, e.toString());
         Messages.error(this, e);
      }
   }
}
