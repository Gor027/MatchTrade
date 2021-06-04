package com.gstep.coinbaseclient.dao;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Subscribe {
    private final static String TYPE = "subscribe";

    private String type = TYPE;
    private String[] product_ids;
    private Channel[] channels;

    public Subscribe(String[] product_ids, Channel[] channels) {
        this.product_ids = product_ids;
        this.channels = channels;
    }
}
