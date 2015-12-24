package org.gemini.init;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;

public class ActivateInitActivity extends Activity
{
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            getExternalFilesDir(null);
        getFilesDir();
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
