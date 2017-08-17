package org.gemini.init;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import java.lang.reflect.Method;

public class Receiver extends BroadcastReceiver
{
    public static class Status
    {
        public static boolean wifiIsOn()
        {
            return wifiIsOn;
        }

        public static boolean wifiIsConnected()
        {
            return wifiIsConnected;
        }

        public static int signalStrength()
        {
            return signalStrength;
        }

        public static boolean screenIsOn()
        {
            return screenIsOn;
        }

        public static boolean userIsPresenting()
        {
            return userIsPresenting;
        }

        public static String ssid()
        {
            return ssid;
        }

        public static String lastSsid()
        {
            return lastSsid;
        }

        public static String lastActiveSsid()
        {
            return lastActiveSsid;
        }

        public static String carrier()
        {
            return carrier;
        }

        public static int preferredNetworkType()
        {
            return preferredNetworkType;
        }

        protected static boolean wifiIsOn = false;
        protected static boolean wifiIsConnected = false;
        protected static int signalStrength = 0;
        protected static boolean screenIsOn = true;
        protected static boolean userIsPresenting = true;
        protected static String ssid = "";
        protected static String lastSsid = "";
        protected static String lastActiveSsid = "";
        protected static String carrier = "";
        protected static int preferredNetworkType = 0;

        private static class Settable extends Status
        {
            public static void setWifiIsOn(boolean v)
            {
                wifiIsOn = v;
            }

            public static void setWifiIsConnected(boolean v)
            {
                wifiIsConnected = v;
            }

            public static void setSignalStrength(int v)
            {
                signalStrength = v;
            }

            public static void setScreenIsOn(boolean v)
            {
                screenIsOn = v;
            }

            public static void setUserIsPresenting(boolean v)
            {
                userIsPresenting = v;
            }

            public static void setSsid(String v)
            {
                if (ssid != v)
                {
                    if (ssid.length() > 0)
                    {
                        lastActiveSsid = ssid;
                    }
                    lastSsid = ssid;
                    ssid = v;
                }
            }

            public static void setCarrier(String v)
            {
                carrier = v;
            }

            public static void setPreferredNetworkType(int v)
            {
                preferredNetworkType = v;
            }
        }
    }

    public static final String WIFI_ON = "org.gemini.init.intent.WIFI_ON";
    public static final String WIFI_OFF = "org.gemini.init.initent.WIFI_OFF";
    public static final String WIFI_CONN = "org.gemini.init.intent.WIFI_CONN";
    public static final String WIFI_DISCONN =
        "org.gemini.init.intent.WIFI_DISCONN";
    public static final String SIGNAL_STRENGTHS =
        "org.gemini.init.intent.SIGNAL_STRENGTHS";
    public static final String SIGNAL_STRENGTHS_EXTRA = "LEVEL";
    private static final Receiver instance = new Receiver();
    private static PhoneStateListener phoneStateListener;
    private Logger logger;

    private static void broadcastSignalStrength(final Context context,
                                                final int level) {
        retrieveCarrier(context);
        retrievePreferredNetworkType(context);
        instance.writeLine(context, ">>>> Received signal level " + level);
        if (Status.signalStrength() != level) {
            Status.Settable.setSignalStrength(level);
            Intent intent = new Intent(SIGNAL_STRENGTHS,
                                       Uri.EMPTY,
                                       context,
                                       ExecService.class);
            intent.putExtra(SIGNAL_STRENGTHS_EXTRA, level);
            context.startService(intent);
        }
    }

    private static int asuToLevel(int asu) {
        return (asu <= 2  ? 0 :
               (asu <= 4  ? 1 :
               (asu <= 7  ? 2 :
               (asu <= 11 ? 3 :
               (asu <= 31 ? 4 : 0)
               ))));
    }

    private static int dbmToAsu(int dbm) {
        return (int)Math.floor((dbm + 113) / 2);
    }

    private static int dbmToLevel(int dbm) {
        return asuToLevel(dbmToAsu(dbm));
    }

