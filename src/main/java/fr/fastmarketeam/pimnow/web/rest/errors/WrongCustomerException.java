package fr.fastmarketeam.pimnow.web.rest.errors;

public class WrongCustomerException extends Throwable {
    public WrongCustomerException(){
        super("You are not allowed to access to this element");
    }
}
