package it.units.sim.yourtube.model;

import java.io.Serializable;
import java.util.List;

public class CloudBackupObject implements Serializable {

    private final List<Category> categories;
    private final long backupTimeInMilliseconds;

    public CloudBackupObject() {
        this(null, 0);
        // empty method required by Serializable interface
    }

    public CloudBackupObject(List<Category> categories, long backupTimeInMilliseconds) {
        this.categories = categories;
        this.backupTimeInMilliseconds = backupTimeInMilliseconds;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public long getBackupTimeInMilliseconds() {
        return backupTimeInMilliseconds;
    }

}
