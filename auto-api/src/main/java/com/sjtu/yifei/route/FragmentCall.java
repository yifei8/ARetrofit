package com.sjtu.yifei.route;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sjtu.yifei.util.Utils;

import java.io.Serializable;
import java.util.Map;

/**
 * [description]
 * author: yifei
 * created at 18/5/20 下午8:57
 */

public class FragmentCall<T> implements Call {
    private final ServiceMethod<T> serviceMethod;

    public FragmentCall(ServiceMethod<T> serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public Object execute() {
        try {
            if (Utils.isSpecificClass(serviceMethod.clazz, Fragment.class)) {
                Fragment fragment = (Fragment) serviceMethod.clazz.newInstance();
                Bundle bundle = new Bundle();
                for (Map.Entry<String, Object> entry : serviceMethod.params.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof Integer) {
                        bundle.putInt(key, (Integer) val);
                    } else if (val instanceof Double) {
                        bundle.putDouble(key, (Double) val);
                    } else if (val instanceof Byte) {
                        bundle.putByte(key, (Byte) val);
                    } else if (val instanceof Short) {
                        bundle.putShort(key, (Short) val);
                    } else if (val instanceof Long) {
                        bundle.putLong(key, (Long) val);
                    } else if (val instanceof Float) {
                        bundle.putFloat(key, (Float) val);
                    } else if (val instanceof Boolean) {
                        bundle.putBoolean(key, (Boolean) val);
                    } else if (val instanceof String) {
                        bundle.putString(key, (String) val);
                    } else if (val instanceof Serializable) {
                        bundle.putSerializable(key, (Serializable) val);
                    }
                }
                fragment.setArguments(bundle);
                return fragment;
            } else if (Utils.isSpecificClass(serviceMethod.clazz, android.app.Fragment.class)) {
                android.app.Fragment fragment = (android.app.Fragment) serviceMethod.clazz.newInstance();
                Bundle bundle = new Bundle();
                for (Map.Entry<String, Object> entry : serviceMethod.params.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof Integer) {
                        bundle.putInt(key, (Integer) val);
                    } else if (val instanceof Double) {
                        bundle.putDouble(key, (Double) val);
                    } else if (val instanceof Byte) {
                        bundle.putByte(key, (Byte) val);
                    } else if (val instanceof Short) {
                        bundle.putShort(key, (Short) val);
                    } else if (val instanceof Long) {
                        bundle.putLong(key, (Long) val);
                    } else if (val instanceof Float) {
                        bundle.putFloat(key, (Float) val);
                    } else if (val instanceof Boolean) {
                        bundle.putBoolean(key, (Boolean) val);
                    } else if (val instanceof String) {
                        bundle.putString(key, (String) val);
                    } else if (val instanceof Serializable) {
                        bundle.putSerializable(key, (Serializable) val);
                    }
                }
                fragment.setArguments(bundle);
                return fragment;
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
