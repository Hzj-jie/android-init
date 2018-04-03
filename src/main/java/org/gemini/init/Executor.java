package org.gemini.init;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gemini.shared.Debugging;
import org.gemini.shared.Notifier;
import org.gemini.shared.Storage;

public final class Executor {
  private static final String TAG = Debugging.createTag("Init.Executor");
  private final Context context;
  private final String initFolder;
  private final String filename;
  private final Map<String, String> envs;
  private final Storage storage;

  private Executor(Context context,
                   String initFolder,
                   String filename,
                   Map<String, String> envs) {
    this.context = context;
    this.initFolder = initFolder;
    this.filename = filename;
    this.envs = envs;
    this.storage = new Storage(context);
  }

  private Executor(Context context,
                   String filename,
                   Map<String, String> envs) {
    this(context, "init", filename, envs);
  }

  private static final void addIfExists(File file, List<String> prog) {
    if (file.exists()) prog.add(file.getAbsolutePath());
  }

  private static final int waitFor(Process p) {
    try {
      return p.waitFor();
    }
    catch (InterruptedException ex) {
      return Short.MIN_VALUE;
    }
  }

  private final String internalStorageDirectory() {
    return storage.buildInSharedStoragePath();
  }

  private final String externalStorageDirectory() {
    return storage.externalSharedStoragePath();
  }

  private final File internalInitDirectory() {
    return new File(internalStorageDirectory(), initFolder);
  }

  private final File externalInitDirectory() {
    return new File(externalStorageDirectory(), initFolder);
  }

  private final List<String> buildCmd(String filename) {
    List<String> prog = new ArrayList<>();
    addIfExists(new File(internalInitDirectory(), filename), prog);
    addIfExists(new File(externalInitDirectory(), filename), prog);
    addIfExists(new File(context.getFilesDir(), filename), prog);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
       addIfExists(new File(context.getExternalFilesDir(null), filename), prog);
    }
    return prog;
  }

  private final String outputDir() {
    File file = context.getExternalFilesDir(null);
    // This should not happen as long as WRITE_EXTERNAL_STORAGE permission is
    // granted.
    if (file == null) return "";
    return file.toString();
  }

  private final void exec(String script) {
    Log.i(TAG, "Start command " + script);
    Process p = null;
    List<String> prog = new ArrayList<>();
    prog.add("/system/bin/sh");
    prog.add(script);
    try {
      ProcessBuilder builder = new ProcessBuilder()
          .command(prog)
          .redirectErrorStream(true)
          .directory((new File(script)).getParentFile());
      if (internalStorageDirectory() != null) {
        builder.environment().put(
            "INTERNAL", internalStorageDirectory());
      }
      else {
        builder.environment().put("INTERNAL", "/storage/sdcard0");
      }
      if (externalStorageDirectory() != null) {
        builder.environment().put(
            "SDCARD", externalStorageDirectory());
      }
      else {
        builder.environment().put(
            "SDCARD", builder.environment().get("INTERNAL"));
      }
      if (envs != null) {
        for (Map.Entry<String, String> entry : envs.entrySet()) {
          builder.environment().put(entry.getKey(), entry.getValue());
        }
      }
      builder.environment().put(
          "CURRENT", (new File(script)).getParent());
      builder.environment().put(
          "OUTPUT_DIR", outputDir());
      builder.environment().put(
          "WIFI_ON",
          String.valueOf(Status.wifiIsOn()));
      builder.environment().put(
          "WIFI_CONNECT",
          String.valueOf(Status.wifiIsConnected()));
      builder.environment().put(
          "MOBILE_DATA_CONNECT",
          String.valueOf(Status.mobileDataIsConnected()));
      builder.environment().put(
          "SIGNAL_STRENGTH",
          String.valueOf(Status.signalStrength()));
      builder.environment().put(
          "SCREEN_ON",
          String.valueOf(Status.screenIsOn()));
      builder.environment().put(
          "USER_PRESENT",
          String.valueOf(Status.userIsPresenting()));
      builder.environment().put(
          "WIFI_SSID",
          Status.ssid());
      builder.environment().put(
          "WIFI_LAST_SSID",
          Status.lastSsid());
      builder.environment().put(
          "WIFI_LAST_ACTIVE_SSID",
          Status.lastActiveSsid());
      builder.environment().put(
          "CARRIER",
          Status.carrier());
      builder.environment().put(
          "PREFERRED_NETWORK_TYPE",
          String.valueOf(Status.preferredNetworkType()));
      builder.environment().put(
          "MODEL",
          Build.MODEL);
      builder.environment().put(
          "POWER_CONNECTED",
          String.valueOf(Status.powerConnected()));
      builder.environment().put(
          "POWER_LEVEL",
          String.valueOf(Status.powerLevel()));
      builder.environment().put(
          "POWER_LEVEL_LOW",
          String.valueOf(Status.powerLevelLow()));
      builder.environment().put(
          "USB_CHARGING",
          String.valueOf(Status.usbCharging()));
      builder.environment().put(
          "AC_CHARGING",
          String.valueOf(Status.acCharging()));
      builder.environment().put(
          "WIRELESS_CHARGING",
          String.valueOf(Status.wirelessCharging()));
      builder.environment().put(
          "NETWORK_CLASS",
          String.valueOf(Status.networkClass()));
      builder.environment().put(
          "DATA_NETWORK_CLASS",
          String.valueOf(Status.dataNetworkClass()));
      builder.environment().put(
          "VOICE_NETWORK_CLASS",
          String.valueOf(Status.voiceNetworkClass()));
      p = builder.start();
    }
    catch (Exception ex) {
      Notifier.notify(context, Notifier.Configuration
          .New()
          .withIcon(R.drawable.blank)
          .withText("Failed to start process " + script +
                    ": " + ex.toString()));
      return;
    }

    BufferedReader in = new BufferedReader(
      new InputStreamReader(p.getInputStream()), 1);
    try {
      String line = null;
      while ((line = in.readLine()) != null) {
        Log.i(TAG, script + " >> " + line);
      }
    }
    catch (IOException ex) {
      Notifier.notify(context, Notifier.Configuration
          .New()
          .withIcon(R.drawable.blank)
          .withText("Failed to read process output of " + script +
                    ": " + ex.toString()));
    }
    int r = waitFor(p);
    Log.i(TAG, "Command " + script + " finishes at " + Debugging.currentTime() +
               " with exit code " + r);
  }

  private final int exec() {
    Log.i(TAG, "Instance " + filename + " starts at " +
               Debugging.currentTime());
    try {
      List<String> prog = buildCmd(filename);
      if (prog == null || prog.isEmpty()) {
        Log.w(TAG, "No " + filename + " scripts found.");
        return 0;
      }

      for (final String script : prog) {
        exec(script);
      }
      Log.i(TAG, prog.size() + " scripts have been executed.");
      return prog.size();
    }
    finally {
      Log.i(TAG, "Instance " + filename + " finishes at " +
                 Debugging.currentTime());
    }
  }

  public static final int exec(Context context,
                 String initFolder,
                 String filename,
                 Map<String, String> envs) {
    return (new Executor(context, initFolder, filename, envs)).exec();
  }

  public static final int exec(Context context,
                 String filename,
                 Map<String, String> envs) {
    return (new Executor(context, filename, envs)).exec();
  }
}
