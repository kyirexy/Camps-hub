package com.liuxy.campushub.handler;

import com.liuxy.campushub.enums.BountyStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(BountyStatusEnum.class)
public class BountyStatusEnumTypeHandler extends BaseTypeHandler<BountyStatusEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BountyStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public BountyStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : BountyStatusEnum.fromCode(code);
    }

    @Override
    public BountyStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : BountyStatusEnum.fromCode(code);
    }

    @Override
    public BountyStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : BountyStatusEnum.fromCode(code);
    }
} 