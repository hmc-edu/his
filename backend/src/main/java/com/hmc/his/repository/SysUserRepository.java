package com.hmc.his.repository;

import com.hmc.his.model.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserRepository {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    SysUser selectById(Long id);

    @Select("<script>" +
            "SELECT * FROM sys_user " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    (username LIKE CONCAT('%', #{keyword}, '%') OR real_name LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            " ORDER BY id ASC LIMIT #{offset}, #{size}" +
            "</script>")
    List<SysUser> selectPage(@Param("keyword") String keyword,
                             @Param("offset") int offset,
                             @Param("size") int size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM sys_user " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    (username LIKE CONCAT('%', #{keyword}, '%') OR real_name LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            "</script>")
    long countPage(@Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM sys_user")
    long countAll();

    @Insert("INSERT INTO sys_user(username, password, real_name, role, enabled) " +
            "VALUES(#{username}, #{password}, #{realName}, #{role}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("UPDATE sys_user SET real_name = #{realName}, role = #{role}, enabled = #{enabled} WHERE id = #{id}")
    int update(SysUser user);

    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);
}
