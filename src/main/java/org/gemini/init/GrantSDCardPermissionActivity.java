package org.gemini.init;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import org.gemini.shared.Debugging;

public final class GrantSDCardPermissionActivity extends Activity {
  private static final String TAG =
      Debugging.createTag(GrantSDCardPermissionActivity.class);

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
      intent.addFlags(
          Intent.FLAG_GRANT_READ_URI_PERMISSION
          | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
          | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
          | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
      startActivityForResult(intent, 0);
    }
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode,
                                  Intent resultData) {
    if (resultCode != RESULT_OK) {
      return;
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return;
    }
    Uri treeUri = resultData.getData();
    Log.i(TAG, "onActivityResult: treeUri " + treeUri.toString());
    DocumentFile file = DocumentFile.fromTreeUri(this, treeUri);
    Log.i(TAG, "onActivityResult: documentFile " + file.getName());
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
