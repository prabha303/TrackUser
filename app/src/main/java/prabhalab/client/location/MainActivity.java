package prabhalab.client.location;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class MainActivity extends AppCompatActivity implements UpdateInterService {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    //Button mGetQuoteLocation;
    TextView mLatitude;
    TextView mLongitude;
    TextView mTimestamp;
    TextView mAddress;
    public  static String batteryPercentage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            initializeUI();
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            }else
            {
                startLocationButtonClick();
            }

            Utility.getInstance().stayScreenOn(this);

            gettimeutc();

            this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));





        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();

                } else {
                    Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private void initializeUI() {
        try
        {
            mLatitude = findViewById(R.id.latitude_value);
            mLongitude = findViewById(R.id.longitude_value);
            mTimestamp = findViewById(R.id.timestamp_value);
            mAddress =  findViewById(R.id.address_value);

            //mGetQuoteLocation = findViewById(R.id.get_location);


            /*mGetQuoteLocation.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getLocation();
                }
            });*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getLocation() {
        try
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            } else {
                Log.d(TAG, "getLocation: permissions granted");
            }

            Intent service = new Intent(getApplicationContext(), LocationService.class);
            startService(service);
            LocationService locationService = new LocationService(MainActivity.this,this);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void startLocationButtonClick() {
        try
        {
            // Requesting ACCESS_FINE_LOCATION using Dexter library
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            getLocation();
                        }
                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                // open device settings when the permission is
                                // denied permanently
                                openSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
     }
    private void openSettings()
    {
        try
        {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void doUpdateLocation(Location location, String address) {
        try
        {

            long  timeMillis = System.currentTimeMillis();
            Date curDateTime = new Date(timeMillis);
            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final String dateTime = sdf.format(curDateTime);

            mAddress.setText(address);
            mLatitude.setText(""+location.getLatitude());
            mLongitude.setText(""+location.getLongitude());
            mTimestamp.setText(dateTime);


            mTimestamp.setText(Connectivity.isConnectedFast(MainActivity.this));



        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryPercentage = String.valueOf(level) + "%";
        }
    };




    private void gettimeutc() {

        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, RestartServiceBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+30*1000, pendingIntent);

    }

}
