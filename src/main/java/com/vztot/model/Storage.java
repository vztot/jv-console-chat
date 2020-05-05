package com.vztot.model;

import java.util.ArrayList;
import java.util.List;

public class Storage<T> {
    private List<T> list;

    public Storage() {
        this.list = new ArrayList<>();
    }

    public synchronized boolean addMessage(T t) {
        return list.add(t);
    }

    public synchronized void update(Storage<T> storage) {
        this.list = storage.list;
    }

    public synchronized List<T> get() {
        return list;
    }

    public synchronized int size() {
        return list.size();
    }

    public synchronized int hash() {
        return list.hashCode();
    }
}
