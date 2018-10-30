package com.sjtu.yifei.route;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * [description]
 * author: yifei
 * created at 18/5/20 下午8:57
 */

public class ActivityCall implements Call<Boolean> {
    private final ServiceMethod<Object> serviceMethod;

    public ActivityCall(ServiceMethod<Object> serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public Boolean execute() {
        if (!TextUtils.isEmpty(serviceMethod.uristring)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(serviceMethod.uristring));
            List<ResolveInfo> activities = ActivityLifecycleMonitor.getApp().getPackageManager().queryIntentActivities(intent, 0);
            boolean isValid = !activities.isEmpty();
            if (isValid) {
                Activity activity = ActivityLifecycleMonitor.getTopActivity();
                if (activity != null) {
                    activity.startActivity(intent);
                    return true;
                }
            }
            Log.e("auto-api","\"" + serviceMethod.uristring + "\" is invalid");
            Toast.makeText(ActivityLifecycleMonitor.getApp(), "\"" + serviceMethod.uristring + "\" is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }

        List<AInterceptor> interceptors = RouteRegister.getInstance().getInterceptors();
        interceptors.add(new CallActivityAInterceptor());
        AInterceptor.Chain chain = new RealAInterceptorChain(interceptors, 0, serviceMethod);
        try {
            return chain.proceed();
        } catch (Exception e) {
            Log.e("auto-api", e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

}
