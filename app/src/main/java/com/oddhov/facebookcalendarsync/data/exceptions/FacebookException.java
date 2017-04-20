package com.oddhov.facebookcalendarsync.data.exceptions;

public class FacebookException extends Exception {
    public FacebookException(String tag, String message) {
        super("tag: " + tag + ", message: " + message);
    }
}
