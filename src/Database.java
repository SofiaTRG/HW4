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
            if (countRead < maxRead ) {
                lockRead.lock();
                flag = true;
                lockRead.unlock();
            }
        }
        return flag;
    }

    public void readAcquire() {
        // TODO: Add your code here...

        while (!lockWrite.tryLock()) {
            this.wait();
        }
        while (countRead >= maxRead) {
            this.wait();
        }
        lockRead.lock();
        countRead += 1;
    }


    public void readRelease() {
        // TODO: Add your code here...

        try {


        } catch (IllegalMonitorStateException e) {

        } finally {

        }
    }

    public void writeAcquire() {
       // TODO: Add your code here...
    }

    public boolean writeTryAcquire() {
        // TODO: Add your code here...
    }

    public void writeRelease() {
        // TODO: Add your code here...
    }
}