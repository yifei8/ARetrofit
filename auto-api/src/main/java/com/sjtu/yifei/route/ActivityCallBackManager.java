package com.sjtu.yifei.route;

import android.support.annotation.IntRange;

import java.util.HashMap;
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
            map.put(key, callback);
        }
    }

    void setResult(String key, @IntRange(from = -1, to = 0) int result, Object data) {
        ActivityCallback callback = map.get(key);
        if (callback != null) {
            callback.onActivityResult(result, data);
            map.remove(key);
        }
    }

}
