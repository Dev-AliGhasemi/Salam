package ir.vira.network;

import android.content.Context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * This class for get some information about internet connection .
 *
 * @author Ali Ghasemi
 */
public class InternetInformation {

    public static boolean isConnectedToInternet(Context context) throws IOException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        NetworkInformation networkInformation = new NetworkInformation(context);
        if (networkInformation.isWifiEnabled() || networkInformation.isMobileDataEnabled()) {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("ping -c 1 8.8.8.8");
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return true;
            } else {
                return false;
            }
        } else
            return false;
    }
}
