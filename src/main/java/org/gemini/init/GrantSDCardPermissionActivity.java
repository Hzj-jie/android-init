package org.gemini.init;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.net.Uri;

public class GrantSDCardPermissionActivity extends Activity
{
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            startActivityForResult(
                new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), -1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent resultData)
    {
        if (resultCode != RESULT_OK) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        Uri treeUri = resultData.getData();
        grantUriPermission(
            getPackageName(),
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION |
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getContentResolver().takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION |
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        finish();
    }
}
