package com.oddhov.facebookcalendarsync.data.exceptions;

public class UnexpectedException extends Exception {
    public UnexpectedException(String tag, String message) {
        super("tag: " + tag + ", message: " + message);
    }
}
