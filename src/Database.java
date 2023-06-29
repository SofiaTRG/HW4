import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class Database {
    private Map<String, String> data;
    final int maxRead;
    private int countRead;
    private Lock lockRead = new ReentrantLock();
    private Lock lockWrite = new ReentrantLock();


    public Database(int maxNumOfReaders) {
        data = new HashMap<>();  // Note: You may add fields to the class and initialize them in here. Do not add parameters!
        countRead = 0;
        maxRead = maxNumOfReaders;
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }


    public boolean readTryAcquire() {
        // TODO: Add your code here...

        boolean flag = false;
        if (lockWrite.tryLock()) {
            lockWrite.lock();
            if (countRead < maxRead ) {
                lockRead.lock();
                flag = true;
                lockRead.unlock();
                lockWrite.lock();
            }
        }
        return flag;
    }

    public void readAcquire() {
        // TODO: Add your code here...

        while (!lockWrite.tryLock()) {
            lockRead.wait();
        }
        lockWrite.lock();
        while (countRead >= maxRead) {
            lockRead.wait();
        }
        if (lockRead.tryLock()) {
            lockRead.lock();
            countRead += 1;
        }
    }

    public void readRelease() {
        // TODO: Add your code here...

        /** not the one who's reading */
        try {
            if (Thread.holdsLock(lockRead)) {
                countRead -= 1;
                //lockRead.unlock();
            }
        }catch (IllegalMonitorStateException e) {
            System.out.println("Illegal read release attempt");
            throw e;
        } finally {
            lockRead.unlock();
        }
    }

    public void writeAcquire() {
       if (lockWrite.tryLock()) {

       }
    }

    public boolean writeTryAcquire() {
        // TODO: Add your code here...
    }

    public void writeRelease() {
        // TODO: Add your code here...
    }
}