package com.hmc.his.repository;

import com.hmc.his.model.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * XML Mapper 写法示例（与 DepartmentRepository 的注解写法对照）。
 * SQL 全部写在 resources/mappers/PatientMapper.xml 中。
 */
@Mapper
public interface PatientRepository {

    List<Patient> selectPage(@Param("keyword") String keyword,
                             @Param("offset") int offset,
                             @Param("size") int size);

    long countPage(@Param("keyword") String keyword);

    Patient selectById(Long id);

    int insert(Patient patient);

    int update(Patient patient);

    int deleteById(Long id);
}
