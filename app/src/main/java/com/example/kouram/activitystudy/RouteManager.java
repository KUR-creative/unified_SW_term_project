package com.example.kouram.activitystudy;

/**
 * Created by Ko U Ram on 2017-06-16.
 */

public class RouteManager {
    boolean hasConstructingPath = false;

    public void createNewPath() {
        if(hasConstructingPath) {
            throw new RuntimeException("createNewPath cannot be duplicated in same time!" +
                                    "you need to end job for current path.");
        }
        hasConstructingPath = true;
    }

    public void discardCurrentPath() {
        hasConstructingPath = false;
    }
}
