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
import org.gemini.shared.Objects;
import org.gemini.shared.TelephonyState;
import org.gemini.shared.PhonySignalStrengthListener;

public class Receiver extends BroadcastReceiver {
  private static class Settable extends Status {
    public static boolean setWifiIsOn(boolean v) {
      if (wifiIsOn == v) return false;
      wifiIsOn = v;
      return true;
    }

    public static boolean setWifiIsConnected(boolean v) {
      if (wifiIsConnected == v) return false;
      wifiIsConnected = v;
      return true;
    }

    public static boolean setSignalStrength(int v) {
      if (signalStrength == v) return false;
      signalStrength = v;
      return true;
    }

    public static boolean setScreenIsOn(boolean v) {
      if (screenIsOn == v) return false;
      screenIsOn = v;
      return true;
    }

    public static boolean setUserIsPresenting(boolean v) {
      if (userIsPresenting == v) return false;
      userIsPresenting = v;
      return true;
    }

    public static boolean setSsid(String v) {
      if (Objects.equals(ssid, v)) return false;
      if (ssid.length() > 0) {
        lastActiveSsid = ssid;
      }
      lastSsid = ssid;
      ssid = v;
      return true;
    }

    public static boolean setCarrier(String v) {
      if (Objects.equals(carrier, v)) return false;
      carrier = v;
      return true;
    }

    public static boolean setPreferredNetworkType(int v) {
      if (preferredNetworkType == v) return false;
      preferredNetworkType = v;
      return true;
    }

    public static boolean setMobileDataIsConnected(boolean v) {
      if (mobileDataIsConnected == v) return false;
      mobileDataIsConnected = v;
      return true;
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
  public static final String MOBILE_DATA_CONN =
      "org.gemini.init.intent.MOBILE_DATA_CONN";
  public static final String MOBILE_DATA_DISCONN =
      "org.gemini.init.intent.MOBILE_DATA_DISCONN";
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
    context = context.getApplicationContext();
    instance.initialize(context);
    registerScreen(context);

    // Initialize Settable.
    broadcastSignalStrength(context, PhonySignalStrengthListener.MIN_LEVEL - 1);
    retrieveConnectionStatus(context);
  }

  public static void unregister(Context context) {
    context = context.getApplicationContext();
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
      if (Settable.setSignalStrength(level)) {
        Intent intent = new Intent(SIGNAL_STRENGTHS,
                                   Uri.EMPTY,
                                   context,
                                   ExecService.class);
        intent.putExtra(SIGNAL_STRENGTHS_EXTRA, level);
        context.startService(intent);
      }
    }
  }

  private static void retrieveConnectionStatus(Context context) {
    WifiManager wifi = (WifiManager)
      context.getSystemService(Context.WIFI_SERVICE);
    if (wifi.isWifiEnabled()) {
      if (Settable.setWifiIsOn(true)) {
        context.startService(new Intent(WIFI_ON,
                                        Uri.EMPTY,
                                        context,
                                        ExecService.class));
      }
    }
    else {
      if (Settable.setWifiIsOn(false)) {
        context.startService(new Intent(WIFI_OFF,
                                        Uri.EMPTY,
                                        context,
                                        ExecService.class));
      }
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
      if (Settable.setWifiIsConnected(true)) {
        context.startService(new Intent(WIFI_CONN,
                                        Uri.EMPTY,
                                        context,
                                        ExecService.class));
      }
    }
    else {
      if (Settable.setWifiIsConnected(false)) {
        context.startService(new Intent(WIFI_DISCONN,
                                        Uri.EMPTY,
                                        context,
                                        ExecService.class));
      }
    }
    if (netInfo != null &&
        netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
      if (Settable.setMobileDataIsConnected(true)) {
        context.startService(new Intent(MOBILE_DATA_CONN,
                                        Uri.EMPTY,
                                        context,
                                        ExecService.class));
      }
    }
    else {
      if (Settable.setMobileDataIsConnected(false)) {
        context.startService(new Intent(MOBILE_DATA_DISCONN,
                                        Uri.EMPTY,
                                        context,
                                        ExecService.class));
      }
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
    else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(
                 intent.getAction()) ||
             WifiManager.WIFI_STATE_CHANGED_ACTION.equals(
                 intent.getAction()) ||
             ConnectivityManager.CONNECTIVITY_ACTION.equals(
                 intent.getAction())) {
      // TODO: Split retrieveConnectionStatus() into retrieveWifiStatus() and
      // retrieveMobileDataStatus(): retrieving wifi status in
      // CONNECTIVITY_ACTION is not necessary.
      retrieveConnectionStatus(context);
    }
  }
}
