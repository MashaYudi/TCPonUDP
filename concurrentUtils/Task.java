package edu.spbspu.dcn.netcourse.concurrentUtils;

/**
 * Created by masha on 23.10.15.
 */
public interface Task extends Runnable {
    void stop();
}
