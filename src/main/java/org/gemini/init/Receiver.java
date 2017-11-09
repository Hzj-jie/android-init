package org.gemini.init;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.gemini.shared.Event;
import org.gemini.shared.NetworkListener;
import org.gemini.shared.Objects;
import org.gemini.shared.PhonySignalStrengthListener;
import org.gemini.shared.Preconditions;
import org.gemini.shared.ScreenListener;
import org.gemini.shared.TelephonyState;

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
  private ScreenListener screenListener;
  private NetworkListener networkListener;

  synchronized private void initialize(final Context context) {
    if (logger != null) return;

    Preconditions.isNull(logger);
    logger = new Logger(context, "receiver.log");

    Preconditions.isNull(telephonyState);
    telephonyState = new TelephonyState(context);

    Preconditions.isNull(signalStrengthListener);
    signalStrengthListener = new PhonySignalStrengthListener(context);
    signalStrengthListener.onSignalStrength().add(
        new Event.ParameterRunnable<Integer>() {
          @Override
          public void run(Integer level) {
            broadcastSignalStrength(context, level);
          }
        });

    Preconditions.isNull(screenListener);
    screenListener = new ScreenListener(context);
    screenListener.onScreenOn().add(
        new Event.ParameterRunnable<Void>() {
          @Override
          public void run(Void nothing) {
            Settable.setScreenIsOn(true);
            startService(context, Intent.ACTION_SCREEN_ON);
          }
        });
    screenListener.onScreenOff().add(
        new Event.ParameterRunnable<Void>() {
          @Override
          public void run(Void nothing) {
            Settable.setScreenIsOn(false);
            Settable.setUserIsPresenting(false);
            startService(context, Intent.ACTION_SCREEN_OFF);
          }
        });
    screenListener.onUserPresent().add(
        new Event.ParameterRunnable<Void>() {
          @Override
          public void run(Void nothing) {
            Settable.setUserIsPresenting(true);
            startService(context, Intent.ACTION_USER_PRESENT);
          }
        });

    Preconditions.isNull(networkListener);
    networkListener = new NetworkListener(context);
    networkListener.onStateChanged().add(
        new Event.ParameterRunnable<NetworkListener.State>() {
          @Override
          public void run(NetworkListener.State state) {
            Preconditions.isNotNull(state);
            if (Settable.setWifiIsOn(state.wifiIsOn())) {
              startService(context, state.wifiIsOn() ? WIFI_ON : WIFI_OFF);
            }

            Settable.setSsid(state.ssid());

            if (Settable.setWifiIsConnected(state.wifiIsConnected())) {
              startService(
                  context, state.wifiIsConnected() ? WIFI_CONN : WIFI_DISCONN);
            }

            if (Settable.setMobileDataIsConnected(
                    state.mobileDataIsConnected())) {
              startService(context,
                           state.mobileDataIsConnected() ?
                               MOBILE_DATA_CONN :
                               MOBILE_DATA_DISCONN);
            }
          }
        });
  }

  public static void register(Context context) {
    context = context.getApplicationContext();
    instance.initialize(context);

    // Initialize Settable.
    broadcastSignalStrength(context, PhonySignalStrengthListener.MIN_LEVEL - 1);
  }

  public static void unregister(Context context) {
    context = context.getApplicationContext();
    try {
      context.unregisterReceiver(instance);
    }
    catch (Exception ex) {}
    instance.signalStrengthListener.stop();
    instance.screenListener.stop();
  }

  private static void startService(Context context, String action) {
    Intent intent = new Intent(action,
                               Uri.EMPTY,
                               context,
                               ExecService.class);
    context.startService(intent);
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

  @Override
  public void onReceive(Context context, Intent intent)
  {
    if (intent == null) return;

    if (instance != this) {
      instance.onReceive(context, intent);
      return;
    }

    initialize(context);
    startService(context, intent.getAction());
  }
}
