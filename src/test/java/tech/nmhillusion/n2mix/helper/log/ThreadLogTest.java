package tech.nmhillusion.n2mix.helper.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.constant.LogLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-09-08
 */
public class ThreadLogTest {

    @BeforeAll
    static void setupLogger() {
        final tech.nmhillusion.pi_logger.model.LogConfigModel updatedConfig = LogHelper.getDefaultPiLoggerConfig()
                .setLogLevel(LogLevel.TRACE)
                .setTimestampPattern("yyyy/MMM/dd EEE HH:mm:ss.SSS");
        LogHelper.setDefaultPiLoggerConfig(
                updatedConfig
        )
        ;
    }


    @Test
    void testOnAnotherThread() {
        Assertions.assertDoesNotThrow(() -> {
            final String testThreadName = "Test Thread";
            final Thread thread = new Thread(() -> {
                getLogger(this).info("log message from thread {}.", testThreadName);
            }, testThreadName);

            thread.start();
            thread.join();
        });
    }

    @Test
    void testOnManyThreads() {
        Assertions.assertDoesNotThrow(() -> {
            AtomicInteger finishedThreadCount = new AtomicInteger();
            final int maxThreads = 10;
            final int loopCount = 21;
            final List<Thread> threads = new ArrayList<>();

            for (int threadIdx = 0; threadIdx < maxThreads; threadIdx++) {
                final String testThreadName = "Test Thread " + threadIdx;
                final Thread thread = new Thread(() -> {

                    for (int loopIdx = 0; loopIdx < loopCount; loopIdx++) {
                        getLogger(this).info("log message from thread {}. loop: {}", testThreadName, loopIdx);

                        if (loopIdx == loopCount - 1) {
                            finishedThreadCount.getAndIncrement();
                            getLogger(this).info("Finished on thread {}, count: {}/{}."
                                    , testThreadName
                                    , finishedThreadCount.get()
                                    , maxThreads
                            );
                        }

                        Assertions.assertDoesNotThrow(() -> {
                            Thread.sleep(1400L);
                        });
                    }

                }, testThreadName);

                threads.add(thread);
            }

            for (Thread thread : threads) {
                thread.start();
            }

            while (finishedThreadCount.get() < maxThreads) ;
        });
    }
}