package prabhalab.client.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JrWayDao {



    public static void insertUserDetails(Context context, Location location, String address, String battaryPercentage) {
        try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            if (location != null)
            {
                String user_id = SharedPref.getStringValue(context,Utility.AppData.user_id);
                if(Utility.isNotEmpty(user_id))
                {
                    long  timeMillis = System.currentTimeMillis();
                    Date curDateTime = new Date(timeMillis);
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String dateTime = sdf.format(curDateTime);
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DatabaseHelper.location_id,timeMillis);
                    contentValues.put(DatabaseHelper.latlng,location.getLatitude() +","+location.getLongitude());
                    contentValues.put(DatabaseHelper.address,address);
                    contentValues.put(DatabaseHelper.modified_date,dateTime);
                    contentValues.put(DatabaseHelper.update_date,dateTime);
                    contentValues.put(DatabaseHelper.timeMillSec,""+timeMillis);
                    contentValues.put(DatabaseHelper.speed,""+location.getSpeed());
                    contentValues.put(DatabaseHelper.user_id,user_id);
                    contentValues.put(DatabaseHelper.battary_percentage,battaryPercentage);


                    long rs = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                    Log.d(DatabaseHelper.TABLE_NAME,""+rs);
                }
            }
            db.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public static JSONObject getSavedValues(Context context) {
        JSONObject objMainList = new JSONObject();
        JSONArray allPoints = new JSONArray();
        try
        {
            if (context != null) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                String selectQuery = "SELECT * FROM "+ DatabaseHelper.TABLE_NAME ;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            JSONObject c_points = new JSONObject();;
                            c_points.put("location_id", cursor.getString(cursor.getColumnIndex(DatabaseHelper.location_id)));
                            c_points.put("latlng", cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng)));
                            c_points.put("modified_date", cursor.getString(cursor.getColumnIndex(DatabaseHelper.modified_date)));
                            c_points.put("update_date", cursor.getString(cursor.getColumnIndex(DatabaseHelper.update_date)));
                            c_points.put("timeMillSec", cursor.getString(cursor.getColumnIndex(DatabaseHelper.timeMillSec)));
                            c_points.put("speed", cursor.getString(cursor.getColumnIndex(DatabaseHelper.speed)));
                            c_points.put("user_id", cursor.getString(cursor.getColumnIndex(DatabaseHelper.user_id)));
                            c_points.put("battary_percentage", cursor.getString(cursor.getColumnIndex(DatabaseHelper.battary_percentage)));
                            allPoints.put(c_points);
                        }  while (cursor.moveToNext());
                    }
                }
            }
            objMainList.put("driverWaypoints", allPoints);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return objMainList;
    }

}
