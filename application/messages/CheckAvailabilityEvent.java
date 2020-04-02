package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class CheckAvailabilityEvent<Integer> implements Event {

    private Future<Integer> price;
    private String bookTitle;

    public CheckAvailabilityEvent(String book){
        bookTitle = book;
        this.price = new Future<>();
    }

    public Future<Integer> getFuture(){
        return price;
    }

    public String getBookTitle(){
        return bookTitle;
    }



}
