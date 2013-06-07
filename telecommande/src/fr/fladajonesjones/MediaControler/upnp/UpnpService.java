package fr.fladajonesjones.MediaControler.upnp;


import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class UpnpService extends AndroidUpnpServiceImpl {

    private static final Logger log = Logger.getLogger(UpnpService.class.getName());

    @Override
    protected AndroidUpnpServiceConfiguration createConfiguration() {
        return new ConfAndroid();
    }

    public class MyThreadPoolExecutor extends ThreadPoolExecutor {
        public MyThreadPoolExecutor(int corePoolSize,
                                    int maximumPoolSize,
                                    long keepAliveTime,
                                    TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue,
                                    ThreadFactory threadFactory,
                                    RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        @Override
        public Future<?> submit(Runnable task) {
            //   log.info("Submit " + task.getClass().getName());
            return super.submit(task);
        }

        @Override
        public void execute(Runnable command) {
            //   log.info("Execute " + command.getClass().getName());

            super.execute(command);
        }
    }

    public class ConfAndroid extends AndroidUpnpServiceConfiguration {
        @Override
        protected ExecutorService getDefaultExecutorService() {
            return new MyThreadPoolExecutor(
                    10,
                    50,
                    60L,
                    TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    new ClingThreadFactory(),
                    new ThreadPoolExecutor.DiscardPolicy() {
                        // The pool is unbounded but rejections will happen during shutdown
                        @Override
                        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                            // Log and discard

                            //  log.info("Thread pool rejected execution of " + runnable.getClass());

                            //  log.info(String.valueOf(threadPoolExecutor.getActiveCount()));
                            //  log.info(String.valueOf(threadPoolExecutor.getQueue().size()));
                            super.rejectedExecution(runnable, threadPoolExecutor);
                        }
                    }
            );
        }
    }

}
