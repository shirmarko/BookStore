package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
*
 */

public class BookOrderEvent<OrderReceipt> implements Event {

    private Future<OrderReceipt> receipt;
    private String bookTitle;
    private int orderId;
    private Customer customer;
    private int orderTick;

    public BookOrderEvent(String book, int orderId, Customer customer, int orderTick){
        bookTitle = book;
        this.orderId = orderId;
        this.customer = customer;
        this.orderTick = orderTick;
        this.receipt = new Future<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public String getBookTitle(){
        return bookTitle;
    }

    public Future getFuture(){
        return receipt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderTick() {return orderTick;}
}

