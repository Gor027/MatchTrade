package com.gstep.coinbaseclient.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Channel {
    private String name;
    private String[] product_ids;
}
