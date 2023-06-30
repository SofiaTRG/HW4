import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Database {
    private Map<String, String> data;
    private int countRead;
    private Object readLock = new Object();
    private Object writeLock = new Object();
    private Set<Thread> nowReading;
    private Thread nowWriting;
    private final int maxRead;

    public Database(int maxNumOfReaders) {
        data = new HashMap<>();
        countRead = 0;
        maxRead = maxNumOfReaders;
        nowReading = new HashSet<>();
        nowWriting = null;
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public boolean readTryAcquire() {
        synchronized (readLock) {
            if (countRead < maxRead) {
                countRead++;
                return true;
            }
        }
        return false;
    }

    public void readAcquire() {
        synchronized (readLock) {
            while (countRead >= maxRead || nowWriting != null) {
                try {
                    readLock.wait();
                } catch (InterruptedException e) {
                    // Handle InterruptedException
                }
            }
            countRead++;
            nowReading.add(Thread.currentThread());
        }
    }

    public void readRelease() {
        synchronized (readLock) {
            if (nowReading.contains(Thread.currentThread())) {
                countRead--;
                nowReading.remove(Thread.currentThread());
                if (countRead == 0) {
                    readLock.notifyAll();
                }
            } else {
                throw new IllegalMonitorStateException("Illegal read release attempt");
            }
        }
    }

    public void writeAcquire() {
        synchronized (writeLock) {
            while (nowWriting != null) {
                try {
                    writeLock.wait();
                } catch (InterruptedException e) {
                    // Handle InterruptedException
                }
            }
            nowWriting = Thread.currentThread();
        }
    }


    public boolean writeTryAcquire() {
        synchronized (writeLock) {
            return countRead == 0 && nowWriting == null;
        }
    }

    public void writeRelease() {
        synchronized (writeLock) {
            if (nowWriting == Thread.currentThread()) {
                nowWriting = null;
                synchronized (readLock) {
                    readLock.notifyAll();
                }
                writeLock.notify();
            } else {
                throw new IllegalMonitorStateException("Illegal write release attempt");
            }
        }
    }

}
