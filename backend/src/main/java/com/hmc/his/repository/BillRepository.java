package com.hmc.his.repository;

import com.hmc.his.model.Bill;
import com.hmc.his.model.BillItem;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BillRepository {

    @Insert("INSERT INTO bill(bill_no, prescription_id, total_amount, status) " +
            "VALUES(#{billNo}, #{prescriptionId}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Bill b);

    @Insert("INSERT INTO bill_item(bill_id, prescription_item_id, drug_id, qty, unit_price, subtotal) " +
            "VALUES(#{billId}, #{prescriptionItemId}, #{drugId}, #{qty}, #{unitPrice}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertItem(BillItem item);

    @Select("SELECT * FROM bill WHERE id = #{id}")
    Bill selectById(Long id);

    @Select("SELECT * FROM bill WHERE prescription_id = #{prescriptionId}")
    Bill selectByPrescriptionId(Long prescriptionId);

    @Select("<script>" +
            "SELECT b.*, p.name AS patient_name, p.gender AS patient_gender, " +
            "       d.name AS doctor_name, r.reg_no " +
            "FROM bill b " +
            "LEFT JOIN prescription pr ON pr.id = b.prescription_id " +
            "LEFT JOIN visit v ON v.id = pr.visit_id " +
            "LEFT JOIN registration r ON r.id = v.registration_id " +
            "LEFT JOIN patient p ON p.id = r.patient_id " +
            "LEFT JOIN doctor d ON d.id = r.doctor_id " +
            "<where>" +
            "  <if test='status != null and status != \"\"'> b.status = #{status} </if>" +
            "</where>" +
            " ORDER BY b.id DESC" +
            "</script>")
    List<Bill> selectList(@Param("status") String status);

    @Select("SELECT bi.*, dr.name AS drug_name, dr.spec AS drug_spec, dr.unit AS drug_unit " +
            "FROM bill_item bi LEFT JOIN drug dr ON dr.id = bi.drug_id " +
            "WHERE bi.bill_id = #{billId} ORDER BY bi.id ASC")
    List<BillItem> selectItems(Long billId);

    @Update("UPDATE bill SET status = #{status}, paid_at = #{paidAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("paidAt") LocalDateTime paidAt);

    @Select("SELECT COUNT(*) FROM bill WHERE DATE(created_at) = CURDATE()")
    long countToday();
}
