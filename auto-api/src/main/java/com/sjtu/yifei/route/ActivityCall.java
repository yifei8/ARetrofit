package com.sjtu.yifei.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.sjtu.yifei.util.Utils;

import java.io.Serializable;
import java.util.Map;

/**
 * [description]
 * author: yifei
 * created at 18/5/20 下午8:57
 */

public class ActivityCall<T> implements Call {
    private final ServiceMethod<T> serviceMethod;

    public ActivityCall(ServiceMethod<T> serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public Object execute() {
        Activity activity = Utils.getTopActivity();
        if (activity != null && serviceMethod.clazz != null) {
            Intent intent = new Intent(activity, serviceMethod.clazz);
            for (Map.Entry<String, Object> entry : serviceMethod.params.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if (val instanceof Integer) {
                    intent.putExtra(key, (Integer) val);
                } else if (val instanceof Double) {
                    intent.putExtra(key, (Double) val);
                } else if (val instanceof Byte) {
                    intent.putExtra(key, (Byte) val);
                } else if (val instanceof Short) {
                    intent.putExtra(key, (Short) val);
                } else if (val instanceof Long) {
                    intent.putExtra(key, (Long) val);
                } else if (val instanceof Float) {
                    intent.putExtra(key, (Float) val);
                } else if (val instanceof Boolean) {
                    intent.putExtra(key, (Boolean) val);
                } else if (val instanceof String) {
                    intent.putExtra(key, (String) val);
                } else if (val instanceof Serializable) {
                    intent.putExtra(key, (Serializable) val);
                } else if (val instanceof Parcelable) {
                    intent.putExtra(key, (Parcelable) val);
                } else  if (val instanceof CharSequence) {
                    intent.putExtra(key, (CharSequence) val);
                } else if (val instanceof Integer[]) {
                    intent.putExtra(key, (Integer[]) val);
                } else if (val instanceof Double[]) {
                    intent.putExtra(key, (Double[]) val);
                } else if (val instanceof Byte[]) {
                    intent.putExtra(key, (Byte[]) val);
                } else if (val instanceof Short[]) {
                    intent.putExtra(key, (Short[]) val);
                } else if (val instanceof Long[]) {
                    intent.putExtra(key, (Long[]) val);
                } else if (val instanceof Float[]) {
                    intent.putExtra(key, (Float[]) val);
                } else if (val instanceof Boolean[]) {
                    intent.putExtra(key, (Boolean[]) val);
                } else if (val instanceof String[]) {
                    intent.putExtra(key, (String[]) val);
                } else if (val instanceof Serializable[]) {
                    intent.putExtra(key, (Serializable[]) val);
                } else if (val instanceof Parcelable[]) {
                    intent.putExtra(key, (Parcelable[]) val);
                } else if (val instanceof CharSequence[]) {
                    intent.putExtra(key, (CharSequence[]) val);
                } else if (val instanceof Intent) {
                    intent.putExtras((Intent) val);
                } else if (val instanceof Bundle) {
                    intent.putExtras((Bundle) val);
                }
            }
            if (serviceMethod.flag > 0) {
                intent.addFlags(serviceMethod.flag);
            }
            if (serviceMethod.requestCode > 0) {
                activity.startActivityForResult(intent, serviceMethod.requestCode);
            } else {
                activity.startActivity(intent);
            }
        }
        return true;
    }
}
