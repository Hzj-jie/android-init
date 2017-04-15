package org.gemini.init;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExecService extends Service
{
    public static final String ONE_SHOT = "org.gemini.init.intent.ONE_SHOT";
    private static final String TAG = ExecService.class.getSimpleName();
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
        new Switch(Intent.ACTION_USER_PRESENT, "screen-unlock.sh"),
        new Switch(Receiver.WIFI_ON, "wifi-on.sh"),
        new Switch(Receiver.WIFI_OFF, "wifi-off.sh"),
        new Switch(Receiver.WIFI_CONN, "wifi-connected.sh"),
        new Switch(Receiver.WIFI_DISCONN, "wifi-disconnected.sh"),
        new Switch(Receiver.SIGNAL_STRENGTHS, "signal-strengths.sh"),
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

    private Logger logger;

    public ExecService()
    {
        super();
        if (instance != null) return;
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
        Receiver.register(this);
        if (instance == this)
        {
            logger = new Logger(this, "service.log");
            // This ensures no matter how the service started, the default
            // intent is always executed.
            startService(new Intent(switches[defaultSwitch].action,
                                    Uri.EMPTY,
                                    this,
                                    ExecService.class));
        }
    }

    @Override
    public void onDestroy()
    {
        Receiver.unregister(this);
        super.onDestroy();
    }

    private int exec(final int switchId, final int startId, final Bundle bundle)
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
                    Executor.exec(me, filename, parseBundle(bundle));
                    if (!running.compareAndSet(1, 0)) assert false;
                    logger.writeLine("Finished " + filename + " for action " +
                                     action + " at " + Logger.currentTime());
                    // Avoid the service from being killed.
                    if (switchId != defaultSwitch)
                    {
                        stopSelf(startId);
                    }
                }
            }.start();
            // Avoid the service from being killed.
            if (switchId == defaultSwitch)
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(120 * 1000);
                    }
                    catch (InterruptedException e) {}
                }
            }
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
        if (instance != this)
            return instance.onStartCommand(intent, flags, startId);

        if (intent != null)
        {
            logger.writeLine(">>>> Received service command " +
                             intent.getAction() +
                             " at " +
                             Logger.currentTime());
            for (int i = 0; i < switches.length; i++)
            {
                if (switches[i].action.equals(intent.getAction()))
                    return exec(i, startId, intent.getExtras());
            }
        }
        else
        {
            logger.writeLine(">>>> Received service command [null] at " +
                             Logger.currentTime());
        }

        return exec(defaultSwitch, startId, intent.getExtras());
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
