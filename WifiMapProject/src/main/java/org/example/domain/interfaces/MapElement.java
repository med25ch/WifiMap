package org.example.domain.interfaces;

public interface MapElement {
    boolean contains(double x, double y);
    void handleClick();
    void update(Object... params);
    String getDetails();
    String getId();
}
