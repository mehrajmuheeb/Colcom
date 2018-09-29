package muheeb.com.colcom20;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by BISMA on 08-Feb-17.
 */
public class CheckConnection extends AppCompatActivity {



    private static final String TAG = CheckConnection.class.getSimpleName();
    //HELPER METHOD TO DETERMINE WHETHER NETWORK IS AVAILABLE OR NOT
    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}















