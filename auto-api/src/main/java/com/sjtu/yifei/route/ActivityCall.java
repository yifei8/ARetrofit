package com.sjtu.yifei.route;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sjtu.yifei.util.Utils;

import java.io.Serializable;
import java.util.Map;

/**
 * 类描述：通过路由进行Activity组件之间的跳转
 * 创建人：yifei
 * 创建时间：2018/5/15
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class ActivityCall {

    private ActivityCall() {
    }

    private ActivityCall(Class activity, Map<String, Object> params, int requestCode) {
        this.activity = activity;
        this.params = params;
        this.requestCode = requestCode;
    }

    private Class activity;
    private Map<String, Object> params;
    private int requestCode;

    public static final class Builder {
        private Class activity;
        private Map<String, Object> params;
        private int requestCode;

        public Builder() {
        }

        public Builder activityClass(Class activity) {
            this.activity = activity;
            return this;
        }

        public Builder params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public Builder requestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public ActivityCall build() {
            return new ActivityCall(activity, params, requestCode);
        }
    }

    public void launch() {
        Activity activity = Utils.getTopActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, this.activity);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
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
                }
            }
            if (requestCode > 0) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivity(intent);
            }
        }
    }
}
