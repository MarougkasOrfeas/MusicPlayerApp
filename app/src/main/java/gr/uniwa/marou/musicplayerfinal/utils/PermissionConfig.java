package gr.uniwa.marou.musicplayerfinal.utils;

import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import gr.uniwa.marou.musicplayerfinal.activities.MainActivity;

public class PermissionConfig {

    private static final int PERMISSION_REQUEST_CODE = 100;

    public static boolean checkPermission(MainActivity mainActivity){
        int result = ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(MainActivity mainActivity){
        if(ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(mainActivity, "READ PERMISSION IS REQUIRED, PLEASE ALLOW FROM SETTINGS", Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

}
