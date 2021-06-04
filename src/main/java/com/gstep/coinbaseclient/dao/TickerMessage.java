package com.gstep.coinbaseclient.dao;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TickerMessage {
    private String instrument;
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal last;
    private LocalTime time;
}
