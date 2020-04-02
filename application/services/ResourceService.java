package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.ReleaseEvent;
import bgu.spl.mics.application.messages.VehicleAvailabilityEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link //ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolderInst;
	private int Id;
	private BlockingQueue<Future<DeliveryVehicle>> waiting = new LinkedBlockingQueue<>(); //future objects waiting for a vehicle

	public ResourceService(int Id) {
		super("resources " +Id);
		resourcesHolderInst = ResourcesHolder.getInstance();
		this.Id = Id;
	}

	@Override
	protected void initialize() {
		this.subscribeEvent(VehicleAvailabilityEvent.class, (event)-> {
			Future<DeliveryVehicle> f = resourcesHolderInst.acquireVehicle();
			complete(event, f);
			try {
				if (!f.isDone())
					waiting.put(f);
			} catch (InterruptedException e) {
			}
		});

		this.subscribeEvent(ReleaseEvent.class, (event)-> {
			resourcesHolderInst.releaseVehicle(event.getVehicle());
			complete(event, true);
		});

		this.subscribeBroadcast(FinalTickBroadcast.class, (broadcast)-> {
			while(!waiting.isEmpty()) //there is no time for handling the delivery futures, so resolving them as null
				waiting.poll().resolve(null);
			this.terminate();
		});
	}

}
