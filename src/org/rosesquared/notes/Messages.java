package org.rosesquared.notes;

import android.app.*;
import android.content.*;
import android.widget.*;

/**
 * Messages utility.
 *
 * @author Thornton Rose
 */
public final class Messages {
   private Messages() {}

   public static String getAppVersion(Context context) {
      try {
         return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
      } catch(Exception e) {
         error(context, e);
      }

      return "1.0";
   }

   public static void about(Context context) {
      info(
         context, 
         "About",
         context.getString(R.string.app_name) + " " + getAppVersion(context) + 
            "\nby " + context.getString(R.string.author) + 
            "\n" + context.getString(R.string.author_email)
      );
   }

   public static void error(Context context, Throwable t) {
      StringBuilder message = new StringBuilder().append(context).append(":\n").append(t.toString());
      StackTraceElement[] stacktrace = t.getStackTrace();

      for (int i = 0; i < Math.min(3, stacktrace.length); i ++) {
         message.append("\nat ").append(stacktrace[i]);
      }

      alert(context, "Error", message.toString());
   }

   public static void confirm(Context context, String message, DialogInterface.OnClickListener positiveListener,
         DialogInterface.OnClickListener negativeListener) {
      new AlertDialog.Builder(context)
         .setIcon(android.R.drawable.ic_dialog_info)
         .setTitle("Confirm")
         .setMessage(message)
         .setCancelable(false)
         .setPositiveButton("Yes", positiveListener)
         .setNegativeButton("No", negativeListener)
         .show();
   }

   public static void info(Context context, String title, String message) {
      new AlertDialog.Builder(context)
         .setIcon(android.R.drawable.ic_dialog_info)
         .setTitle(title)
         .setMessage(message)
         .show();
   }

   public static void alert(Context context, String title, String message) {
      new AlertDialog.Builder(context)
         .setIcon(android.R.drawable.ic_dialog_alert)
         .setTitle(title)
         .setMessage(message)
         .show();
   }

   public static void toast(Context context, String message) {
      Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
   }
}
