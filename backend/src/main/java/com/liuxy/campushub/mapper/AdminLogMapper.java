package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.AdminLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminLogMapper {
    
    @Insert("INSERT INTO admin_log (admin_id, action, target, ip, detail) " +
            "VALUES (#{adminId}, #{action}, #{target}, #{ip}, #{detail})")
    void insert(AdminLog adminLog);
} 