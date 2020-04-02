package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link //Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;

	public TimeService(int speed, int duration) {
		super("time");
		this.speed = speed;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		for (int i=1 ; i<duration; i++){
			sendBroadcast(new TickBroadcast(i));
			try{
				Thread.currentThread().sleep(speed);
			}
			catch (Exception e) {
			}

		}
		sendBroadcast(new TickBroadcast(duration));
		sendBroadcast(new FinalTickBroadcast());
		this.terminate();
	}

}
