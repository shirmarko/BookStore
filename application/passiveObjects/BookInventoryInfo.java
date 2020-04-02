package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	private String BookTitle;
	private int AmoutInInventory;
	private int Price;

	public BookInventoryInfo(String BookTitle, int AmoutInInventory, int Price){
		this.BookTitle = BookTitle;
		this.AmoutInInventory = AmoutInInventory;
		this.Price = Price;
	}

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return BookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return AmoutInInventory;
	}

	public void setBookTitle(String bookTitle) {
		BookTitle = bookTitle;
	}

	public void setAmoutInInventory(int amoutInInventory) {
		AmoutInInventory = amoutInInventory;
	}

	public void setPrice(int price) {
		Price = price;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return Price;
	}

}
