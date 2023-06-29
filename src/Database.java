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
    private boolean isWriting;
    private Lock lockRead = new ReentrantLock();
    private Lock lockWrite = new ReentrantLock();
    private Condition readCondition = lockRead.newCondition();
    private Condition writeCondition = lockWrite.newCondition();
    private Set<Thread> nowReading;
    private final int maxRead;


    public Database(int maxNumOfReaders) {
        data = new HashMap<>();  // Note: You may add fields to the class and initialize them in here. Do not add parameters!
        countRead = 0;
        maxRead = maxNumOfReaders;
        nowReading = new HashSet<>();
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }


    public boolean readTryAcquire() {
        // TODO: Add your code here...

//        boolean flag = false;
//        if (lockWrite.tryLock()) {
//            lockWrite.lock();
//            if (countRead < maxRead ) {
//                lockRead.lock();
//                flag = true;
//                lockRead.unlock();
//                lockWrite.lock();
//            }
//        }
//        return flag;

        lockWrite.lock();
        try {
            return countRead < maxRead && !isWriting;
        } finally {
            lockWrite.unlock();
        }
    }

    public void readAcquire() {
        // TODO: Add your code here...

        lockWrite.lock();
        try {
            while (countRead >= maxRead || isWriting) {
                readCondition.await();
            }
            lockRead.lock();
            try {
                countRead++;
                nowReading.add(Thread.currentThread());
            } finally {
                lockRead.unlock();
            }
        } finally {
            lockWrite.unlock();
        }
    }

    public void readRelease() {
        // TODO: Add your code here...

        lockRead.lock();
        try {
            if (nowReading.contains(Thread.currentThread())) {
                countRead--;
                nowReading.remove(Thread.currentThread());
                if (countRead == 0) {
                    writeCondition.signal();
                }
            } else {
                throw new IllegalMonitorStateException("Illegal attempt to release read");
            }
        } finally {
            lockRead.unlock();
        }
    }

    public void writeAcquire() {
        lockWrite.lockInterruptibly();
        try {
            while (countRead > 0 || isWriting) {
                writeCondition.await();
            }
            isWriting = true;
        } finally {
            lockWrite.unlock();
        }
    }

    public boolean writeTryAcquire() {
        // TODO: Add your code here...
    }

    public void writeRelease() {
        // TODO: Add your code here...
    }
}