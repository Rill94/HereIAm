package com.test.MapTest2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.test.MapTest2.exif.Exif;
import com.test.MapTest2.location.PicLocation;
import com.test.MapTest2.model.Picture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener
{
    String[] data_list = null;
    SupportMapFragment mapFragment;
    GoogleMap map;
    Bitmap bmp;
    PicLocation.DBHelper dbHelper;
    final String TAG = "myLogs";
    private LocationManager locationManager;
    private static Date date;
    public int check;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        dbHelper = new PicLocation.DBHelper(this);
        date = new Date(System.currentTimeMillis());
        check = 0;

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();
        if (map == null) {
            finish();
            return;
        }
        this.updateMarkers();
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);
        init();
    }

    private void init() {
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        data_list = new String[] { "Add picture"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, data_list);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mTitle = getResources().getString(R.string.app_name);
        mDrawerTitle = getResources().getString(R.string.drawer_open);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                double[] coord = new double[2];
                coord[0] = latLng.latitude;
                coord[1] = latLng.longitude;
                intent.putExtra("location",coord);
                startActivity(intent);
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d(TAG, "onCameraChange: " + camera.target.latitude + "," + camera.target.longitude);
            }
        });

        actionbarToggleHandler();
    }

    @SuppressLint("NewApi")
    private void actionbarToggleHandler() {
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle(mTitle);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView adapter, View view, int position,
                                long id) {
            Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Uri imageFileUri = intent.getData();
            Exif exif = new Exif();
            Picture picture;
            try {
                Log.d("Info", exif.getPath(imageFileUri, this.getApplicationContext()));
                picture = exif.getpicturedata(exif.getPath(imageFileUri, this.getApplicationContext()));
                if (picture == null)
                    {
                        picture = this.getCurrentLocation();
                    }
                Log.d("PICTUREDATA",picture.getLat()+" "+picture.getLog());
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                bmpFactoryOptions.inSampleSize = 2;
                bmpFactoryOptions.inJustDecodeBounds = false;

                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        imageFileUri), null, bmpFactoryOptions);
                Bitmap bitmap = exif.getCroppedBitmap(bmp,130);
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(picture.getLat(), picture.getLog()))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                Intent event = new Intent(MainActivity.this, EventActivity.class);
                double[] coord = new double[2];
                coord[0] = picture.getLat();
                coord[1] = picture.getLog();
                event.putExtra("location",coord);
                startActivity(event);

                Log.d("ADDRESS", exif.getAdress(picture.getLat(), picture.getLog(), this.getApplicationContext()));
            } catch (FileNotFoundException e) {
                Log.v("ERROR", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void onClickTest(View view) {
    }

    public void onClickMessages(View view)
    {
        Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 1, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 1,
                locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private Location showLocation(Location location) {

        this.updateMarkers();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("coordinates", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int longitudeColIndex = c.getColumnIndex("longitude");
            int latitudeColIndex = c.getColumnIndex("latitude");

            do {
                if (!(((location.getLongitude()<=(c.getDouble(longitudeColIndex)-0.0005))|(location.getLongitude()>= (c.getDouble(longitudeColIndex)+0.0005)))
                    &(location.getLatitude()<=(c.getDouble(latitudeColIndex)-0.0005))|(location.getLatitude()>=(c.getDouble(latitudeColIndex)+0.0005))))
                {
                    if (((date.getTime() - new Date(System.currentTimeMillis()).getTime())>=600000)|(check==0)) {
                        date = new Date(System.currentTimeMillis());
                    }
                }
            } while (c.moveToNext());
        } else
            Log.d(TAG, "0 rows");
        check = 1;
        c.close();
        dbHelper.close();
        if (location == null) {
            Log.d(TAG, "location null");
            return null;
        }
        else
        {
            Log.d(TAG, "onCameraChange: " + location.getLatitude() + "," + location.getLongitude());
            return location;
        }

    }

    private Picture getCurrentLocation() {
        Location location;
        Picture picture = new Picture();
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Фотография будет привязана к текущему местоположению");
            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(viewIntent);
        } else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0, locationListener);
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        picture.setLat((float) location.getLatitude());
                        picture.setLog((float) location.getLongitude());
                    }
                }
            } else
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0, locationListener);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            picture.setLat((float) location.getLatitude());
                            picture.setLog((float) location.getLongitude());
                        }
                    }
                }
        }
        return picture;
    }

    private void updateMarkers()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("coordinates", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int longitudeColIndex = c.getColumnIndex("longitude");
            int latitudeColIndex = c.getColumnIndex("latitude");
            int messageColIndex = c.getColumnIndex("message");

            do {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(c.getDouble(latitudeColIndex), c.getDouble(longitudeColIndex)))
                        .title(c.getString(messageColIndex)));

            } while (c.moveToNext());
        } else
            Log.d(TAG, "0 rows");
        c.close();
        dbHelper.close();

    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        return false;
    }

}
