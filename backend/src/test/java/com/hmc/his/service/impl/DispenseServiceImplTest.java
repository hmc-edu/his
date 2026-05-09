package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.model.Prescription;
import com.hmc.his.model.PrescriptionItem;
import com.hmc.his.repository.DrugRepository;
import com.hmc.his.repository.PrescriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DispenseServiceImpl 的单元测试。
 *
 * 保护发药核心业务规则：
 *   1. 只能发药给 PAID+PENDING 状态的处方（已在待发药队列）
 *   2. 发药时逐条扣减库存，任一药品库存不足则整体失败
 *   3. 全部扣减成功后才置 DISPENSED
 */
@ExtendWith(MockitoExtension.class)
class DispenseServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private DispenseServiceImpl dispenseService;

    // ════════════════════ dispense ════════════════════

    @Test
    @DisplayName("dispense：逐条扣减库存，全部成功后置 DISPENSED")
    void dispense_deductsStockAndMarksDispensed() {
        Prescription p = new Prescription();
        p.setId(100L);

        PrescriptionItem item1 = new PrescriptionItem();
        item1.setDrugId(10L);
        item1.setDrugName("阿司匹林");
        item1.setQty(2);

        PrescriptionItem item2 = new PrescriptionItem();
        item2.setDrugId(20L);
        item2.setDrugName("维生素C");
        item2.setQty(3);

        when(prescriptionRepository.selectDispenseQueue())
                .thenReturn(List.of(p));
        when(prescriptionRepository.selectItems(100L))
                .thenReturn(List.of(item1, item2));
        when(drugRepository.deductStock(anyLong(), anyInt())).thenReturn(1);

        dispenseService.dispense(100L);

        // 逐条扣减
        verify(drugRepository).deductStock(10L, 2);
        verify(drugRepository).deductStock(20L, 3);
        // 最终置 DISPENSED
        verify(prescriptionRepository).updateDispenseStatus(100L, "DISPENSED");
    }

    @Test
    @DisplayName("dispense：处方不在待发药队列则抛异常")
    void dispense_whenNotInQueue_throws() {
        Prescription other = new Prescription();
        other.setId(999L);
        when(prescriptionRepository.selectDispenseQueue())
                .thenReturn(List.of(other));

        assertThatThrownBy(() -> dispenseService.dispense(100L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不在待发药队列");

        // 不应扣库存，也不应更新状态
        verify(drugRepository, never()).deductStock(anyLong(), anyInt());
        verify(prescriptionRepository, never()).updateDispenseStatus(anyLong(), anyString());
    }

    @Test
    @DisplayName("dispense：任一药品库存不足时抛异常，不更新发药状态")
    void dispense_whenStockInsufficient_throwsAndNoStatusUpdate() {
        Prescription p = new Prescription();
        p.setId(200L);

        PrescriptionItem item1 = new PrescriptionItem();
        item1.setDrugId(30L);
        item1.setDrugName("青霉素");
        item1.setQty(5);

        PrescriptionItem item2 = new PrescriptionItem();
        item2.setDrugId(40L);
        item2.setDrugName("葡萄糖");
        item2.setQty(10);

        when(prescriptionRepository.selectDispenseQueue())
                .thenReturn(List.of(p));
        when(prescriptionRepository.selectItems(200L))
                .thenReturn(List.of(item1, item2));
        // 第一个扣减成功，第二个失败
        when(drugRepository.deductStock(30L, 5)).thenReturn(1);
        when(drugRepository.deductStock(40L, 10)).thenReturn(0);

        assertThatThrownBy(() -> dispenseService.dispense(200L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("库存不足");

        // 第一个扣了，第二个也尝试扣了
        verify(drugRepository).deductStock(30L, 5);
        verify(drugRepository).deductStock(40L, 10);
        // 但因为失败，不应更新发药状态
        verify(prescriptionRepository, never()).updateDispenseStatus(anyLong(), anyString());
    }

    // ════════════════════ list ════════════════════

    @Test
    @DisplayName("list：返回待发药队列，每条处方含明细")
    void list_returnsQueueWithItems() {
        Prescription p1 = new Prescription();
        p1.setId(1L);

        Prescription p2 = new Prescription();
        p2.setId(2L);

        when(prescriptionRepository.selectDispenseQueue())
                .thenReturn(List.of(p1, p2));

        PrescriptionItem item = new PrescriptionItem();
        item.setId(1L);
        item.setDrugName("阿司匹林");
        when(prescriptionRepository.selectItems(1L))
                .thenReturn(List.of(item));
        when(prescriptionRepository.selectItems(2L))
                .thenReturn(Collections.emptyList());

        List<Prescription> result = dispenseService.list();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getDrugName()).isEqualTo("阿司匹林");
        assertThat(result.get(1).getItems()).isEmpty();
    }
}
