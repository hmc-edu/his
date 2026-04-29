package com.hmc.his.common;

import lombok.Data;

import java.util.List;

@Data
public class PageRes<T> {
    private long total;
    private List<T> records;

    public static <T> PageRes<T> of(long total, List<T> records) {
        PageRes<T> p = new PageRes<>();
        p.total = total;
        p.records = records;
        return p;
    }
}
