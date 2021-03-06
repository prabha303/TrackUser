package prabhalab.client.location;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import mehdi.sakout.fancybuttons.FancyButton;

import static prabhalab.client.location.Utility.AppData.hasLoggedIn;


/**
 * Created by PrabhagaranR on 22-03-19.
 */

public class Login extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = Login.class.getSimpleName();
    FancyButton login;
    TextView mLatitude,mLongitude,mTimestamp,status,mAddress,lastTripKM;
    EditText password,userId;
    String fcmToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);



        login = findViewById(R.id.login);
        userId = findViewById(R.id.userId);
        password = findViewById(R.id.password);

        //login.setText(getResources().getString(R.string.fa_arrow_circle_right) + " Login");

        showSplashImage();

    }

    private void showSplashImage() {
        try {
            final LinearLayout splashLayout = findViewById(R.id.splashLayout);
            final LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
            splashLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 3 seconds
                    splashLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    initializeUI();
                }
            }, 2000);

        }catch (Exception e)
        {
            initializeUI();
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        try
        {

            LinearLayout splashLayout = findViewById(R.id.splashLayout);
            LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
            splashLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            try
            {
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
                }else
                {

                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(validate(userId.getText().toString(),password.getText().toString()))
                    {
                        openDriverPage(userId.getText().toString(),password.getText().toString());
                    }else
                    {
                        Toast.makeText(Login.this, "UserId, password should not empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if(SharedPref.getBooleanValue(this, Utility.AppData.hasLoggedIn))
            {
                String user_id = SharedPref.getStringValue(this, Utility.AppData.user_id);
                String pwd = SharedPref.getStringValue(this, Utility.AppData.password);

                 if(Utility.isNotEmpty(user_id) && Utility.isNotEmpty(pwd))
                 {
                     userId.setText(user_id);
                     password.setText(pwd);
                     openDriverPage(user_id,pwd);
                 }
            }


            if(SharedPref.getBooleanValue(this, hasLoggedIn))
            {
                String user_id = SharedPref.getStringValue(this,Utility.AppData.user_id);
                String pwd = SharedPref.getStringValue(this,Utility.AppData.password);

                if(Utility.isNotEmpty(user_id) && Utility.isNotEmpty(pwd))
                {
                    userId.setText(user_id);
                    password.setText(pwd);
                    openDriverPage(user_id,pwd);
                }
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private boolean  validate(String usr, String pwd)
    {

        if (Utility.isNotEmpty(usr) && Utility.isNotEmpty(pwd))
        {
            usr = usr.trim();
            pwd = pwd.trim();
        }

        if(Utility.isNotEmpty(usr) && Utility.isNotEmpty(pwd))
        {
            return true;
        }

        return false;
    }

    private void openDriverPage(String user, String pwd)
    {
        if(Utility.isNotEmpty(pwd) && pwd.equals("lynk@123"))
        {

            SharedPref.getInstance().setSharedValue(Login.this, hasLoggedIn, true);
            SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.user_id, user);
            SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.password, pwd);

            gettimeutc();
            createNotificationChannel();



            Intent service = new Intent(Login.this, LocationService.class);
            startService(service);


            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
            finish();


        }else
        {
            Toast.makeText(this,"invalid credentials",Toast.LENGTH_SHORT).show();
        }
    }



    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.CHANNEL_ID);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void gettimeutc(){

        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Login.this, RestartServiceBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+30*1000, pendingIntent);

    }









}
