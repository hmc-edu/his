package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.model.Drug;
import com.hmc.his.repository.DrugRepository;
import com.hmc.his.service.DrugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugServiceImpl implements DrugService {

    private final DrugRepository drugRepository;

    @Override
    public List<Drug> list(String keyword) {
        return drugRepository.selectAll(keyword);
    }

    @Override
    public Drug get(Long id) {
        Drug d = drugRepository.selectById(id);
        if (d == null) throw new BusinessException("药品不存在");
        return d;
    }

    @Override
    public Long create(Drug drug) {
        if (drug.getStock() == null) drug.setStock(0);
        if (drug.getPrice() == null) drug.setPrice(BigDecimal.ZERO);
        if (drug.getUnit() == null || drug.getUnit().isBlank()) drug.setUnit("盒");
        drugRepository.insert(drug);
        return drug.getId();
    }

    @Override
    public void update(Long id, Drug drug) {
        Drug exist = drugRepository.selectById(id);
        if (exist == null) throw new BusinessException("药品不存在");
        drug.setId(id);
        if (drug.getStock() == null) drug.setStock(exist.getStock());
        if (drug.getPrice() == null) drug.setPrice(exist.getPrice());
        drugRepository.update(drug);
    }

    @Override
    public void delete(Long id) {
        drugRepository.deleteById(id);
    }
}
