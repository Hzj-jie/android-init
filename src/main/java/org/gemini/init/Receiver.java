package org.gemini.init;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;

public class Receiver extends BroadcastReceiver
{
    public static final String WIFI_ON = "org.gemini.init.intent.WIFI_ON";
    public static final String WIFI_OFF = "org.gemini.init.initent.WIFI_OFF";
    public static final String WIFI_CONN = "org.gemini.init.intent.WIFI_CONN";
    public static final String WIFI_DISCONN =
        "org.gemini.init.intent.WIFI_DISCONN";
    private static final Receiver instance = new Receiver();
    private Logger logger;

    public static void register(Context context)
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(instance, filter);
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
