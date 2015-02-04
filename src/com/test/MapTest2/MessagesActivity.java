package com.test.MapTest2;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.test.MapTest2.location.PicLocation;

public class MessagesActivity extends Activity {

    final String TAG = "myLogs";
    PicLocation.DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        dbHelper = new PicLocation.DBHelper(this);

        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams linLayoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(linLayout, linLayoutParam);

        ViewGroup.LayoutParams lpView = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final Cursor c = db.query("coordinates", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            final int messageColIndex = c.getColumnIndex("message");

            do {
                final TextView tv = new TextView(this);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(c.getString(messageColIndex));
                tv.setTextSize(25);
                tv.setLayoutParams(lpView);
                linLayout.addView(tv);
            } while (c.moveToNext());
        } else
            Log.d(TAG, "0 rows");
        dbHelper.close();

    }



}
