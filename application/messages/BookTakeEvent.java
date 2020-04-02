package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class BookTakeEvent<OrderResult> implements Event {

    private Future<OrderResult> orderResult;
    private String bookTitle;

    public BookTakeEvent(String book){
        bookTitle = book;
        this.orderResult = new Future<>();
    }

    public Future<OrderResult> getFuture(){
        return orderResult;
    }

    public String getBookTitle(){
        return bookTitle;
    }



}
