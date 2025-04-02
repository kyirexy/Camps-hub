package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.StudentUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface StudentUserMapper {

    @Select("SELECT * FROM student_user WHERE user_id = #{userId}")
    @ResultMap("userResultMap")
    StudentUser selectById(@Param("userId") Long userId);

    @Results(id = "userResultMap", value = {
            @Result(property = "userId", column = "user_id", id = true),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "studentNumber", column = "student_number"),
            @Result(property = "collegeId", column = "college_id"),
            @Result(property = "collegeName", column = "college_name"),
            @Result(property = "userRole", column = "user_role"),
            @Result(property = "registerTime", column = "register_time"),
            @Result(property = "lastLogin", column = "last_login"),
            @Result(property = "avatarUrl", column = "avatar_url"),
            @Result(property = "jwPassword", column = "jw_password"),
            @Result(property = "creditScore", column = "credit_score")
    })
    @Select("SELECT * FROM student_user WHERE username = #{username}")
    StudentUser findByUsername(@Param("username") String username);

    @Insert("INSERT INTO student_user (" +
            "username, password, real_name, student_number, gender, phone, email, " +
            "college_id, college_name, major, grade, user_role, register_time, last_login, " +
            "avatar_url, bio, status, jw_password, credit_score) " +
            "VALUES (" +
            "#{username}, #{password}, #{realName}, #{studentNumber}, #{gender}, #{phone}, #{email}, " +
            "#{collegeId}, #{collegeName}, #{major}, #{grade}, #{userRole}, #{registerTime}, #{lastLogin}, " +
            "#{avatarUrl}, #{bio}, #{status}, #{jwPassword}, #{creditScore})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(StudentUser studentUser);

    @Select("SELECT * FROM student_user WHERE student_number = #{studentNumber}")
    @ResultMap("userResultMap")
    StudentUser findByStudentNumber(@Param("studentNumber") String studentNumber);

    @Select("SELECT * FROM student_user WHERE phone = #{phone}")
    @ResultMap("userResultMap")
    StudentUser findByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM student_user WHERE email = #{email}")
    @ResultMap("userResultMap")
    StudentUser findByEmail(@Param("email") String email);

    @Select("SELECT * FROM student_user WHERE username = #{loginId} OR student_number = #{loginId} OR phone = #{loginId}")
    @ResultMap("userResultMap")
    StudentUser findByLoginId(@Param("loginId") String loginId);

    @Update("UPDATE student_user SET " +
            "real_name = #{realName}, " +
            "phone = #{phone}, " +
            "email = #{email}, " +
            "bio = #{bio}, " +
            "password = #{password} " +
            "WHERE user_id = #{userId}")
    int update(StudentUser studentUser);
}