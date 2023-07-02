import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private Map<String, String> data;
    private static int maxRead;
    private static int numCurrentReading;
    private static Lock lockRead = new ReentrantLock();
    private static Lock lockWrite = new ReentrantLock();
    private static Set<Thread> nowReadWork = new HashSet<>();

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

    public void readRelease() throws IllegalMonitorStateException {
        if (!nowReadWork.contains(Thread.currentThread())) {
            String errorMessage = "Illegal read release attempt";
            throw new IllegalMonitorStateException(errorMessage);
        } else {
            numCurrentReading--;
            nowReadWork.remove(Thread.currentThread());
        }
    }

    public void writeAcquire() {
        while (!lockWrite.tryLock());
        while (!lockRead.tryLock());
        while (numCurrentReading != 0);
        nowReadWork.add(Thread.currentThread());
    }

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
