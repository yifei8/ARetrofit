package com.sjtu.yifei.route;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntRange;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/10/29
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
final class ActivityCallBackManager {
    private static ActivityCallBackManager ourInstance;

    private Map<String, ActivityCallback> map;

    private LinkedList<String> stack = new LinkedList<String>();

    static ActivityCallBackManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActivityCallBackManager();
            ourInstance.map = new HashMap<>();
        }
        return ourInstance;
    }

    private ActivityCallBackManager() {
    }

    void putCallBack(String key, ActivityCallback callback) {
        if (callback != null) {
            String methodHash = String.valueOf(callback.hashCode());
            key = key + "_" + methodHash;
            stack.add(key);
            map.put(key, callback);
        }
    }

    void setResult(String key, @IntRange(from = -1, to = 0) final int result, final Object data) {
        setResult(key, result, data, 0);
    }

    void setResultDelayed(String key, @IntRange(from = -1, to = 0) final int result, final Object data) {
        setResult(key, result, data, 500);
    }

    void setResult(String key, @IntRange(from = -1, to = 0) final int result, final Object data, long delayMillis) {
        if (map.size() == 0) {
            return;
        }
        String lastKey = stack.getLast();
        if (lastKey != null && lastKey.startsWith(key + "_")) {
            stack.remove(lastKey);
            final ActivityCallback callback = map.get(lastKey);
            if (callback != null) {
                map.remove(lastKey);
                postMainRun(new Runnable() {
                    @Override
                    public void run() {
                        callback.onActivityResult(result, data);
                    }
                }, delayMillis);
            }
        }
    }

    private void postMainRun(Runnable runnable, long delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }
}
