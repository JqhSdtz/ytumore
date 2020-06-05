package util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JQH
 * @since 下午 11:23 19/09/12
 */
public class ThreadUtil {
    private static ThreadPoolExecutor asyncExecutor;
    private static boolean initialized = false;

    public static void createAsyncExecutor() {
        if(initialized)
            return;
        asyncExecutor = new ThreadPoolExecutor(25, 150, 1800L,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
        System.out.printf("async servlet executor has been load with corePoolSize:%d " +
                "maximumPoolSize:%d keepAliveTime:%d arrayBlockingQueueCapacity:%d\n", 25, 150, 1800L, 100);
        initialized = true;
    }

    public static void closeAsyncExecutor() {
        asyncExecutor.shutdown();
        System.out.println("async servlet executor has been shut down");
    }

    public static ThreadPoolExecutor getAsyncExecutor() {
        return asyncExecutor;
    }

    public static void completeAsyncTask(HttpServletRequest request) {
        if(request.isAsyncStarted())//结束异步操作
            request.getAsyncContext().complete();
    }

}
