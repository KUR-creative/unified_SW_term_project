package com.example.kouram.activitystudy;

import java.io.Serializable;

/**
 * Created by Ko U Ram on 2017-06-23.
 */

public class Tuple<X, Y> implements Serializable {
    public final X left;
    public final Y right;
    public Tuple(X l, Y r) {
        this.left = l;
        this.right = r;
    }
}
