package com.hmc.his.service;

import com.hmc.his.model.Drug;

import java.util.List;

public interface DrugService {
    List<Drug> list(String keyword);

    Drug get(Long id);

    Long create(Drug drug);

    void update(Long id, Drug drug);

    void delete(Long id);
}
