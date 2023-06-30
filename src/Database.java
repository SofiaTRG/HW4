import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private Map<String, String> data;
    private int countRead;
    private Lock lockRead = new ReentrantLock();
    private Lock lockWrite = new ReentrantLock();
    private Condition readCondition = lockRead.newCondition();
    private Condition writeCondition = lockWrite.newCondition();
    private Set<Thread> nowReading;
    private Thread nowWriting;
    private final int maxRead;


    public Database(int maxNumOfReaders) {
        data = new HashMap<>();  // Note: You may add fields to the class and initialize them in here. Do not add parameters!
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
        // TODO: Add your code here...
        lockRead.lock(); // Acquire the read lock
        try {
            if (countRead < maxRead) {
                countRead++; // Increment the number of readers
                return true; // Successfully acquired the read lock
            }
        } finally {
            lockRead.unlock(); // Release the read lock
        }
        return false; // Failed to acquire the read lock
    }

    public void readAcquire() {
        lockRead.lock(); // Acquire the read lock
        try {
            while (countRead >= maxRead) {
                readCondition.await();
            }
            countRead++;
            nowReading.add(Thread.currentThread());
        } catch (InterruptedException e) {
            // Handle InterruptedException
        } finally {
            lockRead.unlock();
        }
    }


    public void readRelease() {
        lockRead.lock();
        try {
            if (nowReading.contains(Thread.currentThread())) {
                countRead--;
                nowReading.remove(Thread.currentThread());
                if (countRead == 0) {
                    writeCondition.signal();
                }
            } else {
                throw new IllegalMonitorStateException("Illegal read release attempt");
            }
        } finally {
            lockRead.unlock();
        }
    }


    public void writeAcquire() {
        lockRead.unlock();
        lockWrite.lock();
        try {
            while (countRead > 0) {
                writeCondition.await();
            }
            nowWriting = Thread.currentThread();
        } catch (InterruptedException e) {
            // Handle InterruptedException
        } finally {
            lockWrite.unlock();
        }
    }



    public boolean writeTryAcquire() {
        // TODO: Add your code here...
        lockWrite.lock();
        try {
            return countRead == 0;
        } finally {
            lockWrite.unlock();
        }
    }

    public void writeRelease() {
        lockWrite.lock();
        try {
            if (nowWriting == Thread.currentThread()) {
                nowWriting = null;
                writeCondition.signal();
            } else {
                throw new IllegalMonitorStateException("Illegal write release attempt");
            }
        } finally {
            lockWrite.unlock();
        }
    }

}