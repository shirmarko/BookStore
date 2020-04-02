package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class DeliveryEvent<Boolean> implements Event {

    private Future<Boolean> complete;
    private String address;
    private int distance;

    public DeliveryEvent(String address, int distance){
        this.address=address;
        this.distance=distance;
        this.complete = new Future<>();
    }
    @Override
    public Future<Boolean> getFuture() {
        return complete;
    }

    public String getAddress() {
        return address;
    }

    public int getDistance() {
        return distance;
    }
}
