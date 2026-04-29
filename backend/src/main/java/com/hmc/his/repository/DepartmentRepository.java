package com.hmc.his.repository;

import com.hmc.his.model.Department;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 纯注解 MyBatis 写法示例（与 PatientRepository 的 XML 写法对照）。
 * 适合简单 CRUD；复杂动态 SQL 推荐使用 XML（参见 PatientRepository）。
 */
@Mapper
public interface DepartmentRepository {

    @Select("SELECT * FROM department ORDER BY sort ASC, id ASC")
    List<Department> selectAll();

    @Select("SELECT * FROM department WHERE id = #{id}")
    Department selectById(Long id);

    @Insert("INSERT INTO department(code, name, sort) VALUES(#{code}, #{name}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Department dept);

    @Update("UPDATE department SET code=#{code}, name=#{name}, sort=#{sort} WHERE id=#{id}")
    int update(Department dept);

    @Delete("DELETE FROM department WHERE id = #{id}")
    int deleteById(Long id);
}
