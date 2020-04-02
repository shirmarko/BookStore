package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseEvent<Boolean> implements Event {
    private Future<Boolean> complete;
    private DeliveryVehicle vehicle;

    public ReleaseEvent(DeliveryVehicle v){
        this.vehicle = v;
        this.complete = new Future<>();
    }

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public Future<Boolean> getFuture() {
        return complete;
    }


}
