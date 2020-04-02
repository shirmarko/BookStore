package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private static class ResourcesHolderHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	private BlockingQueue<DeliveryVehicle> vehicles = new LinkedBlockingQueue<>();
	private BlockingQueue<Future<DeliveryVehicle>> waiting = new LinkedBlockingQueue<>(); //waiting for vehicles list


	private ResourcesHolder(){} //Empty constructor

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		return ResourcesHolderHolder.instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public synchronized  Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> f = new Future<>();
		try {
			synchronized (vehicles) {
				if (vehicles.peek() == null) { //if there is no vehicles, add them to the waiting list
					waiting.put(f);
				} else { //there is an available vehicle, take it and send it as future
					DeliveryVehicle curr = vehicles.take();
					f.resolve(curr);
				}
			}
		} catch (Exception e) {
		}
		return f;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {

		try {//makes the vehicle available again
			synchronized (vehicles) {
				if (waiting.peek() != null) //if there is a future that waits for vehicle, give the vehicle to it
					waiting.take().resolve(vehicle);
				else {//else, add it to the vehicles list
					vehicles.put(vehicle);
				}
			}

		} catch (Exception e) {
		}

	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for (int i = 0; i < vehicles.length; i++) {
			try {
				this.vehicles.put(vehicles[i]);
			} catch (Exception e) {
			}
		}
	}
}
