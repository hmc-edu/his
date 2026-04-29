package com.hmc.his.repository;

import com.hmc.his.model.Registration;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RegistrationRepository {

    @Select("<script>" +
            "SELECT r.*, p.name AS patient_name, p.gender AS patient_gender, " +
            "       d.name AS doctor_name, dept.name AS dept_name " +
            "FROM registration r " +
            "LEFT JOIN patient p ON p.id = r.patient_id " +
            "LEFT JOIN doctor d ON d.id = r.doctor_id " +
            "LEFT JOIN department dept ON dept.id = r.dept_id " +
            "<where>" +
            "  <if test='regDate != null'> r.reg_date = #{regDate} </if>" +
            "  <if test='status != null and status != \"\"'> AND r.status = #{status} </if>" +
            "  <if test='doctorId != null'> AND r.doctor_id = #{doctorId} </if>" +
            "</where>" +
            " ORDER BY r.id DESC" +
            "</script>")
    List<Registration> selectList(@Param("regDate") LocalDate regDate,
                                  @Param("status") String status,
                                  @Param("doctorId") Long doctorId);

    @Select("SELECT r.*, p.name AS patient_name, p.gender AS patient_gender, " +
            "       d.name AS doctor_name, dept.name AS dept_name " +
            "FROM registration r " +
            "LEFT JOIN patient p ON p.id = r.patient_id " +
            "LEFT JOIN doctor d ON d.id = r.doctor_id " +
            "LEFT JOIN department dept ON dept.id = r.dept_id " +
            "WHERE r.id = #{id}")
    Registration selectById(Long id);

    @Select("SELECT COUNT(*) FROM registration WHERE reg_date = #{regDate}")
    long countByDate(LocalDate regDate);

    @Insert("INSERT INTO registration(reg_no, patient_id, doctor_id, dept_id, reg_date, reg_fee, status) " +
            "VALUES(#{regNo}, #{patientId}, #{doctorId}, #{deptId}, #{regDate}, #{regFee}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Registration r);

    @Update("UPDATE registration SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
