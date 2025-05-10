package com.liuxy.campushub.handler;

import com.liuxy.campushub.enums.PostTypeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(PostTypeEnum.class)
public class PostTypeEnumTypeHandler extends BaseTypeHandler<PostTypeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PostTypeEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public PostTypeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : PostTypeEnum.fromCode(code);
    }

    @Override
    public PostTypeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : PostTypeEnum.fromCode(code);
    }

    @Override
    public PostTypeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : PostTypeEnum.fromCode(code);
    }
} 