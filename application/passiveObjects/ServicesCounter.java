package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class ServicesCounter {

    private AtomicInteger total = new AtomicInteger(0);
    private AtomicInteger current = new AtomicInteger(0);
    private static ServicesCounter instance = new ServicesCounter();

    private ServicesCounter(){}

    public void SetTotal(AtomicInteger total){
        this.total = total;
    }

    public synchronized boolean isDone(){
        return (total.compareAndSet(current.intValue(), total.intValue()));
    }

    public synchronized void Decrement(){//subscribe 1 from the micro service counter
        this.current.getAndDecrement();
        notifyAll();
    }

    public synchronized void Increment(){ //add 1 to the micro service counter
        this.current.getAndIncrement();
        notifyAll();
    }

    public void initialize() {
        total = new AtomicInteger(0);
        current = new AtomicInteger(0);
    }

    public static ServicesCounter getInstance(){
        return instance;
    }

}
