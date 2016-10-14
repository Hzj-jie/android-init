package org.gemini.init;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Executor
{
    private final Context context;
    private final String initFolder;
    private final String filename;
    private final Map<String, String> envs;

    public Executor(Context context,
                    String initFolder,
                    String filename,
                    Map<String, String> envs)
    {
        this.context = context;
        this.initFolder = initFolder;
        this.filename = filename;
        this.envs = envs;
    }

    public Executor(Context context, String filename, Map<String, String> envs)
    {
        this(context, "init", filename, envs);
    }

    private static final boolean addIfExists(File file, List<String> prog)
    {
        if (file.exists())
        {
            prog.add(file.getAbsolutePath());
            return true;
        }
        return false;
    }

    private static final int waitFor(Process p)
    {
        try
        {
            return p.waitFor();
        }
        catch (InterruptedException ex)
        {
            return Short.MIN_VALUE;
        }
    }

    private static final String internalStorageDirectory()
    {
        String p = Environment.getExternalStorageDirectory().getAbsolutePath();
        String e = System.getenv("EXTERNAL_STORAGE");
        if (p.equals(externalStorageDirectory()) && e != null)
            return e;
        else
            return p;
    }

    private static final String externalStorageDirectory()
    {
        return System.getenv("SECONDARY_STORAGE");
    }

    private final File internalInitDirectory()
    {
        return new File(internalStorageDirectory(), initFolder);
    }

    private final File externalInitDirectory()
    {
        return new File(externalStorageDirectory(), initFolder);
    }

    private final List<String> buildCmd(String filename)
    {
        List<String> prog = new ArrayList<>();
        prog.add("/system/bin/sh");
        if (addIfExists(new File(internalInitDirectory(), filename), prog) ||
            addIfExists(new File(externalInitDirectory(), filename), prog) ||
            addIfExists(new File(context.getFilesDir(), filename), prog) ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO &&
             addIfExists(new File(context.getExternalFilesDir(null), filename),
                         prog)))
            return prog;
        return null;
    }

    private final void exec()
    {
        final Logger logger = new Logger(context, filename + ".log");
        if (!logger.writeLine(">>>> Instance " + filename +
                              " starts at " + Logger.currentTime()))
            return;
        try
        {
            List<String> prog = buildCmd(filename);
            if (prog == null || prog.isEmpty())
            {
                logger.writeLine("No " + filename + " scripts found.");
                return;
            }
            if (!logger.writeLine(">>>> Start command " + prog))
                return;
            Process p = null;
            try
            {
                ProcessBuilder builder = new ProcessBuilder()
                        .command(prog)
                        .redirectErrorStream(true)
                        .directory(logger.outDir);
                if (externalStorageDirectory() != null)
                {
                    builder.environment().put("SDCARD",
                                              externalStorageDirectory());
                }
                if (internalStorageDirectory() != null)
                {
                    builder.environment().put("INTERNAL",
                                              internalStorageDirectory());
                }
                if (envs != null) {
                    for (Map.Entry<String, String> entry : envs.entrySet()) {
                        builder.environment().put(entry.getKey(),
                                                  entry.getValue());
                    }
                }
                builder.environment().put(
                    "WIFI_ON",
                    String.valueOf(Receiver.Status.wifiIsOn()));
                builder.environment().put(
                    "WIFI_CONNECT",
                    String.valueOf(Receiver.Status.wifiIsConnected()));
                builder.environment().put(
                    "SIGNAL_STRENGTH",
                    String.valueOf(Receiver.Status.signalStrength()));
                builder.environment().put(
                    "SCREEN_ON",
                    String.valueOf(Receiver.Status.screenIsOn()));
                builder.environment().put(
                    "USER_PRESENT",
                    String.valueOf(Receiver.Status.userIsPresenting()));
                p = builder.start();
            }
            catch (Exception ex)
            {
                logger.notify("Failed to start process, ex " + ex.getMessage());
                return;
            }
            BufferedReader in = new BufferedReader(
                new InputStreamReader(p.getInputStream()), 1);
            try
            {
                String line = null;
                while ((line = in.readLine()) != null)
                {
                    if (!logger.writeLine(line))
                        return;
                }
            }
            catch (IOException ex)
            {
                logger.notify("Failed to read process output.");
            }
            int r = waitFor(p);
            if (!logger.writeLine(">>>> Instance finishes at " +
                                  Logger.currentTime() +
                                  ", with exit code " +
                                  r))
                return;
        }
        finally
        {
            logger.close();
        }
    }

    public static final void exec(Context context,
                                  String initFolder,
                                  String filename,
                                  Map<String, String> envs)
    {
        new Executor(context, initFolder, filename, envs).exec();
    }

    public static final void exec(Context context,
                                  String filename,
                                  Map<String, String> envs)
    {
        new Executor(context, filename, envs).exec();
    }
}
