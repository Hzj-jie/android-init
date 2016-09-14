package org.gemini.init;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

public final class Logger
{
    public final File outDir;
    public final PrintWriter writer;
    private final Context context;
    private final String filename;

    public Logger(Context context, String filename)
    {
        this.context = context;
        this.filename = filename;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO &&
            externalStorageWritable())
            outDir = context.getExternalFilesDir(null);
        else
            outDir = context.getFilesDir();
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(
                new FileWriter(new File(outDir, filename), true));
        }
        catch (Exception ex)
        {
            notify("Failed to create writer of " + outDir +
                   ", ex " + ex.getMessage());
        }
        this.writer = writer;
    }

    public static final String currentTime()
    {
        return DateFormat.getDateTimeInstance().format(new Date());
    }

    private static final boolean externalStorageWritable()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO &&
            Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState());
    }

    @SuppressWarnings("deprecation")
    @TargetApi(15)
    private static final Notification getNotification(
        Notification.Builder builder)
    {
        return builder.getNotification();
    }

    @SuppressWarnings("deprecation")
    @TargetApi(10)
    private static final Notification buildNotification(
        int icon, CharSequence msg, long when)
    {
        return new Notification(icon, msg, when);
    }

    public final void notify(String title, String msg)
    {
        try
        {
            Notification n = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {
                Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.blank)
                    .setContentText(msg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    builder.setStyle(
                        new Notification.BigTextStyle().bigText(msg));
                    n = builder.build();
                }
                else
                    n = getNotification(builder);
            }
            else
                n = buildNotification(R.drawable.blank, msg, 0);
            NotificationManager m =
                (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            m.notify(0, n);
        }
        catch (Exception ex)
        {
            writeLine("Failed to create notification, ex " + ex.getMessage());
        }
        writeLine("[" + title + "]: " + msg);
    }

    public final void notify(String msg)
    {
        notify("Service failed", msg);
    }

    public final boolean writeLine(String msg)
    {
        if (writer == null)
            return false;
        writer.println(msg);
        writer.flush();
        return true;
    }

    public final void close()
    {
        if (writer != null)
        {
            try
            {
                writer.close();
            }
            catch (Exception ex) {}
        }
    }
}
