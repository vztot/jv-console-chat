package com.vztot.model;

public class Request {
    private int storageHash;
    private Message message;
    private RequestType requestType;
    private Storage<Message> storage;

    public Request(RequestType requestType, Message message) {
        this.requestType = requestType;
        this.message = message;
    }

    public Request(RequestType requestType, int storageHash) {
        this.requestType = requestType;
        this.storageHash = storageHash;
    }

    public Request(RequestType requestType, Storage<Message> storage) {
        this.requestType = requestType;
        this.storage = storage;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public Message getMessage() {
        return message;
    }

    public int getStorageHash() {
        return storageHash;
    }

    public Storage<Message> getStorage() {
        return storage;
    }
}
