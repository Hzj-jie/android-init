package org.gemini.init;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.gemini.shared.BatteryListener;
import org.gemini.shared.BootCompletedListener;
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

    public static boolean setPowerConnected(boolean v) {
      if (powerConnected == v) return false;
      powerConnected = v;
      return true;
    }

    public static boolean setPowerLevel(int v) {
      if (powerLevel == v) return false;
      powerLevel = v;
      return true;
    }

    public static boolean setPowerLevelLow(boolean v) {
      if (powerLevelLow == v) return false;
      powerLevelLow = v;
      return true;
    }

    public static boolean setUsbCharging(boolean v) {
      if (usbCharging == v) return false;
      usbCharging = v;
      return true;
    }

    public static boolean setAcCharging(boolean v) {
      if (acCharging == v) return false;
      acCharging = v;
      return true;
    }

    public static boolean setWirelessCharging(boolean v) {
      if (wirelessCharging == v) return false;
      wirelessCharging = v;
      return true;
    }

    public static boolean setNetworkClass(int v) {
      if (networkClass == v) return false;
      networkClass = v;
      return true;
    }

    public static boolean setDataNetworkClass(int v) {
      if (dataNetworkClass == v) return false;
      dataNetworkClass = v;
      return true;
    }

    public static boolean setVoiceNetworkClass(int v) {
      if (voiceNetworkClass == v) return false;
      voiceNetworkClass = v;
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
  public static final String POWER_CONN =
      "org.gemini.init.intent.POWER_CONN";
  public static final String POWER_DISCONN =
      "org.gemini.init.intent.POWER_DISCONN";
  public static final String POWER_LOW =
      "org.gemini.init.intent.POWER_LOW";
  public static final String POWER_OK =
      "org.gemini.init.intent.POWER_OK";
  public static final String SCREEN_ON =
      "org.gemini.init.intent.SCREEN_ON";
  public static final String SCREEN_OFF =
      "org.gemini.init.intent.SCREEN_OFF";
  public static final String USER_PRESENT =
      "org.gemini.init.intent.USER_PRESENT";
  private static final Receiver instance = new Receiver();
  private TelephonyState telephonyState;
  private PhonySignalStrengthListener signalStrengthListener;
  private ScreenListener screenListener;
  private NetworkListener networkListener;
  private BootCompletedListener bootCompletedListener;
  private BatteryListener batteryListener;

  synchronized private boolean initialize(final Context context) {
    if (telephonyState != null) return false;

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
            if (Settable.setScreenIsOn(true)) {
              startService(context, SCREEN_ON);
            }
          }
        });
    screenListener.onScreenOff().add(
        new Event.ParameterRunnable<Void>() {
          @Override
          public void run(Void nothing) {
            Settable.setUserIsPresenting(false);
            if (Settable.setScreenIsOn(false)) {
              startService(context, SCREEN_OFF);
            }
          }
        });
    screenListener.onUserPresent().add(
        new Event.ParameterRunnable<Void>() {
          @Override
          public void run(Void nothing) {
            if (Settable.setUserIsPresenting(true)) {
              startService(context, USER_PRESENT);
            }
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

    Preconditions.isNull(bootCompletedListener);
    bootCompletedListener =
        new BootCompletedListener(context, ExecService.class);

    Preconditions.isNull(batteryListener);
    batteryListener = new BatteryListener(context);
    batteryListener.onStateChanged().add(
        new Event.ParameterRunnable<BatteryListener.State>() {
          @Override
          public void run(BatteryListener.State state) {
            Preconditions.isNotNull(state);
            final boolean triggerPowerConnected =
                Settable.setPowerConnected(state.powerConnected());
            final boolean triggerLevelLow =
                Settable.setPowerLevelLow(state.levelLow());
            Settable.setPowerLevel(state.level());
            Settable.setUsbCharging(state.usbCharging());
            Settable.setAcCharging(state.acCharging());
            Settable.setWirelessCharging(state.wirelessCharging());

            if (triggerPowerConnected) {
              startService(context,
                           state.powerConnected() ? POWER_CONN : POWER_DISCONN);
            }
            if (triggerLevelLow) {
              startService(context,
                           state.levelLow() ? POWER_LOW : POWER_OK);
            }
          }
        });

    return true;
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
    Settable.setNetworkClass(instance.telephonyState.networkClass());
    Settable.setDataNetworkClass(instance.telephonyState.dataNetworkClass());
    Settable.setVoiceNetworkClass(instance.telephonyState.voiceNetworkClass());
    if (level >= PhonySignalStrengthListener.MIN_LEVEL &&
        level <= PhonySignalStrengthListener.MAX_LEVEL) {
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
  public void onReceive(Context context, Intent intent) {
    if (intent == null) return;

    if (instance != this) {
      instance.onReceive(context, intent);
      return;
    }

    if (initialize(context)) {
      // Only manually start the service if it's the first request after service
      // restarted; generally it will start INIT and LOOPER as the default
      // switch and default looper switch. Otherwise, listeners should have
      // handled the requests already.
      startService(context, intent.getAction());
    }
  }
}
