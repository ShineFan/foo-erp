package cn.shinefan.fooerp.config;

import cn.shinefan.fooerp.model.DeliveryStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(DeliveryStatus.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class DeliveryStatusTypeHandler extends BaseTypeHandler<DeliveryStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DeliveryStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public DeliveryStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : DeliveryStatus.valueOf(value);
    }

    @Override
    public DeliveryStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : DeliveryStatus.valueOf(value);
    }

    @Override
    public DeliveryStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : DeliveryStatus.valueOf(value);
    }
}
