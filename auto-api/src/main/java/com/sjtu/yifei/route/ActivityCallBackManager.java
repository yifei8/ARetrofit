package com.sjtu.yifei.route;

import android.support.annotation.IntRange;

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
    ActivityCallback callback;

    static ActivityCallBackManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ActivityCallBackManager();
        }
        return ourInstance;
    }

    private ActivityCallBackManager() {
    }

    void setResult(@IntRange(from = 0, to = 1) int result, Object data) {
        callback.onActivityResult(result, data);
        callback = null;
    }

}
