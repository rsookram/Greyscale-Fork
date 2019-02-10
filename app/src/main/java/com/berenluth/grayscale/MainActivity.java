package com.berenluth.grayscale;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Switch;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Util.hasPermission(this)) {
            Util.toggleGreyscale(this, !Util.isGreyscaleEnable(this));
            finish();
        } else {
            Dialog dialog = Util.createTipsDialog(this);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            dialog.show();
        }

    }
}
