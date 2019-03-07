package com.atheeshproperty.messageassistantfinal;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class AppRequirements {

    public void checkWhetherInProtectedAppsOfHuawei(final Context context){

        final SharedPreferences settings = context.getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);

        if("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !skipMessage) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(context);
            builder.setTitle("Warning").setMessage("This app is in protected app list. Please remove this app from protected app list for work properly!")
                    .setPositiveButton("Go to protected apps", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            context.startActivity(intent);
                            settings.edit().putBoolean("protected",true).apply();
                        }
                    }).create().show();
        }


    }
}
