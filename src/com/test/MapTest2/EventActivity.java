package com.test.MapTest2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.test.MapTest2.location.PicLocation;

public class EventActivity extends Activity implements View.OnClickListener {

    final String TAG = "myLogs";
    EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        Intent intent = getIntent();
        double[] location = intent.getDoubleArrayExtra("location");
        Log.d(TAG, "location:"+location[0]+";"+location[1]);
        etMessage = (EditText) findViewById(R.id.editText);


    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        PicLocation picLocation = new PicLocation();
        double[] location = intent.getDoubleArrayExtra("location");
        double latitude = location[0];
        double longitude = location[1];
        String message = etMessage.getText().toString();
        picLocation.saveLocation(longitude, latitude, message, this.getApplicationContext());
        v.refreshDrawableState();
        finish();
    }

}
