package org.example.azoi.dto;

public class Message {
    boolean status = false;
    String message = "NOT DEFINE, PLEASE CHECK CALL.";

    public Message() {
    }

    public Message(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "This msg is " + (status ? "ok" : "error") + "\n" + message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
