package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}
	private ConcurrentHashMap<String, BookInventoryInfo> map= new ConcurrentHashMap<>(); //BookTitle, BookInfo
	private ConcurrentHashMap<String,Integer> inventoryToPrint = new ConcurrentHashMap<>(); //bookTitle, remained amount
	private HashMap<String,Integer> FinalPrint = new HashMap<>(); //the map for print

	private Inventory(){
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(int i=0; i<inventory.length; i++){
			map.put(inventory[i].getBookTitle(),inventory[i]);
			inventoryToPrint.put(inventory[i].getBookTitle(), inventory[i].getAmountInInventory());
		}

	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book) {
		//the book was load in the inventory-> map.get(book) wont be null
		synchronized (map.get(book)) {
			BookInventoryInfo temp = map.get(book);
			if (temp.getAmountInInventory() == 0)
				return OrderResult.NOT_IN_STOCK;
			else{
				temp.setAmoutInInventory(temp.getAmountInInventory() - 1);
				map.replace(book, temp);
				AtomicInteger val = new AtomicInteger(inventoryToPrint.remove(book));
				inventoryToPrint.put(book, val.decrementAndGet());
				return OrderResult.SUCCESSFULLY_TAKEN;
			}
		}
	}



	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		synchronized (map.get(book)) {
			if(map.get(book).getAmountInInventory() != 0)
				return map.get(book).getPrice();
			return -1;
		}
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */

	public void printInventoryToFile(String filename){
		try{
			for (HashMap.Entry<String, Integer> entry : inventoryToPrint.entrySet()) {
				FinalPrint.put(entry.getKey(),entry.getValue());
			}
			FileOutputStream f = new FileOutputStream((filename));
			ObjectOutputStream object = new ObjectOutputStream(f);
			object.writeObject(FinalPrint);
			object.close();
			f.close();
		}
		catch (IOException e){
		}
	}
}
