package com.hmc.his.service.impl;

import com.hmc.his.common.BusinessException;
import com.hmc.his.model.Bill;
import com.hmc.his.model.BillItem;
import com.hmc.his.model.Prescription;
import com.hmc.his.model.PrescriptionItem;
import com.hmc.his.repository.BillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * BillServiceImpl 的单元测试。
 *
 * 保护三点核心业务规则：
 *   1. charge 只能对 PENDING 账单操作，refund 只能对 PAID 账单操作（状态机）
 *   2. createFromPrescription 自动生成 bill_no 并按处方同步明细
 *   3. 账单不存在时各方法应抛 BusinessException
 */
@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillServiceImpl billService;

    // ════════════════════ createFromPrescription ════════════════════

    @Test
    @DisplayName("createFromPrescription：自动生成 bill_no、状态 PENDING、金额与处方一致")
    void createFromPrescription_assignsBillNoAndPendingStatus() {
        Prescription p = new Prescription();
        p.setId(300L);
        p.setTotalAmount(new BigDecimal("128.50"));
        p.setItems(List.of(
                itemJson(301L, 101L, 2, "10.00", "20.00"),
                itemJson(302L, 102L, 3, "5.50", "16.50"),
                itemJson(303L, 103L, 1, "92.00", "92.00")
        ));

        when(billRepository.countToday()).thenReturn(0L);
        doAnswer(inv -> {
            ((Bill) inv.getArgument(0)).setId(500L);
            return 1;
        }).when(billRepository).insert(any(Bill.class));

        Bill result = billService.createFromPrescription(p);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("128.50");
        assertThat(result.getPrescriptionId()).isEqualTo(300L);
        assertThat(result.getBillNo()).startsWith("B");

        ArgumentCaptor<Bill> billCaptor = ArgumentCaptor.forClass(Bill.class);
        verify(billRepository).insert(billCaptor.capture());
        Bill inserted = billCaptor.getValue();
        assertThat(inserted.getStatus()).isEqualTo("PENDING");
        assertThat(inserted.getTotalAmount()).isEqualByComparingTo("128.50");

        // 处方有 3 条明细，应逐一转为 3 条账单明细
        ArgumentCaptor<BillItem> itemCaptor = ArgumentCaptor.forClass(BillItem.class);
        verify(billRepository, times(3)).insertItem(itemCaptor.capture());
        List<BillItem> items = itemCaptor.getAllValues();
        assertThat(items).hasSize(3);
    }

    @Test
    @DisplayName("createFromPrescription：处方明细应逐一转为账单明细，金额一致")
    void createFromPrescription_transfersItemsCorrectly() {
        Prescription p = new Prescription();
        p.setId(300L);
        p.setTotalAmount(new BigDecimal("25.50"));
        p.setItems(List.of(
                itemJson(301L, 101L, 2, "10.00", "20.00"),
                itemJson(302L, 102L, 1, "5.50", "5.50")
        ));

        when(billRepository.countToday()).thenReturn(5L);
        doAnswer(inv -> {
            ((Bill) inv.getArgument(0)).setId(501L);
            return 1;
        }).when(billRepository).insert(any(Bill.class));

        billService.createFromPrescription(p);

        ArgumentCaptor<BillItem> captor = ArgumentCaptor.forClass(BillItem.class);
        verify(billRepository, times(2)).insertItem(captor.capture());
        List<BillItem> items = captor.getAllValues();

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getDrugId()).isEqualTo(101L);
        assertThat(items.get(0).getQty()).isEqualTo(2);
        assertThat(items.get(0).getUnitPrice()).isEqualByComparingTo("10.00");
        assertThat(items.get(0).getSubtotal()).isEqualByComparingTo("20.00");
        assertThat(items.get(0).getBillId()).isEqualTo(501L);
        assertThat(items.get(1).getDrugId()).isEqualTo(102L);
        assertThat(items.get(1).getSubtotal()).isEqualByComparingTo("5.50");
    }

    @Test
    @DisplayName("createFromPrescription：序列号从 countToday+1 开始")
    void createFromPrescription_billNoSequential() {
        Prescription p = new Prescription();
        p.setId(100L);
        p.setTotalAmount(BigDecimal.TEN);
        p.setItems(List.of(itemJson(1L, 1L, 1, "10.00", "10.00")));

        when(billRepository.countToday()).thenReturn(3L);
        doAnswer(inv -> {
            ((Bill) inv.getArgument(0)).setId(200L);
            return 1;
        }).when(billRepository).insert(any(Bill.class));

        Bill result = billService.createFromPrescription(p);

        assertThat(result.getBillNo()).matches("B\\d{8}0004");
    }

    // ════════════════════ charge ════════════════════

    @Test
    @DisplayName("charge：PENDING 账单可收费，状态置为 PAID 并记录 paid_at")
    void charge_whenPending_setsPaid() {
        Bill b = new Bill();
        b.setId(1L);
        b.setStatus("PENDING");
        when(billRepository.selectById(1L)).thenReturn(b);

        billService.charge(1L);

        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(billRepository).updateStatus(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.eq("PAID"), timeCaptor.capture());
        assertThat(timeCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("charge：非 PENDING 状态抛异常，不改库")
    void charge_whenNotPending_throws() {
        Bill b = new Bill();
        b.setId(1L);
        b.setStatus("PAID");
        when(billRepository.selectById(1L)).thenReturn(b);

        assertThatThrownBy(() -> billService.charge(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待收费");

        verify(billRepository, never()).updateStatus(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("charge：账单不存在时抛异常")
    void charge_whenNotExists_throws() {
        when(billRepository.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> billService.charge(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("账单不存在");
    }

    // ════════════════════ refund ════════════════════

    @Test
    @DisplayName("refund：PAID 账单可退费，状态置为 REFUNDED")
    void refund_whenPaid_setsRefunded() {
        Bill b = new Bill();
        b.setId(2L);
        b.setStatus("PAID");
        when(billRepository.selectById(2L)).thenReturn(b);

        billService.refund(2L);

        verify(billRepository).updateStatus(2L, "REFUNDED", null);
    }

    @Test
    @DisplayName("refund：非 PAID 状态抛异常，不改库")
    void refund_whenNotPaid_throws() {
        Bill b = new Bill();
        b.setId(2L);
        b.setStatus("REFUNDED");
        when(billRepository.selectById(2L)).thenReturn(b);

        assertThatThrownBy(() -> billService.refund(2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已收费");

        verify(billRepository, never()).updateStatus(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("refund：账单不存在时抛异常")
    void refund_whenNotExists_throws() {
        when(billRepository.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> billService.refund(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("账单不存在");
    }

    // ════════════════════ get ════════════════════

    @Test
    @DisplayName("get：正常返回账单及其明细")
    void get_returnsBillWithItems() {
        Bill b = new Bill();
        b.setId(10L);
        b.setBillNo("B202605080001");
        b.setStatus("PENDING");
        when(billRepository.selectById(10L)).thenReturn(b);

        BillItem item = new BillItem();
        item.setId(1L);
        item.setDrugName("阿司匹林");
        when(billRepository.selectItems(10L)).thenReturn(List.of(item));

        Bill result = billService.get(10L);

        assertThat(result.getBillNo()).isEqualTo("B202605080001");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getDrugName()).isEqualTo("阿司匹林");
    }

    @Test
    @DisplayName("get：账单不存在时抛异常")
    void get_whenNotExists_throws() {
        when(billRepository.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> billService.get(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("账单不存在");
    }

    // ════════════════════ helpers ════════════════════

    private static PrescriptionItem itemJson(Long id, Long drugId, int qty, String price, String subtotal) {
        PrescriptionItem pi = new PrescriptionItem();
        pi.setId(id);
        pi.setDrugId(drugId);
        pi.setQty(qty);
        pi.setUnitPrice(new BigDecimal(price));
        pi.setSubtotal(new BigDecimal(subtotal));
        return pi;
    }
}
