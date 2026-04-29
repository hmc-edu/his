package com.hmc.his.repository;

import com.hmc.his.model.Visit;
import org.apache.ibatis.annotations.*;

@Mapper
public interface VisitRepository {

    @Select("SELECT * FROM visit WHERE id = #{id}")
    Visit selectById(Long id);

    @Select("SELECT * FROM visit WHERE registration_id = #{registrationId}")
    Visit selectByRegistrationId(Long registrationId);

    @Insert("INSERT INTO visit(registration_id, chief_complaint, present_illness, diagnosis, doctor_advice, visited_at) " +
            "VALUES(#{registrationId}, #{chiefComplaint}, #{presentIllness}, #{diagnosis}, #{doctorAdvice}, #{visitedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Visit v);

    @Update("UPDATE visit SET chief_complaint = #{chiefComplaint}, present_illness = #{presentIllness}, " +
            "diagnosis = #{diagnosis}, doctor_advice = #{doctorAdvice} WHERE id = #{id}")
    int update(Visit v);
}
