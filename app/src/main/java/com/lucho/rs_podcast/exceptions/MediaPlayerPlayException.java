package com.lucho.rs_podcast.exceptions;

public class MediaPlayerPlayException extends Throwable {

    private final String msg;

    public MediaPlayerPlayException(String e) {
        msg=e;
    }

    public String getMsg() {
        return msg;
    }
}
