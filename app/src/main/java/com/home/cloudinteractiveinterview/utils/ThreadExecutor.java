package com.home.cloudinteractiveinterview.utils;

public interface ThreadExecutor {
    void execute(Runnable runnable);

    void executeUiThread(Runnable runnable);
}
