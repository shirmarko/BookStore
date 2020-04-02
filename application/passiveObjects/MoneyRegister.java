package bgu.spl.mics.application.passiveObjects;
import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	private static class MoneyRegisterHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}
	private AtomicInteger totalEarning = new AtomicInteger(0);
	private LinkedList<OrderReceipt> ordersToPrint = new LinkedList<>();

	private MoneyRegister(){} //Empty constructor

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return MoneyRegisterHolder.instance;
	}

	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		synchronized (totalEarning) {
			totalEarning.addAndGet(r.getPrice());
			ordersToPrint.add(r);

		}
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
			return totalEarning.intValue();
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) { //checks if needs to be check if he has enough money
		synchronized (c) {
			c.setAvailableAmountInCreditCard(c.getAvailableCreditAmount() - amount);
		}
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try{
			FileOutputStream f = new FileOutputStream((filename));
			ObjectOutputStream object = new ObjectOutputStream(f);
			object.writeObject(ordersToPrint);
			object.close();
			f.close();
		}
		catch (IOException e){
			System.out.println("Error writing file " + filename + " ");
		}
	}

}
