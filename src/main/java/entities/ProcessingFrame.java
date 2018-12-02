package entities;

import java.io.Serializable;

public class ProcessingFrame implements Serializable {
    private String name;
    private int keyboard;
    private int mouseWheel;
    private long messageNumber;

    private boolean isConsumed = false;

    public ProcessingFrame() {
    }

    public int getMouseWheel() {
        return mouseWheel;
    }

    public ProcessingFrame setMouseWheel(int mouseWheel) {
        this.mouseWheel = mouseWheel;
        return this;
    }

    public long getMessageNumber() {
        return messageNumber;
    }

    public ProcessingFrame setMessageNumber(long messageNumber) {
        this.messageNumber = messageNumber;
        return this;
    }

    public String getName() {
        return name;
    }

    public int getKeyboard() {
        return keyboard;
    }

    public ProcessingFrame setKeyboard(int keyboard) {
        this.keyboard = keyboard;
        return this;
    }

    public ProcessingFrame setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public ProcessingFrame setConsumed(boolean consumed) {
        isConsumed = consumed;
        return this;
    }

    @Override
    public String toString() {
        return "{frameName: " + name + "; keyboard: " + keyboard + "; mouseWheel: " + mouseWheel
                + "messageNumber: " + messageNumber + "}";
    }
}
