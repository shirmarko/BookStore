package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private int Id;

	public LogisticsService(int Id) {
		super("logistics " +Id);
		this.Id = Id;
	}

	@Override
	protected void initialize() {
		this.subscribeEvent(DeliveryEvent.class, (event)-> {
			VehicleAvailabilityEvent take = new VehicleAvailabilityEvent();
			Future<Future<DeliveryVehicle>> fAvail = this.sendEvent(take); //check for a delivery vehicle
			if(fAvail != null) {//there a ms that handles that checkAvailabilityEvent
				if (fAvail.get() != null) {
					if (fAvail.get().get() != null) {//for the case resource was terminated and resolved the event as null
						fAvail.get().get().deliver(event.getAddress(), event.getDistance());// deliver the book
						ReleaseEvent<Boolean> release = new ReleaseEvent<>(fAvail.get().get());// release the delivery vehicle
						Future<Boolean> fRelease = this.sendEvent(release);
					}
				}
			}
		});
		this.subscribeBroadcast(FinalTickBroadcast.class, (broadcast)-> this.terminate());
	}
}
