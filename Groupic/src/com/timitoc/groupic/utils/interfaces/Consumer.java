package com.timitoc.groupic.utils.interfaces;

import java.io.Serializable;

/**
 * Created by timi on 25.04.2016.
 */
public interface Consumer<T> extends Serializable{
    void accept(T t);
}
