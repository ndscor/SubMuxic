package com.gloxandro.submuxic.util.compat;

import android.content.Context;
import android.util.Log;

import androidx.mediarouter.media.MediaRouter;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;

import com.gloxandro.submuxic.service.ChromeCastController;
import com.gloxandro.submuxic.service.DownloadService;
import com.gloxandro.submuxic.service.RemoteController;
import com.gloxandro.submuxic.util.EnvironmentVariables;

import static com.gloxandro.submuxic.util.EnvironmentVariables.CAST_APPLICATION_ID;

public final class GoogleCompat {

    private static final String TAG = GoogleCompat.class.getSimpleName();

    public static boolean playServicesAvailable(Context context){
        int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if(result != ConnectionResult.SUCCESS){
            Log.w(TAG, "No play services, failed with result: " + result);
            return false;
        }
        return true;
    }

    public static void installProvider(Context context) throws Exception{
        ProviderInstaller.installIfNeeded(context);
    }

    public static boolean castAvailable() {
        if (CAST_APPLICATION_ID == null) {
            Log.w(TAG, "CAST_APPLICATION_ID not provided");
            return false;
        }
        try {
            Class.forName("com.google.android.gms.cast.CastDevice");
        } catch (Exception ex) {
            Log.w(TAG, "Chromecast library not available");
            return false;
        }
        return true;
    }

    public static RemoteController getController(DownloadService downloadService, MediaRouter.RouteInfo info) {
        CastDevice device = CastDevice.getFromBundle(info.getExtras());
        if(device != null) {
            return new ChromeCastController(downloadService, device);
        } else {
            return null;
        }
    }

    public static String getCastControlCategory() {
        return CastMediaControlIntent.categoryForCast(CAST_APPLICATION_ID);
    }
}