package com.liuxy.campushub.handler;

import com.liuxy.campushub.enums.CommentStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 评论状态枚举类型处理器
 *
 * @author liuxy
 * @since 2024-04-21
 */
@MappedTypes(CommentStatusEnum.class)
public class CommentStatusEnumTypeHandler extends BaseTypeHandler<CommentStatusEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CommentStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public CommentStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : CommentStatusEnum.fromCode(code);
    }

    @Override
    public CommentStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : CommentStatusEnum.fromCode(code);
    }

    @Override
    public CommentStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : CommentStatusEnum.fromCode(code);
    }
} 