    @SuppressWarnings("deprecation")
    @TargetApi(7)
    private static void registerTelephony(final Context context) {
        retrieveCarrier(context);
        retrievePreferredNetworkType(context);
        TelephonyManager manager = (TelephonyManager)
            context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(
                SignalStrength signalStrength) {
                if (signalStrength == null) return;
                instance.writeLine(context,
                                   ">>>> Received signal strength " +
                                   signalStrength.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    broadcastSignalStrength(context, signalStrength.getLevel());
                /*
                } else if (...) {
                    if (signalStrength.isGsm()) {
                        if (signalStrength.getLteLevel() != 0) {
                            broadcastSignalStrength(
                                context, signalStrength.getLteLevel());
                        } else {
                            broadcastSignalStrength(
                                context, signalStrength.getGsmLevel());
                        }
                    } else {
                        if (signalStrength.getCdmaLevel() != 0) {
                            broadcastSignalStrength(
                                context, signalStrength.getCdmaLevel());
                        } else {
                            broadcastSignalStrength(
                                context, signalStrength.getEvdoLevel());
                        }
                    }
                */
                } else {
                    if (signalStrength.isGsm()) {
                        broadcastSignalStrength(context, asuToLevel(
                            signalStrength.getGsmSignalStrength()));
                        /*
                        if (signalStrength.getLteSignalStrength() != 0) {
                            broadcastSignalStrength(context, asuToLevel(
                                signalStrength.getLteSignalStrength()));
                        } else {
                            broadcastSignalStrength(context, asuToLevel(
                                signalStrength.getGsmSignalStrength()));
                        }
                        */
                    } else {
                        if (signalStrength.getCdmaDbm() < 0) {
                            broadcastSignalStrength(context, dbmToLevel(
                                signalStrength.getCdmaDbm()));
                        } else {
                            broadcastSignalStrength(context, dbmToLevel(
                                signalStrength.getEvdoDbm()));
                        }
                    }
                }
                super.onSignalStrengthsChanged(signalStrength);
            }

            @Override
            public void onSignalStrengthChanged(int asu) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR_MR1) {
                    instance.writeLine(context,
                                       ">>>> Received signal strength " + asu);
                    broadcastSignalStrength(context, asuToLevel(asu));
                }
                super.onSignalStrengthChanged(asu);
            }
        };
        manager.listen(phoneStateListener,
                       PhoneStateListener.LISTEN_SIGNAL_STRENGTH |
                       PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private static void registerScreen(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(instance, filter);
    }

    public static void register(Context context)
    {
        registerScreen(context);
        registerTelephony(context);
        retrieveWifiStatus(context);
    }

    public static void unregister(Context context)
    {
        try
        {
            context.unregisterReceiver(instance);
        }
        catch (Exception ex) {}
        if (phoneStateListener != null)
        {
            TelephonyManager manager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
            manager.listen(phoneStateListener,
                           PhoneStateListener.LISTEN_NONE);
        }
    }

    private static void retrieveCarrier(final Context context)
    {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(
            Context.TELEPHONY_SERVICE);
        Status.Settable.setCarrier(manager.getNetworkOperatorName());
    }

    private static int getSubId(final Context context)
    {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(
            Context.TELEPHONY_SERVICE);
        Method method = null;
        try
        {
            method = TelephonyManager.class.getDeclaredMethod("getSubId");
            return (Integer)method.invoke(manager);
        }
        catch (Exception ex)
        {
            instance.writeLine(context,
                               "Failed to get getSubId: " +
                               ex.toString());
            return Integer.MAX_VALUE;
        }
    }

    private static boolean retrievePreferredNetworkType1(final Context context)
    {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(
            Context.TELEPHONY_SERVICE);
        Method method = null;
        try
        {
            method = TelephonyManager.class.getMethod(
                "getPreferredNetworkType", int.class);
        }
        catch (Exception ex)
        {
            instance.writeLine(context,
                               "Failed to get getPreferredNetworkType 1: " +
                               ex.toString());
            return false;
        }
        if (method == null) return false;
        method.setAccessible(true);

        try
        {
            Status.Settable.setPreferredNetworkType(
                (Integer)method.invoke(manager, getSubId(context)));
        }
        catch (java.lang.reflect.InvocationTargetException ex)
        {
            instance.writeLine(context,
                               "Failed to retrieve preferred network type 1: " +
                               ex.toString() +
                               ", target ex " +
                               ex.getTargetException().toString());
            return false;
        }
        catch (Exception ex)
        {
            instance.writeLine(context,
                               "Failed to retrieve preferred network type 1: " +
                               ex.toString());
            return false;
        }
        return true;
    }

    private static boolean retrievePreferredNetworkType2(final Context context)
    {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(
            Context.TELEPHONY_SERVICE);
        Method method = null;
        try
        {
            method = TelephonyManager.class.getMethod(
                "getPreferredNetworkType");
        }
        catch (Exception ex)
        {
            instance.writeLine(context,
                               "Failed to get getPreferredNetworkType 2: " +
                               ex.toString());
            return false;
        }
        if (method == null) return false;
        method.setAccessible(true);

        try
        {
            Status.Settable.setPreferredNetworkType(
                (Integer)method.invoke(manager));
        }
        catch (Exception ex)
        {
            instance.writeLine(context,
                               "Failed to retrieve preferred network type 2: " +
                               ex.toString());
            return false;
        }
        return true;
    }

    private static void retrievePreferredNetworkType(final Context context)
    {
        if (!retrievePreferredNetworkType1(context) &&
            !retrievePreferredNetworkType2(context))
        {
            Status.Settable.setPreferredNetworkType(-1);
        }
    }

    private static void retrieveWifiStatus(final Context context)
    {
        WifiManager wifi = (WifiManager)
            context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled())
        {
            Status.Settable.setWifiIsOn(true);
            context.startService(new Intent(WIFI_ON,
                                            Uri.EMPTY,
                                            context,
                                            ExecService.class));
        }
        else
        {
            Status.Settable.setWifiIsOn(false);
            context.startService(new Intent(WIFI_OFF,
                                            Uri.EMPTY,
                                            context,
                                            ExecService.class));
        }
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        if (wifiInfo != null)
        {
            Status.Settable.setSsid(wifiInfo.getSSID());
        }
        else
        {
            Status.Settable.setSsid("");
        }
        ConnectivityManager conMan = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null &&
            netInfo.getType() == ConnectivityManager.TYPE_WIFI)
        {
            Status.Settable.setWifiIsConnected(true);
            context.startService(new Intent(WIFI_CONN,
                                            Uri.EMPTY,
                                            context,
                                            ExecService.class));
        }
        else
        {
            Status.Settable.setWifiIsConnected(false);
            context.startService(new Intent(WIFI_DISCONN,
                                            Uri.EMPTY,
                                            context,
                                            ExecService.class));
        }
    }

    synchronized private void writeLine(final Context context, final String msg)
    {
        if (logger == null) logger = new Logger(context, "receiver.log");
        logger.writeLine(msg);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null) return;

        if (instance != this)
        {
            instance.onReceive(context, intent);
            return;
        }

        writeLine(context, ">>>> Received action " + intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
            Intent.ACTION_SCREEN_ON.equals(intent.getAction()) ||
            Intent.ACTION_SCREEN_OFF.equals(intent.getAction()) ||
            Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()))
            {
                Status.Settable.setScreenIsOn(true);
            }
            else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()))
            {
                Status.Settable.setScreenIsOn(false);
                Status.Settable.setUserIsPresenting(false);
            }
            else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
            {
                Status.Settable.setUserIsPresenting(true);
            }
            context.startService(new Intent(intent.getAction(),
                                            Uri.EMPTY,
                                            context,
                                            ExecService.class));
        }
        else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(
                     intent.getAction()))
        {
            retrieveWifiStatus(context);
        }
    }
}
