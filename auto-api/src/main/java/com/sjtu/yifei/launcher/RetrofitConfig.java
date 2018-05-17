package com.sjtu.yifei.launcher;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sjtu.yifei.util.Utils;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/15
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class RetrofitConfig {
    public static void init(@NonNull Application application) {
        Utils.init(application);
    }

    public static void init(@NonNull final Context context) {
        Utils.init(context);
    }
}
