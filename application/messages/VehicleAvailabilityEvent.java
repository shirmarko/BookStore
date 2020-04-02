package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;


public class VehicleAvailabilityEvent implements Event<Future<DeliveryVehicle>> {

    private Future<Future<DeliveryVehicle>> vehicle;

    public VehicleAvailabilityEvent(){
        this.vehicle = new Future();
    }

    @Override
    public Future<Future<DeliveryVehicle>> getFuture() {
        return vehicle;
    }
}
