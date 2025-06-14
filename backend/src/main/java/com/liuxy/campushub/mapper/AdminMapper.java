package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDateTime;

@Mapper
public interface AdminMapper {
    
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin findById(@Param("id") Integer id);
    
    @Update("UPDATE admin SET last_login = #{lastLogin}, login_ip = #{loginIp} WHERE id = #{id}")
    void updateLoginInfo(@Param("id") Integer id, 
                        @Param("lastLogin") LocalDateTime lastLogin,
                        @Param("loginIp") String loginIp);
} 