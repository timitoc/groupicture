package com.timitoc.groupic.utils.interfaces;

/**
 * Created by timi on 12.07.2016.
 */
public interface ServerStatusCallback {
    /**
     *
     * @param status Status of server response. Should be either success or failure
     * @param detail If available gives details about the reason of failure
     */
    void onStatus(String status, String detail);
}
