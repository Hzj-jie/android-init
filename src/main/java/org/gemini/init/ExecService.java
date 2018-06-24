package org.gemini.init;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.gemini.shared.Debugging;
import org.gemini.shared.KeepAliveService;

public final class ExecService extends KeepAliveService {
  public static final String ONE_SHOT = "org.gemini.init.intent.ONE_SHOT";
  private static final String INIT = "org.gemini.init.intent.INIT";
  private static final String LOOPER = "org.gemini.init.intent.LOOPER";
  private static final String TAG = Debugging.createTag("Init.ExecService");

  private static final class Switch {
    public final String action;
    public final AtomicInteger running;
    public final String filename;
    public final boolean repeat;

    public Switch(String action, String filename) {
      this(action, filename, false);
    }

    public Switch(String action, String filename, boolean repeat) {
      this.action = action;
      this.running = new AtomicInteger();
      this.filename = filename;
      this.repeat = repeat;
    }
  }

  private static final Switch[] switches = {
    new Switch(INIT, "init.sh"),
    new Switch(LOOPER, "looper.sh", true),
    new Switch(Receiver.SCREEN_ON, "screen-on.sh"),
    new Switch(Receiver.SCREEN_OFF, "screen-off.sh"),
    new Switch(Receiver.USER_PRESENT, "screen-unlock.sh"),
    new Switch(Receiver.WIFI_ON, "wifi-on.sh"),
    new Switch(Receiver.WIFI_OFF, "wifi-off.sh"),
    new Switch(Receiver.WIFI_CONN, "wifi-connected.sh"),
    new Switch(Receiver.WIFI_DISCONN, "wifi-disconnected.sh"),
    new Switch(Receiver.MOBILE_DATA_CONN, "mobile-data-connected.sh"),
    new Switch(Receiver.MOBILE_DATA_DISCONN, "mobile-data-disconnected.sh"),
    new Switch(Receiver.SIGNAL_STRENGTHS, "signal-strengths.sh"),
    new Switch(Receiver.POWER_CONN, "power-connected.sh"),
    new Switch(Receiver.POWER_DISCONN, "power-disconnected.sh"),
    new Switch(Receiver.POWER_LOW, "power-low.sh"),
    new Switch(Receiver.POWER_OK, "power-ok.sh"),
    new Switch(ONE_SHOT, "one-shot.sh"),
  };

  private static final Map<String, String> parseBundle(Bundle bundle) {
    if (bundle == null) return null;
    Map<String, String> r = new HashMap<String, String>();
    Set<String> keys = bundle.keySet();
    if (keys == null || keys.isEmpty()) return r;
    for (String s : keys) {
      Object obj = bundle.get(s);
      if (obj != null) r.put(s, obj.toString());
    }
    return r;
  }

  private static final int defaultSwitch = 0;
  private static final int defaultLooperSwitch = 1;

  @Override
  public void onCreate() {
    super.onCreate();
    startService(new Intent(switches[defaultSwitch].action,
                            Uri.EMPTY,
                            this,
                            ExecService.class));
    startService(new Intent(switches[defaultLooperSwitch].action,
                            Uri.EMPTY,
                            this,
                            ExecService.class));
  }

  private void exec(int switchId, final Bundle bundle) {
    final String action = switches[switchId].action;
    final AtomicInteger running = switches[switchId].running;
    final String filename = switches[switchId].filename;
    final boolean repeat = switches[switchId].repeat;
    final ExecService me = this;
    if (!running.compareAndSet(0, 1)) {
      Log.i(TAG, action + " has been started already, ignore the request at " +
                 Debugging.currentTime());
      return;
    }

    Log.i(TAG, "Going to start " + filename + " for action " + action +
               " at " + Debugging.currentTime());
    new Thread() {
      @Override
      public void run() {
        int r = 0;
        do {
          r = Executor.exec(me, filename, parseBundle(bundle));
        }
        while (r > 0 && repeat);
        if (!running.compareAndSet(1, 0)) assert false;
        Log.i(TAG, "Finished " + filename + " for action " + action +
                   " at " + Debugging.currentTime());
      }
    }.start();
  }

  @Override
  protected void process(Intent intent) {
    if (intent != null) {
      Log.i(TAG, "Received service command " + intent.getAction() +
                 " at " + Debugging.currentTime());
      for (int i = 0; i < switches.length; i++) {
        if (switches[i].action.equals(intent.getAction())) {
          exec(i, intent.getExtras());
          return;
        }
      }
    } else {
      Log.i(TAG, "Received service command [null] at " +
                 Debugging.currentTime());
    }
  }
}
