package com.vztot.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Message {
    private User user;
    private long time;
    private String text;

    public Message(User user, long time, String text) {
        this.user = user;
        this.time = time;
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;
        if (time != message.time) {
            return false;
        }
        if (!Objects.equals(user, message.user)) {
            return false;
        }
        return Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 17 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[" + formatLongToTime(time) + "]" + " " + user.getName() + ": " + text;
    }

    private String formatLongToTime(long l) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date(l));
    }
}
