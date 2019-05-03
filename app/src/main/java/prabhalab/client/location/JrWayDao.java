package prabhalab.client.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static prabhalab.client.location.DatabaseHelper.TABLE_NAME;

public class JrWayDao {



    public static void insertUserDetails(Context context, Location location, String address, String battaryPercentage,String  netWorkStrengh) {
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

                    contentValues.put(DatabaseHelper.network_strength,netWorkStrengh);
                    contentValues.put(DatabaseHelper.network_used,Connectivity.isConnectedFast(context));
                    contentValues.put(DatabaseHelper.address, address);
                    contentValues.put(DatabaseHelper.accuracy,location.getAccuracy());


                    long rs = db.insert(TABLE_NAME, null, contentValues);
                    Log.d(TABLE_NAME,""+rs);
                }
            }
            db.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void deleteLocalData(Context context, String  retrunData) {
        try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            if (retrunData != null)
            {

                long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
                Log.d("MainActivity", "TABLE_NAME row Count" + count);

                JSONArray retrunDataarray = new JSONArray(retrunData);
                if (retrunDataarray.length() > 0) {

                    for (int i = 0; i < retrunDataarray.length(); i++) {

                        String locationId = retrunDataarray.getJSONObject(i).getString("location_id");

                        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+DatabaseHelper.location_id+"='"+locationId+"'");

                    }
                }
                count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
                Log.d("MainActivity", "TABLE_NAME row Count_last" + count);

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
                String selectQuery = "SELECT * FROM "+TABLE_NAME+" LIMIT 5;" ;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            Random random = new Random();
                            String generatedPassword = String.format("%07d", random.nextInt(10000));
                            String uniqueNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.timeMillSec)) +""+generatedPassword;
                            JSONObject c_points = new JSONObject();;
                            c_points.put("timeMillSec", uniqueNumber);
                            c_points.put("location_id", cursor.getString(cursor.getColumnIndex(DatabaseHelper.location_id)));
                            c_points.put("latlng", cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng)));
                            c_points.put("modified_date", cursor.getString(cursor.getColumnIndex(DatabaseHelper.modified_date)));
                            c_points.put("update_date", cursor.getString(cursor.getColumnIndex(DatabaseHelper.update_date)));
                            c_points.put("speed", cursor.getString(cursor.getColumnIndex(DatabaseHelper.speed)));
                            c_points.put("user_id", cursor.getString(cursor.getColumnIndex(DatabaseHelper.user_id)));
                            String  battaryPer = cursor.getString(cursor.getColumnIndex(DatabaseHelper.battary_percentage));
                            if(Utility.isNotEmpty(battaryPer))
                            {
                                battaryPer = battaryPer.replace("%", "");
                                battaryPer = battaryPer.replace("!", "");
                            }
                            c_points.put("battary_percentage", battaryPer);
                            c_points.put("network_strength", cursor.getString(cursor.getColumnIndex(DatabaseHelper.network_strength)));
                            c_points.put("network_used", cursor.getString(cursor.getColumnIndex(DatabaseHelper.network_used)));
                            c_points.put("heading", cursor.getString(cursor.getColumnIndex(DatabaseHelper.accuracy)));
                            c_points.put("address", cursor.getString(cursor.getColumnIndex(DatabaseHelper.address)));

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
