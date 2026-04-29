package com.hmc.his.repository;

import com.hmc.his.model.Drug;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DrugRepository {

    @Select("<script>" +
            "SELECT * FROM drug " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    (code LIKE CONCAT('%', #{keyword}, '%') OR name LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            " ORDER BY id ASC" +
            "</script>")
    List<Drug> selectAll(@Param("keyword") String keyword);

    @Select("SELECT * FROM drug WHERE id = #{id}")
    Drug selectById(Long id);

    @Insert("INSERT INTO drug(code, name, spec, unit, price, stock) " +
            "VALUES(#{code}, #{name}, #{spec}, #{unit}, #{price}, #{stock})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Drug drug);

    @Update("UPDATE drug SET code=#{code}, name=#{name}, spec=#{spec}, unit=#{unit}, " +
            "price=#{price}, stock=#{stock} WHERE id=#{id}")
    int update(Drug drug);

    @Delete("DELETE FROM drug WHERE id = #{id}")
    int deleteById(Long id);
}
