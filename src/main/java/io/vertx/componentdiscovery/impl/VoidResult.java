package io.vertx.componentdiscovery.impl;

import io.vertx.core.AsyncResult;

public class VoidResult implements AsyncResult<Void> {

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public Void result() {
        return null;
    }

    @Override
    public boolean succeeded() {
        return true;
    }

}
