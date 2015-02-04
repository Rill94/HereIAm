package com.test.MapTest2.exif;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.*;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.test.MapTest2.model.Picture;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Valerie on 17.07.2014.
 */
public class Exif {

    public Picture getpicturedata(String uriString) throws IOException {
        ExifInterface exif = new ExifInterface(uriString);
        if (exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) == null)
            return null;
        float log = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
        if(!exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF).equals("E"))	{
            log = -log;
        }
        float lat = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
        if(!exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF).equals("N"))	{
            lat = -lat;
        }

        Picture temp = new Picture(log, lat,exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP) ,exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP));

        return temp;
    }


    private Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

        result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

        return result;

    };

    public Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                sbmp.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }

    public String getPath(Uri uri, Context context) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getAdress(float lat, float lon, Context context)
    {
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        String result = "";

        try {
            List<Address> addressList = geo.getFromLocation(lat, lon, 1);
            result = addressList.get(0).getLocality() +", "
                     + addressList.get(0).getAdminArea() + ", "
                     + addressList.get(0).getCountryName()+ ", "
                     + addressList.get(0).getAddressLine(0);

        }
        catch (Exception ex)
        {
            Log.d("Info", ex.getMessage());
        }
        return result;

    }


}
