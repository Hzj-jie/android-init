package org.gemini.init;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExecService extends Service
{
    public static final String ONE_SHOT = "org.gemini.init.intent.ONE_SHOT";
    private static ExecService instance;

    private static final class Switch
    {
        public final String action;
        public final AtomicInteger running;
        public final String filename;

        public Switch(String action, String filename)
        {
            this.action = action;
            this.running = new AtomicInteger();
            this.filename = filename;
        }
    }

    private static final Switch[] switches = {
        new Switch(Intent.ACTION_BOOT_COMPLETED, "init.sh"),
        new Switch(Intent.ACTION_SCREEN_ON, "screen-on.sh"),
        new Switch(Intent.ACTION_SCREEN_OFF, "screen-off.sh"),
        new Switch(Receiver.WIFI_ON, "wifi-on.sh"),
        new Switch(Receiver.WIFI_OFF, "wifi-off.sh"),
        new Switch(Receiver.WIFI_CONN, "wifi-connected.sh"),
        new Switch(Receiver.WIFI_DISCONN, "wifi-disconnected.sh"),
        new Switch(ONE_SHOT, "one-shot.sh"),
    };

    private static final int defaultSwitch = 0;

    private Logger logger;

    public ExecService()
    {
        super();
        if (instance != null)  return;
        synchronized (ExecService.class)
        {
            if (instance == null)
                instance = this;
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        if (logger != null) logger.close();
        super.finalize();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        if (instance == this)
        {
            logger = new Logger(this, "service.log");
            Receiver.register(this);
        }
    }

    @Override
    public void onDestroy()
    {
        if (instance == this)
        {
            Receiver.unregister(this);
            if (logger != null) logger.close();
            for (int i = 0; i < switches.length; i++)
            {
                if (switches[i].running.get() == 1)
                {
                    startService(new Intent(switches[i].action,
                                            Uri.EMPTY,
                                            this,
                                            ExecService.class));
                }
            }
        }
        super.onDestroy();
    }

    private int exec(int switchId, final int startId)
    {
        final String action = switches[switchId].action;
        final AtomicInteger running = switches[switchId].running;
        final String filename = switches[switchId].filename;
        final ExecService me = this;
        if (running.compareAndSet(0, 1))
        {
            logger.writeLine("Going to start " + filename + " for action " +
                             action + " at " + Logger.currentTime());
            new Thread()
            {
                @Override
                public void run()
                {
                    Executor.exec(me, filename);
                    if (!running.compareAndSet(1, 0)) assert false;
                    logger.writeLine("Finished " + filename + " for action " +
                                     action + " at " + Logger.currentTime());
                    stopSelf(startId);
                }
            }.start();
            return START_STICKY;
        }
        else
        {
            stopSelf(startId);
            return START_NOT_STICKY;
        }
    }

    @Override
    public int onStartCommand(
        final Intent intent, final int flags, final int startId)
    {
        if (intent == null)
        {
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        if (instance != this)
            return instance.onStartCommand(intent, flags, startId);

        logger.writeLine(">>>> Received service command " + intent.getAction() +
                         " at " + Logger.currentTime());
        for (int i = 0; i < switches.length; i++)
        {
            if (switches[i].action.equals(intent.getAction()))
                return exec(i, startId);
        }

        return exec(defaultSwitch, startId);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
