package com.rsons.application.connecting_thoughts.Notification;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.rsons.application.connecting_thoughts.R;

import java.util.List;

import static com.rsons.application.connecting_thoughts.MainActivity.context;

/**
 * Created by ankit on 3/24/2018.
 */

public class NotificationSetting extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_setting);

        Button OpenSetting= (Button) findViewById(R.id.open_setting_page);

        OpenSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    String manufacturer = android.os.Build.MANUFACTURER;
                    Log.e("dkjfsfjsls","this is manufecture "+manufacturer);
                    if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                    } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    }

                    List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if  (list.size() > 0) {
                        Log.e("dkjfsfjsls","inside if "+manufacturer);
                        context.startActivity(intent);
                    }
                    Log.e("dkjfsfjsls","outside if "+manufacturer);
                } catch (Exception e) {
                    //Crashlytics.logException(e);
                    Log.e("dkjfsfjsls",e.getMessage());
                }
            }
        });
    }
}
