package org.gemini.init;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.net.Uri;

public class ActivateInitActivity extends Activity
{
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        startService(new Intent(Intent.ACTION_BOOT_COMPLETED,
                                Uri.EMPTY,
                                this,
                                ExecService.class));
        finish();
    }
}
