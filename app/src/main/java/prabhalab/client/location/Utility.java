package prabhalab.client.location;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;

public class Utility {
    private static Utility utility = null;
    public static int forgroundservice_notification_id = 999;
    public static Utility getInstance()
    {
        if (utility != null)
        {
            return utility;
        }
        else
        {
            utility = new Utility();
            return utility;
        }
    }
    public static boolean isNotEmpty(String Value) {
        boolean flag =false;
        try {
            if(!TextUtils.isEmpty(Value)){
                Value = Value.replace("null", "");
            }else{
                Value = "";
            }
            if(!TextUtils.isEmpty(Value)){
                Value = Value.trim();
            }
            if(!TextUtils.isEmpty(Value)){
                flag =true;
            }else{
                flag =false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void stayScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public interface AppData {
        String hasLoggedIn = "has_logged_in";
        String user_id = "user_id";
        String password = "password";
    }

}
