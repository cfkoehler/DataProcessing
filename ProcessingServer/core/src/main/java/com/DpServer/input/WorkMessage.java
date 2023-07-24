package com.DpServer.input;

import com.google.gson.Gson;
import emissary.pickup.WorkBundle;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WorkMessage implements Serializable {
    private static final long serialVersionUID = 123456789L;

    private String id;
    private WorkBundle workBundle;

    public WorkMessage(WorkBundle workBundle) {
        this.id = generateId();
        this.workBundle = workBundle;
    }

    public String getId() {
        return id;
    }

    protected static String generateId() {
        return UUID.randomUUID().toString();
    }

    public WorkBundle getWorkBundle() {
        return workBundle;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }

    public byte[] getBytes() {
        return getJson().getBytes(StandardCharsets.UTF_8);
    }

}
