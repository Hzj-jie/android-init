package org.gemini.init;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.ArrayList;
import java.util.List;

public class InitService extends Service
{
    private static final String initFolder = "init";

    private static final File internalInitDirectory()
    {
        return new File(Environment.getExternalStorageDirectory(), initFolder);
    }

    private static final File externalInitDirectory()
    {
        return new File(System.getenv("SECONDARY_STORAGE"), initFolder);
    }

    private static final boolean externalStorageWritable()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO &&
            Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState());
    }

    private static final boolean externalStorageReadable()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO &&
            (externalStorageWritable() ||
             Environment.MEDIA_MOUNTED_READ_ONLY.equals(
                 Environment.getExternalStorageState()));
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

    private final File createOutputFile(String name)
    {
        File iodir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO &&
            externalStorageWritable())
            iodir = getExternalFilesDir(null);
        else
            iodir = getFilesDir();
        if (name == null || name.length() == 0)
            return iodir;
        else
            return new File(iodir, name);
    }

    private final File outputDirectory()
    {
        return createOutputFile(null);
    }

    private final PrintWriter createOutputFileWriter(String name)
        throws IOException
    {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("name");
        return new PrintWriter(createOutputFile(name));
    }

    private final boolean writeLine(PrintWriter writer, String msg)
    {
        writer.println(msg);
        writer.flush();
        return true;
        /*
        try
        {
            writer.write(msg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                writer.write(System.lineSeparator());
            else
                writer.write("\n");
            writer.flush();
            return true;
        }
        catch (IOException ex)
        {
            notify("Failed to write to " + writer);
        }
        return false;
        */
    }

    @SuppressWarnings("deprecation")
    @TargetApi(15)
    private static final Notification getNotification(Notification.Builder builder)
    {
        return builder.getNotification();
    }

    @SuppressWarnings("deprecation")
    @TargetApi(10)
    private static final Notification buildNotification(int icon, CharSequence msg, long when)
    {
        return new Notification(icon, msg, when);
    }

    private final void notify(PrintWriter writer, String title, String msg)
    {
        Notification n = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            Notification.Builder builder =  new Notification.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.blank)
                .setContentText(msg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                builder.setStyle(new Notification.BigTextStyle().bigText(msg));
                n = builder.build();
            }
            else
                n = getNotification(builder);
        }
        else
            n = buildNotification(R.drawable.blank, msg, 0);
        NotificationManager m = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        m.notify(0, n);
        if (writer != null)
            writeLine(writer, "[" + title + "]: " + msg);
    }

    private final void notify(String title, String msg)
    {
        notify(null, title, msg);
    }

    private final void notify(PrintWriter writer, String msg)
    {
        notify(writer, "Init service failed", msg);
    }

    private final void notify(String msg)
    {
        notify((PrintWriter) null, msg);
    }

    private final List<String> buildCmd()
    {
        final String fileName = "init.sh";
        List<String> prog = new ArrayList<>();
        prog.add("sh");
        if (addIfExists(new File(internalInitDirectory(), fileName), prog) ||
            addIfExists(new File(externalInitDirectory(), fileName), prog) ||
            addIfExists(new File(getFilesDir(), fileName), prog) ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO &&
             addIfExists(new File(getExternalFilesDir(null), fileName), prog)))
            return prog;
        return null;
    }

    private final void exec()
    {
        File iodir = outputDirectory();
        PrintWriter writer = null;
        try
        {
            writer = createOutputFileWriter("output.log");
        }
        catch (IOException ex)
        {
            notify("Failed to create writer of " + iodir);
            return;
        }
        try
        {
            List<String> prog = buildCmd();
            if (prog == null || prog.isEmpty())
            {
                notify(writer, "No initial scripts found.");
                return;
            }
            Process p = null;
            try
            {
                p = new ProcessBuilder()
                    .command(prog)
                    .redirectErrorStream(true)
                    .directory(iodir)
                    .start();
            }
            catch (IOException ex)
            {
                notify(writer, "Failed to start process.");
                return;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()), 1);
            try
            {
                String line = null;
                while ((line = in.readLine()) != null)
                {
                    if (!writeLine(writer, line))
                        return;
                }
            }
            catch (IOException ex)
            {
                notify(writer, "Failed to read process output.");
            }
            notify(writer, "Init service finished", "No other errors detected so far.");
            try
            {
                p.waitFor();
            }
            catch (InterruptedException ex) {}
        }
        finally
        {
            writer.close();
            /*
            try
            {
                writer.close();
            }
            catch (IOException ex)
            {
                notify("Failed to close writer of " + writer);
                return;
            }
            */
        }
    }

    @Override
    public void onCreate()
    {
        exec();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
