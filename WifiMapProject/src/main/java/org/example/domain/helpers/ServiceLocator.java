package org.example.domain.helpers;

import java.util.concurrent.ConcurrentHashMap;

// Thread-safe Singleton using volatile and double-checked locking.
// Prevent duplicate registrations with a check in setService().
// Allows removing services with removeService().

public class ServiceLocator {

    private static volatile ServiceLocator instance;
    private static final ConcurrentHashMap<Class<?>, Object> services = new ConcurrentHashMap<>();

    private ServiceLocator() {
        // Private constructor to prevent instantiation
    }

    public static ServiceLocator getInstance() {
        if (instance == null) {
            synchronized (ServiceLocator.class) {
                if (instance == null) {
                    instance = new ServiceLocator();
                }
            }
        }
        return instance;
    }

    public <T, U extends T> void setService(Class<T> type, U instance) {
        if (services.containsKey(type)) {
            throw new IllegalStateException("Service " + type.getName() + " is already registered.");
        }
        System.out.println("Registering service: " + type.getName());
        services.put(type, instance);
    }

    public <T> T getService(Class<T> type) {
        Object service = services.get(type);
        if (service == null) {
            throw new ServiceNotFoundException("Service " + type.getName() + " not found.");
        }
        return type.cast(service);
    }

    public <T> void removeService(Class<T> type) {
        if (services.remove(type) != null) {
            System.out.println("Removed service: " + type.getName());
        } else {
            throw new ServiceNotFoundException("Service " + type.getName() + " not found.");
        }
    }

    public static class ServiceNotFoundException extends RuntimeException {
        public ServiceNotFoundException(String message) {
            super(message);
        }
    }
}