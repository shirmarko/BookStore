package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookTakeEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventoryInst;
	private int Id;


	public InventoryService(int Id) {
		super("inventory " +Id);
		inventoryInst = Inventory.getInstance();
		this.Id = Id;
	}

	@Override
	protected void initialize() {
		this.subscribeEvent(CheckAvailabilityEvent.class, (event)->{
			this.complete(event, inventoryInst.checkAvailabiltyAndGetPrice(event.getBookTitle()));
		});
		this.subscribeEvent(BookTakeEvent.class, (event)-> {
			this.complete(event, inventoryInst.take(event.getBookTitle()));
		});
		this.subscribeBroadcast(FinalTickBroadcast.class, (broadcast)-> {
			this.terminate();
		});
	}

}
