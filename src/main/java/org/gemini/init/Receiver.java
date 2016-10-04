package org.gemini.init;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class Receiver extends BroadcastReceiver
{
    public static final String WIFI_ON = "org.gemini.init.intent.WIFI_ON";
    public static final String WIFI_OFF = "org.gemini.init.initent.WIFI_OFF";
    public static final String WIFI_CONN = "org.gemini.init.intent.WIFI_CONN";
    public static final String WIFI_DISCONN =
        "org.gemini.init.intent.WIFI_DISCONN";
    public static final String SIGNAL_STRENGTHS =
        "org.gemini.init.intent.SIGNAL_STRENGTHS";
    public static final String SIGNAL_STRENGTHS_EXTRA = "LEVEL";
    private static final Receiver instance = new Receiver();
    private static int lastLevel = -1;
    private Logger logger;

    private static void broadcastSignalStrength(Context context, int level) {
        instance.writeLine(context, ">>>> Received signal level " + level);
        if (lastLevel != level) {
            lastLevel = level;
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
        TelephonyManager manager = (TelephonyManager)
            context.getSystemService(Context.TELEPHONY_SERVICE);
        manager.listen(new PhoneStateListener() {
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
                    if ((asu >= 0 && asu <= 31) || asu == 99) {
                        broadcastSignalStrength(context, asuToLevel(asu));
                    }
                }
                super.onSignalStrengthChanged(asu);
            }
        },
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
    }

    public static void unregister(Context context)
    {
        try
        {
            context.unregisterReceiver(instance);
        }
        catch (Exception ex) {}
    }

    synchronized private void writeLine(Context context, String msg)
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
            context.startService(new Intent(intent.getAction(),
                                            Uri.EMPTY,
                                            context,
                                            ExecService.class));
        }
        else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(
                     intent.getAction()))
        {
            WifiManager wifi = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()==true)
            {
                context.startService(new Intent(WIFI_ON,
                                                Uri.EMPTY,
                                                context,
                                                ExecService.class));
            }
            else
            {
                context.startService(new Intent(WIFI_OFF,
                                                Uri.EMPTY,
                                                context,
                                                ExecService.class));
            }
            ConnectivityManager conMan = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if (netInfo != null &&
                netInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                context.startService(new Intent(WIFI_CONN,
                                                Uri.EMPTY,
                                                context,
                                                ExecService.class));
            }
            else
            {
                context.startService(new Intent(WIFI_DISCONN,
                                                Uri.EMPTY,
                                                context,
                                                ExecService.class));
            }
        }
    }
}
