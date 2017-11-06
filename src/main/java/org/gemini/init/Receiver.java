package org.gemini.init;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import org.gemini.shared.Event;
import org.gemini.shared.TelephonyState;
import org.gemini.shared.PhonySignalStrengthListener;

public class Receiver extends BroadcastReceiver {
  private static class Settable extends Status {
    public static void setWifiIsOn(boolean v) {
      wifiIsOn = v;
    }

    public static void setWifiIsConnected(boolean v) {
      wifiIsConnected = v;
    }

    public static void setSignalStrength(int v) {
      signalStrength = v;
    }

    public static void setScreenIsOn(boolean v) {
      screenIsOn = v;
    }

    public static void setUserIsPresenting(boolean v) {
      userIsPresenting = v;
    }

    public static void setSsid(String v) {
      if (ssid != v) {
        if (ssid.length() > 0) {
          lastActiveSsid = ssid;
        }
        lastSsid = ssid;
        ssid = v;
      }
    }

    public static void setCarrier(String v) {
      carrier = v;
    }

    public static void setPreferredNetworkType(int v) {
      preferredNetworkType = v;
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
  private Logger logger;
  private TelephonyState telephonyState;
  private PhonySignalStrengthListener signalStrengthListener;

  synchronized private void initialize(final Context context) {
    if (logger == null) {
      logger = new Logger(context, "receiver.log");
    }
    if (telephonyState == null) {
      telephonyState = new TelephonyState(context);
    }
    if (signalStrengthListener == null) {
      signalStrengthListener = new PhonySignalStrengthListener(context);
      signalStrengthListener.onSignalStrength().add(
          new Event.ParameterRunnable<Integer>() {
            @Override
            public void run(Integer level) {
              broadcastSignalStrength(context, level);
            }
          });
    }
  }

  private static void registerScreen(Context context) {
    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    context.registerReceiver(instance, filter);
  }

  public static void register(Context context) {
    instance.initialize(context);
    registerScreen(context);

    // Initialize Settable.
    broadcastSignalStrength(context, PhonySignalStrengthListener.MIN_LEVEL - 1);
    retrieveWifiStatus(context);
  }

  public static void unregister(Context context) {
    try {
      context.unregisterReceiver(instance);
    }
    catch (Exception ex) {}
    instance.signalStrengthListener.stop();
  }

  private static void broadcastSignalStrength(Context context, int level) {
    Settable.setCarrier(instance.telephonyState.carrier());
    Settable.setPreferredNetworkType(
        instance.telephonyState.preferredNetworkType());
    if (level >= PhonySignalStrengthListener.MIN_LEVEL &&
        level <= PhonySignalStrengthListener.MAX_LEVEL) {
      instance.logger.writeLine(">>>> Received signal level " + level);
      if (Status.signalStrength() != level) {
        Settable.setSignalStrength(level);
        Intent intent = new Intent(SIGNAL_STRENGTHS,
                                   Uri.EMPTY,
                                   context,
                                   ExecService.class);
        intent.putExtra(SIGNAL_STRENGTHS_EXTRA, level);
        context.startService(intent);
      }
    }
  }

  private static void retrieveWifiStatus(Context context) {
    WifiManager wifi = (WifiManager)
      context.getSystemService(Context.WIFI_SERVICE);
    if (wifi.isWifiEnabled()) {
      Settable.setWifiIsOn(true);
      context.startService(new Intent(WIFI_ON,
                                      Uri.EMPTY,
                                      context,
                                      ExecService.class));
    }
    else {
      Settable.setWifiIsOn(false);
      context.startService(new Intent(WIFI_OFF,
                                      Uri.EMPTY,
                                      context,
                                      ExecService.class));
    }
    WifiInfo wifiInfo = wifi.getConnectionInfo();
    if (wifiInfo != null) {
      Settable.setSsid(wifiInfo.getSSID());
    }
    else {
      Settable.setSsid("");
    }
    ConnectivityManager conMan = (ConnectivityManager)
      context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = conMan.getActiveNetworkInfo();
    if (netInfo != null &&
        netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
      Settable.setWifiIsConnected(true);
      context.startService(new Intent(WIFI_CONN,
                                      Uri.EMPTY,
                                      context,
                                      ExecService.class));
    }
    else {
      Settable.setWifiIsConnected(false);
      context.startService(new Intent(WIFI_DISCONN,
                                      Uri.EMPTY,
                                      context,
                                      ExecService.class));
    }
  }

  @Override
  public void onReceive(Context context, Intent intent)
  {
    if (intent == null) return;

    if (instance != this) {
      instance.onReceive(context, intent);
      return;
    }

    initialize(context);

    logger.writeLine(">>>> Received action " + intent.getAction());
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
        Intent.ACTION_SCREEN_ON.equals(intent.getAction()) ||
        Intent.ACTION_SCREEN_OFF.equals(intent.getAction()) ||
        Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
      if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
        Settable.setScreenIsOn(true);
      }
      else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
        Settable.setScreenIsOn(false);
        Settable.setUserIsPresenting(false);
      }
      else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
        Settable.setUserIsPresenting(true);
      }
      context.startService(new Intent(intent.getAction(),
                                      Uri.EMPTY,
                                      context,
                                      ExecService.class));
    }
    else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(
                 intent.getAction())) {
      retrieveWifiStatus(context);
    }
  }
}
