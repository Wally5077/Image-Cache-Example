package com.home.cloudinteractiveinterview.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AndroidThreadExecutor implements ThreadExecutor {

    private static final int MAX_THREAD = 12;
    private ExecutorService executorService = Executors.newScheduledThreadPool(MAX_THREAD);
    private Handler handler = new Handler(Looper.getMainLooper());

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public void executeUiThread(Runnable runnable) {
        handler.post(runnable);
    }
}
