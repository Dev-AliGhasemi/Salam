package ir.vira.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class for get some information about network like ip address or server ip address and etc .
 *
 * @author Ali Ghasemi
 */
public class NetworkInformation {
    private Context context;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;

    public NetworkInformation(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public String getIpAddress() {
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    public String getServerIpAddress() {
        return Formatter.formatIpAddress(wifiManager.getDhcpInfo().serverAddress);
    }

    public boolean isConnectedToNetwork() {
        if (getIpAddress().equals("0.0.0.0"))
            return false;
        else
            return true;
    }

    public boolean isMobileDataEnabled() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = connectivityManager.getClass().getDeclaredMethod("getMobileDataEnabled");
        method.setAccessible(true);
        return (boolean) method.invoke(connectivityManager);
    }

    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public boolean isWifiAccessPointEnabled() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
        method.setAccessible(true);
        return (boolean) method.invoke(wifiManager);
    }
}