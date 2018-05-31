package com.sjtu.yifei.route;

import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/24
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class IProviderCall<T> implements Call<T> {
    private final ServiceMethod<Object> serviceMethod;

    public IProviderCall(ServiceMethod<Object> serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public T execute() {
        if (serviceMethod.clazz == null) {
            return null;
        }
        T result = null;
        try {
            Class<?>[] parameterTypes = new Class[serviceMethod.params.size()];
            Object[] vals = new Object[serviceMethod.params.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : serviceMethod.params.entrySet()) {
                Object val = entry.getValue();
                vals[i] = val;
                if (val instanceof Integer) {
                    parameterTypes[i] = int.class;
                } else if (val instanceof Double) {
                    parameterTypes[i] = double.class;
                } else if (val instanceof Byte) {
                    parameterTypes[i] = byte.class;
                } else if (val instanceof Short) {
                    parameterTypes[i] = short.class;
                } else if (val instanceof Long) {
                    parameterTypes[i] = long.class;
                } else if (val instanceof Float) {
                    parameterTypes[i] = float.class;
                } else if (val instanceof Boolean) {
                    parameterTypes[i] = boolean.class;
                } else if (val instanceof String) {
                    parameterTypes[i] = String.class;
                } else if (val instanceof Serializable) {
                    parameterTypes[i] = Serializable.class;
                } else if (val instanceof Parcelable) {
                    parameterTypes[i] = Parcelable.class;
                }
                i++;
            }
            Constructor constructor = serviceMethod.clazz.getConstructor(parameterTypes);
            result = (T) constructor.newInstance(vals);
        } catch (Exception e) {
            Log.e("auto-api", "" + e.getMessage());
            e.printStackTrace();
            try {
                result = (T) serviceMethod.clazz.newInstance();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }
}
