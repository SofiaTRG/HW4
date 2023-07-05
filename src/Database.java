import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** A  database implementation with read and write synchronization. */
public class Database {
    private Map<String, String> data;
    private static int maxRead;
    private static int numCurrentReading;
    private static Lock lockRead = new ReentrantLock();
    private static Lock lockWrite = new ReentrantLock();
    /** The current threads that are reading */
    private static Set<Thread> nowReadWork = new HashSet<>();

    /**
     * Constructs a new Database instance with the specified maximum number of readers.
     * @param maxNumOfReaders The maximum number of readers allowed concurrently.
     */
    public Database(int maxNumOfReaders) {
        data = new HashMap<>();
        maxRead = maxNumOfReaders;
        numCurrentReading = 0;
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    /**
     * Tries to acquire a readlock for the current thread without blocking.
     * @return true if the readlock was acquired successfully, false otherwise.
     */
    public boolean readTryAcquire() {
        if (lockWrite.tryLock()) {
            if (lockRead.tryLock()) {
                if (numCurrentReading < maxRead) {
                    numCurrentReading++;
                    lockRead.unlock();
                    nowReadWork.add(Thread.currentThread());
                    lockWrite.unlock();
                    return true;
                }
                lockRead.unlock();
            }
            lockWrite.unlock();
        }
        return false;
    }

    /**
     * Acquires a readlock for the current thread, blocking until it is available.
     */
    public void readAcquire() {
        boolean flag = false;
        while (!lockWrite.tryLock());
        while (!flag) {
            lockRead.lock();
            if (numCurrentReading < maxRead) {
                numCurrentReading++;
                flag = true;
            }
            lockRead.unlock();
        }
        lockWrite.unlock();
        nowReadWork.add(Thread.currentThread());
    }

    /**
     * Releases the readlock held by the current thread.
     * @throws IllegalMonitorStateException if the current thread did not previously acquire the readlock.
     */
    public void readRelease() throws IllegalMonitorStateException {
        if (!nowReadWork.contains(Thread.currentThread())) {
            String errorMessage = "Illegal read release attempt";
            throw new IllegalMonitorStateException(errorMessage);
        } else {
            numCurrentReading--;
            nowReadWork.remove(Thread.currentThread());
        }
    }

    /**
     * Acquires a writelock for the current thread, blocking until it is available.
     */
    public void writeAcquire() {
        while (!lockWrite.tryLock());
        while (!lockRead.tryLock());
        while (numCurrentReading != 0);
        nowReadWork.add(Thread.currentThread());
    }

    /**
     * Tries to acquire a writelock for the current thread without blocking.
     * @return true if the write lock was acquired successfully, false otherwise.
     */
    public boolean writeTryAcquire() {
        if (lockWrite.tryLock()) {
            if (lockRead.tryLock()) {
                if (numCurrentReading == 0) {
                    nowReadWork.add(Thread.currentThread());
                    return true;
                }
                lockRead.unlock();
            }
            lockWrite.unlock();
        }
        return false;
    }

    /**
     * Releases the writelock held by the current thread.
     * @throws IllegalMonitorStateException if the current thread did not previously acquire the writelock.
     */
    public void writeRelease() throws IllegalMonitorStateException {
        if (!nowReadWork.contains(Thread.currentThread())) {
            String errorMessage = "Illegal write release attempt";
            throw new IllegalMonitorStateException(errorMessage);
        } else {
            lockWrite.unlock();
            lockRead.unlock();
            nowReadWork.remove(Thread.currentThread());
        }
    }
}
