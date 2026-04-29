package com.hmc.his.repository;

import com.hmc.his.model.Doctor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DoctorRepository {

    @Select("<script>" +
            "SELECT d.*, dept.name AS dept_name " +
            "FROM doctor d LEFT JOIN department dept ON dept.id = d.dept_id " +
            "<where>" +
            "  <if test='deptId != null'> d.dept_id = #{deptId} </if>" +
            "</where>" +
            " ORDER BY d.dept_id ASC, d.id ASC" +
            "</script>")
    List<Doctor> selectAll(@Param("deptId") Long deptId);

    @Select("SELECT d.*, dept.name AS dept_name FROM doctor d " +
            "LEFT JOIN department dept ON dept.id = d.dept_id " +
            "WHERE d.id = #{id}")
    Doctor selectById(Long id);

    @Select("SELECT * FROM doctor WHERE user_id = #{userId} LIMIT 1")
    Doctor selectByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM doctor")
    long countAll();

    @Insert("INSERT INTO doctor(user_id, dept_id, name, title, reg_fee) " +
            "VALUES(#{userId}, #{deptId}, #{name}, #{title}, #{regFee})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Doctor doctor);

    @Update("UPDATE doctor SET user_id=#{userId}, dept_id=#{deptId}, name=#{name}, " +
            "title=#{title}, reg_fee=#{regFee} WHERE id=#{id}")
    int update(Doctor doctor);

    @Delete("DELETE FROM doctor WHERE id = #{id}")
    int deleteById(Long id);
}
