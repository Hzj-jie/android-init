package org.gemini.init;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.gemini.shared.Debugging;
import org.gemini.shared.Formatter;
import org.gemini.shared.Notifier;
import org.gemini.shared.Preconditions;
import org.gemini.shared.Storage;
import org.gemini.shared.TsvReader;

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

  public final class Script {
    public static final String SCRIPT = ".sh";
    public static final String AM_DELEGATE = ".am";
    private final String file;

    public Script(String file) {
      Preconditions.isNotNull(file);
      this.file = file;
    }

    private final Map<String, String> mergedEnvs() {
      HashMap<String, String> result = new HashMap<>();
      if (internalStorageDirectory() != null) {
        result.put("INTERNAL", internalStorageDirectory());
      }
      else {
        result.put("INTERNAL", "/storage/sdcard0");
      }
      if (externalStorageDirectory() != null) {
        result.put("SDCARD", externalStorageDirectory());
      }
      else {
        result.put("SDCARD", result.get("INTERNAL"));
      }
      result.put("CURRENT", (new File(file)).getParent());
      result.put("OUTPUT_DIR", outputDir());
      result.put("WIFI_ON", String.valueOf(Status.wifiIsOn()));
      result.put("WIFI_CONNECT", String.valueOf(Status.wifiIsConnected()));
      result.put(
          "MOBILE_DATA_CONNECT",
          String.valueOf(Status.mobileDataIsConnected()));
      result.put("SIGNAL_STRENGTH", String.valueOf(Status.signalStrength()));
      result.put("SCREEN_ON", String.valueOf(Status.screenIsOn()));
      result.put("USER_PRESENT", String.valueOf(Status.userIsPresenting()));
      result.put("WIFI_SSID", Status.ssid());
      result.put("WIFI_LAST_SSID", Status.lastSsid());
      result.put("WIFI_LAST_ACTIVE_SSID", Status.lastActiveSsid());
      result.put("CARRIER", Status.carrier());
      result.put(
          "PREFERRED_NETWORK_TYPE",
          String.valueOf(Status.preferredNetworkType()));
      result.put("MODEL", Build.MODEL);
      result.put("SDK_VERSION", String.valueOf(Build.VERSION.SDK_INT));
      result.put("POWER_CONNECTED", String.valueOf(Status.powerConnected()));
      result.put("POWER_LEVEL", String.valueOf(Status.powerLevel()));
      result.put("POWER_LEVEL_LOW", String.valueOf(Status.powerLevelLow()));
      result.put("USB_CHARGING", String.valueOf(Status.usbCharging()));
      result.put("AC_CHARGING", String.valueOf(Status.acCharging()));
      result.put("WIRELESS_CHARGING", String.valueOf(Status.wirelessCharging()));
      result.put("NETWORK_CLASS", String.valueOf(Status.networkClass()));
      result.put("DATA_NETWORK_CLASS", String.valueOf(Status.dataNetworkClass()));
      result.put(
          "VOICE_NETWORK_CLASS",
          String.valueOf(Status.voiceNetworkClass()));
      if (envs != null) {
        result.putAll(envs);
      }
      return result;
    }

    private int execSh() {
      Log.i(TAG, "Start Sh " + file);
      Process p = null;
      List<String> prog = new ArrayList<>();
      prog.add("/system/bin/sh");
      prog.add(file);
      try {
        ProcessBuilder builder = new ProcessBuilder()
            .command(prog)
            .redirectErrorStream(true)
            .directory((new File(file)).getParentFile());
        builder.environment().putAll(mergedEnvs());
        p = builder.start();
      }
      catch (Exception ex) {
        Notifier.notify(context, Notifier.Configuration
            .New()
            .withIcon(R.drawable.blank)
            .withText("Failed to start process " + file + ": " + ex));
        return -1;
      }

      BufferedReader in = new BufferedReader(
        new InputStreamReader(p.getInputStream()), 1);
      try {
        String line = null;
        while ((line = in.readLine()) != null) {
          Log.i(TAG, file + " >> " + line);
        }
      }
      catch (IOException ex) {
        Notifier.notify(context, Notifier.Configuration
            .New()
            .withIcon(R.drawable.blank)
            .withText("Failed to read process output of " + file + ": " + ex));
      }
      int r = waitFor(p);
      Log.i(TAG, "Command " + file + " finishes at " + Debugging.currentTime() +
                 " with exit code " + r);
      return r;
    }

    private void execAmDelegate() {
      Log.i(TAG, "Start AmDelegate " + file);
      Map<String, String> envs = mergedEnvs();
      try (TsvReader reader = new TsvReader(file)) {
        while (true) {
          String[] line = reader.readLine();
          if (line == null) {
            break;
          }
          if (line.length < 3) {
            Notifier.notify(context, Notifier.Configuration
                .New()
                .withIcon(R.drawable.blank)
                .withText("AmDelegate line " + Arrays.toString(line) +
                          " do not contain enough parameters"));
            continue;
          }
          if (!line[0].isEmpty()) {
            String condition = Formatter.csvEnvs(envs, line[0]);
            if (!new File(condition).isAbsolute()) {
              condition = new File((new File(file)).getParentFile(),
                                   condition).getPath();
            }
            if (new Script(condition).execSh() != 0) {
              Log.i(TAG,
                    "Filter " + condition + " returns !0 value, ignore " +
                    Arrays.toString(line));
              continue;
            }
          }
          for (int i = 1; i < line.length; i++) {
            line[i] = Formatter.csvEnvs(envs, line[i]);
          }
          Intent intent = new Intent()
              .setComponent(new ComponentName(line[1], line[2]))
              .setAction(Intent.ACTION_MAIN)
              .addCategory(Intent.CATEGORY_LAUNCHER)
              .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          if (line.length > 3 && !line[3].isEmpty()) {
            intent.setData(Uri.parse(line[3]));
          }
          Log.i(TAG, "Start activity intent " + intent.toString());
          context.startActivity(intent);
        }
      }
    }

    public void exec() {
      if (file.endsWith(SCRIPT)) {
        execSh();
      } else if (file.endsWith(AM_DELEGATE)) {
        execAmDelegate();
      } else {
        Preconditions.notReached();
      }
    }

    private int waitFor(Process p) {
      try {
        return p.waitFor();
      } catch (InterruptedException ex) {
        return Short.MIN_VALUE;
      }
    }
  }

  private final void addIfExists(File file, List<Script> prog) {
    Log.i(TAG, "Searching file " + file.toString() + "(" + file.exists() + ")");
    if (file.exists()) {
      prog.add(new Script(file.getAbsolutePath()));
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

  private final List<Script> buildCmd(String filename) {
    List<Script> prog = new ArrayList<>();
    String[] suffixes = new String[] { Script.SCRIPT, Script.AM_DELEGATE };
    Set<File> folders = new HashSet<>();
    folders.add(internalInitDirectory());
    folders.add(externalInitDirectory());
    folders.add(context.getFilesDir());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
      folders.add(context.getExternalFilesDir(null));
    }
    for (String suffix : suffixes) {
      for (File folder : folders) {
        addIfExists(new File(folder, filename + suffix), prog);
      }
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

  private final int exec() {
    Log.i(TAG, "Instance " + filename + " starts at " +
               Debugging.currentTime());
    try {
      List<Script> prog = buildCmd(filename);
      if (prog == null || prog.isEmpty()) {
        Log.w(TAG, "No " + filename + " scripts found.");
        return 0;
      }

      for (Script script : prog) {
        script.exec();
      }
      Log.i(TAG, prog.size() + " scripts have been executed.");
      return prog.size();
    } catch (Exception ex) {
      Notifier.notify(context, Notifier.Configuration
          .New()
          .withIcon(R.drawable.blank)
          .withText("Failed to execute commands: " + ex));
      return 0;
    } finally {
      Log.i(
          TAG,
          "Instance " + filename + " finishes at " + Debugging.currentTime());
    }
  }

  public static int exec(
      Context context,
      String initFolder,
      String filename,
      Map<String, String> envs) {
    return (new Executor(context, initFolder, filename, envs)).exec();
  }

  public static int exec(
      Context context,
      String filename,
      Map<String, String> envs) {
    return (new Executor(context, filename, envs)).exec();
  }
}
