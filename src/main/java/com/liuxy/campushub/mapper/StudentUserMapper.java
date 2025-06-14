package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.StudentUser;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

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
            @Result(property = "avatarImageId", column = "avatar_image_id"),
            @Result(property = "jwPassword", column = "jw_password"),
            @Result(property = "creditScore", column = "credit_score")
    })
    @Select("SELECT * FROM student_user WHERE username = #{username}")
    StudentUser findByUsername(@Param("username") String username);

    @Insert("INSERT INTO student_user (" +
            "username, password, real_name, student_number, gender, phone, email, " +
            "college_id, college_name, major, grade, user_role, register_time, last_login, " +
            "avatar_image_id, bio, status, jw_password, credit_score) " +
            "VALUES (" +
            "#{username}, #{password}, #{realName}, #{studentNumber}, #{gender}, #{phone}, #{email}, " +
            "#{collegeId}, #{collegeName}, #{major}, #{grade}, #{userRole}, #{registerTime}, #{lastLogin}, " +
            "#{avatarImageId}, #{bio}, #{status}, #{jwPassword}, #{creditScore})")
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

    @Select("<script>SELECT * FROM student_user WHERE 1=1" +
            "<when test='username!=null'> AND username LIKE CONCAT('%',#{username},'%')</when>" +
            "<when test='status!=null'> AND status = #{status}</when>" +
            "<when test='realName!=null'> AND real_name LIKE CONCAT('%',#{realName},'%')</when>" +
            "<when test='studentNumber!=null'> AND student_number LIKE CONCAT('%',#{studentNumber},'%')</when>" +
            "</script>")
    @ResultMap("userResultMap")
    List<StudentUser> selectByCondition(@Param("username") String username, @Param("status") Integer status,
                                      @Param("realName") String realName, @Param("studentNumber") String studentNumber);

    @Select("SELECT * FROM student_user WHERE username = #{loginId} OR student_number = #{loginId} OR phone = #{loginId}")
    @ResultMap("userResultMap")
    StudentUser findByLoginId(@Param("loginId") String loginId);

    @Update("UPDATE student_user SET " +
            "real_name = #{realName}, " +
            "phone = #{phone}, " +
            "email = #{email}, " +
            "bio = #{bio}, " +
            "password = #{password}, " +
            "avatar_image_id = #{avatarImageId} " +
            "WHERE user_id = #{userId}")
    int update(StudentUser studentUser);

    @Select("SELECT COUNT(*) FROM student_user")
    Integer countTotalUsers();

    @Select("SELECT COUNT(*) FROM student_user WHERE register_time >= #{startTime}")
    Integer countNewUsersAfter(@Param("startTime") LocalDateTime startTime);

    @Select("SELECT COUNT(DISTINCT user_id) FROM student_user WHERE last_login >= #{startTime}")
    Integer countActiveUsersAfter(@Param("startTime") LocalDateTime startTime);

    @Select("SELECT COUNT(*) FROM student_user WHERE status = #{status}")
    Integer countUsersByStatus(@Param("status") Integer status);
    
    @Select("SELECT * FROM student_user")
    @ResultMap("userResultMap")
    List<StudentUser> selectAll();
}