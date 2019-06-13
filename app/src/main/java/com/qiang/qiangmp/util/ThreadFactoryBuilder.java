package com.qiang.qiangmp.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiaoqiang
 * @date 19-3-2
 */
public class ThreadFactoryBuilder {

    private String namePrefix = null;
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;

    public ThreadFactoryBuilder setNamePrefix(String namePrefix) {
        if (namePrefix == null) {
            throw new NullPointerException();
        }
        this.namePrefix = namePrefix;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder setPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            priority = Thread.MIN_PRIORITY;
        } else if (priority > Thread.MAX_PRIORITY) {
            priority = Thread.MAX_PRIORITY;
        }
        this.priority = priority;
        return this;
    }

    public ThreadFactory build() {
        return build(this);
    }

    private ThreadFactory build(ThreadFactoryBuilder builder) {
        final String namePrefix = builder.namePrefix;
        final boolean daemon = builder.daemon;
        final int priority = builder.priority;
        return new ThreadFactory() {
            AtomicInteger count = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(namePrefix + "-" + count.getAndIncrement());
                thread.setDaemon(daemon);
                thread.setPriority(priority);
                return thread;
            }
        };
    }
}
