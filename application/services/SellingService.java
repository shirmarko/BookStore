package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private MoneyRegister moneyRegisterInst;
	private int sellingId;
	private int currentTick;

	public SellingService(int id) {
		super("selling " +id);
		sellingId = id;
		moneyRegisterInst = MoneyRegister.getInstance();
		currentTick = 0;
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, (broadcast)-> {
			currentTick = broadcast.getCurrentTick();
		});
		this.subscribeEvent(BookOrderEvent.class, (event)-> {
			CheckAvailabilityEvent check = new CheckAvailabilityEvent(event.getBookTitle());
			Future<Integer> fCheck = this.sendEvent(check);//checking the book price
			if (fCheck.get() != null) {
				int price = fCheck.get();
				if (price == -1) { //There is no available book
					complete(event, null);
				} else { //there is available book
					boolean taken = false; //boolean identifier for bookTake activity
					synchronized (event.getCustomer()) {
						if (event.getCustomer().getAvailableCreditAmount() - price < 0) {// the customer doesnt have enough money
							complete(event, null);
													} else {
							BookTakeEvent bookTake = new BookTakeEvent(event.getBookTitle());
							Future<OrderResult> fBookTake = this.sendEvent(bookTake); //trying to take the book
							if (fBookTake.get() == OrderResult.SUCCESSFULLY_TAKEN) {
								moneyRegisterInst.chargeCreditCard(event.getCustomer(), price); //payment
								taken = true;
							} else {
								complete(event, null);
							}
						}
					}
					if (taken) {
						DeliveryEvent deliver = new DeliveryEvent(event.getCustomer().getAddress(), event.getCustomer().getDistance());
						Future<Boolean> fDeliver = this.sendEvent(deliver); //delivering the book
						//the book will be delivered because the order was confirmed
						OrderReceipt or = new OrderReceipt(event.getOrderId(), this.getName(), event.getCustomer().getId(),
								event.getBookTitle(), price, currentTick, event.getOrderTick(), currentTick);
						moneyRegisterInst.file(or);
						complete(event, or);
					}
				}
			}
		});
		this.subscribeBroadcast(FinalTickBroadcast.class, (broadcast)-> this.terminate());
	}
}
