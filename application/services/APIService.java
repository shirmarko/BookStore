package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private int currentTick;
	private int Id;
	private Customer customer;
	private ConcurrentHashMap<Integer, LinkedList<String>> ordersMap;
	private LinkedBlockingQueue<Future<OrderReceipt>> futureQueue;

	public APIService(int Id, Customer customer, ConcurrentHashMap<Integer, LinkedList<String>> ordersMap) {
		super("customer " +Id);
		currentTick = 0;
		this.Id = Id;
		this.customer =customer;
		this.ordersMap = ordersMap;
		futureQueue = new LinkedBlockingQueue<>();
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, (broadcast)-> {
			currentTick = broadcast.getCurrentTick();
			LinkedList<String> curr;
			try{
				curr = ordersMap.get(currentTick);
				Future<OrderReceipt> f;
				while(!curr.isEmpty()) { //while there are orders book in the same tick
					f = sendEvent(new BookOrderEvent(curr.getFirst(), 0, customer, currentTick));
					futureQueue.add(f);
					curr.removeFirst();
				}
			}
			catch (Exception e) {
			}
			for (Future<OrderReceipt> item : futureQueue) { //iterating on the future queues and check if they were complete
				if (item.isDone()) { //if future is complete, add the receipt to the customer and remove from queue
					if (item.get() != null) {
						customer.getCustomerReceiptList().add(item.get());
						futureQueue.remove(item);
					}
				}
			}
		});
		this.subscribeBroadcast(FinalTickBroadcast.class, (broadcast)-> {
			this.terminate();
		});
	}
}
