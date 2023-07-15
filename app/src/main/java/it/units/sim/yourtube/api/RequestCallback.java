package it.units.sim.yourtube.api;

public interface RequestCallback<T> {
    void onResponse(T responseObject);
}
