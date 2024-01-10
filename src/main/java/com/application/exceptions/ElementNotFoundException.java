package com.application.exceptions;


import com.application.utils.MessagesUtils;

public class ElementNotFoundException extends Exception {

    public ElementNotFoundException() {
        super(MessagesUtils.EXCEPTION_NO_ELEMENT);
    }
}
