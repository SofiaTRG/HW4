import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Database {
    private static int maxRead;
    private static ThreadLocal<Boolean> workHere = new ThreadLocal<>();
    private static Lock lockRead=new ReentrantLock();
    private static Lock lockWrite=new ReentrantLock();
    private static int numOfRead;


    private Map<String, String> data;

    public Database(int maxNumOfReaders) {
        data = new HashMap<>();  // Note: You may add fields to the class and initialize them in here. Do not add parameters!
        maxRead=maxNumOfReaders;
        workHere.set(false);
        numOfRead=0;
    }

    /**
     * putinig value into the date bace
     * @param key where to put
     * @param value what to put
     */
    public void put(String key, String value) {
        //writeTryAcquire();
        data.put(key, value);
        //writeRelease();
    }

    /**
     *
     * @param key from where take
     * @return returning value from the date base
     */
    public String get(String key) {
        //readTryAcquire();
        //String help=data.get(key);
        //readRelease();
        return data.get(key);

    }

    /**
     *
     * @return true if get green light to start read and false if not
     */
    public boolean readTryAcquire() {
        // TODO: Add your code here...
        if(lockWrite.tryLock()) //check no one writing
        {
            if(lockRead.tryLock())//no one read
                if(numOfRead<maxRead)//if he can start read
                {
                    numOfRead++;//adding to the count of people reading
                    lockRead.unlock();//unlock all locks
                    workHere.set(true);//mark that this proses started
                    lockWrite.unlock();
                    return true;
                }
            lockRead.unlock();//unlock all
            lockWrite.unlock();
        }
        return false;
    }

    /**
     * waiting until getting premisses to start reading
     */
    public void readAcquire() {
        while (!lockWrite.tryLock()); //stop from writing
        boolean acquire = false;
        while (!acquire) {
            lockRead.lock(); //stop others from reading
            if (numOfRead < maxRead) {//check if he can start
                numOfRead++;
                acquire = true;
            }
            lockRead.unlock();//unlock read
        }
        lockWrite.unlock();//unlock write
        workHere.set(true);//mark that this proses started
        // TODO: Add your code here...
    }

    /**
     * release reading
     * @throws IllegalMonitorStateException if calles but procese dont started
     */
    public void readRelease()throws IllegalMonitorStateException {
        //threi exeption
        if(!workHere.get()){
            String s="Illegal write release attempt";
            try{
                throw new IllegalMonitorStateException(s);
            }catch (IllegalMonitorStateException e){
                throw new IllegalMonitorStateException(s);
            }
        }
        else {
            // TODO: Add your code here...

            numOfRead = numOfRead - 1;
            workHere.set(false);
        }
    }

    /**
     * geting green light to start writing
     */
    public void writeAcquire() {
        while (!lockWrite.tryLock());
        while (!lockRead.tryLock());
        while (numOfRead!=0);
        workHere.set(true);
    }
    // TODO: Add your code here...

    /**
     *
     * @return true if can start writing and false othewise
     */
    public boolean writeTryAcquire() {
        // TODO: Add your code here...
        if(lockWrite.tryLock())
            if(lockRead.tryLock())
                if(numOfRead==0){
                    workHere.set(true);
                    return true;

                }


        return false;

    }

    /**
     * relese write
     * @throws IllegalMonitorStateException if calls but procese dont started
     */
    public void writeRelease() throws IllegalMonitorStateException {
        if(!workHere.get()){
            String s="Illegal write release attempt";
            try {
                throw new IllegalMonitorStateException(s);
            }catch (IllegalMonitorStateException e){
                throw new IllegalMonitorStateException(s);
            }
        }
        else {
            lockWrite.unlock();
            lockRead.unlock();
            workHere.set(false);
        }
        // TODO: Add your code here...
    }
}