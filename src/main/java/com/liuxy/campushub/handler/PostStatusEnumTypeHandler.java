package com.liuxy.campushub.handler;

import com.liuxy.campushub.entity.PostStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostStatusEnum 类型处理器
 */
@MappedTypes(PostStatusEnum.class)
public class PostStatusEnumTypeHandler extends BaseTypeHandler<PostStatusEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PostStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public PostStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : PostStatusEnum.fromCode(value);
    }

    @Override
    public PostStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : PostStatusEnum.fromCode(value);
    }

    @Override
    public PostStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : PostStatusEnum.fromCode(value);
    }
}
