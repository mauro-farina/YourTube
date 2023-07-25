package it.units.sim.yourtube.model;

import java.util.List;

public class Category {

    private final String name;
    private final List<UserSubscription> channels;

    public Category(String name, List<UserSubscription> channels) {
        this.name = name;
        this.channels = channels;
    }

    public String getName() {
        return name;
    }

    public List<UserSubscription> getChannels() {
        return channels;
    }

}
