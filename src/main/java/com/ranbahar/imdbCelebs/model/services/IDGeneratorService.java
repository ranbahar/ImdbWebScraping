package com.ranbahar.imdbCelebs.model.services;

import java.util.concurrent.atomic.AtomicInteger;

public final class IDGeneratorService {

    static final AtomicInteger generator = new AtomicInteger();

    private IDGeneratorService() {
    }

    public static int generateID() {
        return generator.incrementAndGet();
    }
}
