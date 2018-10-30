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

    // TODO: 2018/10/29 改用map管理声明周期
    Map<String, ActivityCallback> map;

    static ActivityCallBackManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActivityCallBackManager();
            ourInstance.map = new HashMap<>();
        }
        return ourInstance;
    }

    private ActivityCallBackManager() {
    }

    void setResult(String key,@IntRange(from = 0, to = 1) int result, Object data) {
        ActivityCallback callback = map.get(key);
        callback.onActivityResult(result, data);
        map.remove(key);
    }

}
