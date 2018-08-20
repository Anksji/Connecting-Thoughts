package com.rsons.application.connecting_thoughts.ChatActivityAndClasses;

/**
 * Created by ankit on 2/24/2018.
 */

public class Messages {

    private String message, type,from,sourceLocation,time_duration,MessageId,To;
    private Object time;
    private boolean seen;
    private boolean IsDownloaded;

    public Messages() {
    }

    public Messages(String messageId,String message, String type, String from,String to, String sourceLocation, String time_duration,
                    Object time, boolean seen,boolean isDownloaded) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.sourceLocation = sourceLocation;
        this.time_duration = time_duration;
        this.time = time;
        this.seen = seen;
        this.IsDownloaded=isDownloaded;
        this.MessageId=messageId;
        this.To=to;
    }

    public String getTo() {
        return To;
    }

    public void setDownloaded(boolean downloaded) {
        IsDownloaded = downloaded;
    }

    public String getMessageId(){
        return MessageId;
    }

    public boolean IsDownloaded(){
        return IsDownloaded;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public String getTime_duration() {
        return time_duration;
    }

    public Object getTime() {
        return time;
    }

    public boolean isSeen() {
        return seen;
    }
}
