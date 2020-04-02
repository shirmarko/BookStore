package bgu.spl.mics;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class MessageBusImlHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> mapEventQueues = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> mapMsQueues = new ConcurrentHashMap<>();


	private MessageBusImpl(){
//		mapEventQueues.put(BookOrderEvent.class, new LinkedBlockingQueue<MicroService>());
//		mapEventQueues.put(CheckAvailabilityEvent.class, new LinkedBlockingQueue<MicroService>());
//		mapEventQueues.put(DeliveryEvent.class, new LinkedBlockingQueue<MicroService>());
//		mapEventQueues.put(BookTakeEvent.class, new LinkedBlockingQueue<MicroService>());
//		mapEventQueues.put(FinalTickBroadcast.class, new LinkedBlockingQueue<MicroService>());
//		mapEventQueues.put(ReleaseEvent.class, new LinkedBlockingQueue<MicroService>());
//		mapEventQueues.put(TickBroadcast.class, new LinkedBlockingQueue<MicroService>());
//		mapEventQueues.put(VehicleAvailabilityEvent.class, new LinkedBlockingQueue<MicroService>());


	}

	public static MessageBusImpl getInstance(){
		return MessageBusImlHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (type) {
			if (mapEventQueues.get(type) == null)
				mapEventQueues.put(type, new LinkedBlockingQueue<MicroService>());
			mapEventQueues.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type) {
			if (mapEventQueues.get(type) == null)
				mapEventQueues.put(type, new LinkedBlockingQueue<MicroService>());
			mapEventQueues.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		e.getFuture().resolve(result);
	}

	/**
	 * Adds the {@link Broadcast} {@code b} to the message queues of all the
	 * micro-services subscribed to {@code b.getClass()}.
	 * <p>
	 * @param b 	The message to added to the queues.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> a = mapEventQueues.get(b.getClass());
		for (MicroService ms : a) {
			try{
				mapMsQueues.get(ms).put(b);}
			catch (InterruptedException e){}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (mapEventQueues.get(e.getClass()) != null) {// the event does not registered
			synchronized (mapEventQueues.get(e.getClass())) { //sync the queue of the event
				if (!mapEventQueues.get(e.getClass()).isEmpty()) {
					try {
						MicroService s = mapEventQueues.get(e.getClass()).take();

						mapMsQueues.get(s).put(e); //adding the new event to the chosen ms //NULLPTR EXCP
						mapEventQueues.get(e.getClass()).put(s);// push back to the queue the microservice we changed
					} catch (InterruptedException b) {
					}
					return e.getFuture();
				}
			}
		}
		complete(e, null);
		//System.out.println(e.getClass() + " got NULL at SEND EVENT");
		return (e.getFuture());
	}


	@Override
	public void register(MicroService m) {
		mapMsQueues.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		//delete the ms from ths events queues
		for (HashMap.Entry<Class<? extends Message>, BlockingQueue<MicroService>> entry : mapEventQueues.entrySet()) {
			if (entry.getValue().contains(m))
				synchronized (entry.getValue()) { //sync the specific event queue
					entry.getValue().remove(m);
				}
		}

		//complete the futures who are left in the ms queue
		BlockingQueue<Message> msgQue = mapMsQueues.remove(m);
		for (Message msg : msgQue) {
			if (msg instanceof Event) {
				complete((Event) msg, null);
			}
		}
	}

	/**
	 * Using this method, a <b>registered</b> micro-service can take message
	 * from its allocated queue.
	 * This method is blocking meaning that if no messages
	 * are available in the micro-service queue it
	 * should wait until a message becomes available.
	 * The method should throw the {@link IllegalStateException} in the case
	 * where {@code m} was never registered.
	 * <p>
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return The next message in the {@code m}'s queue (blocking).
	 * @throws InterruptedException if interrupted while waiting for a message
	 *                              to became available.
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(mapMsQueues.get(m) == null) //micro service m is not registered
			throw new IllegalStateException();
		return mapMsQueues.get(m).take();
	}


}
