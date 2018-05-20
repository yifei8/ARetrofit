package com.sjtu.yifei.route;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;

/**
 * [description]
 * author: yifei
 * created at 18/5/20 下午7:36
 */

public class Platform {
    private static final Platform PLATFORM = findPlatform();

    static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException ignored) {
            throw new IllegalArgumentException(ignored);
        }
        return new Platform();
    }

    @Nullable
    Executor defaultCallbackExecutor() {
        return null;
    }

    static class Android extends Platform {
        @Override public Executor defaultCallbackExecutor() {
            return new MainThreadExecutor();
        }

        static class MainThreadExecutor implements Executor {
            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override public void execute(Runnable r) {
                handler.post(r);
            }
        }
    }
}
