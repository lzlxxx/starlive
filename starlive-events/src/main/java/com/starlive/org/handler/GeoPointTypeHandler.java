package com.starlive.org.handler;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MappedTypes(GeoLocation.class)
public class GeoPointTypeHandler extends BaseTypeHandler<GeoLocation> {

    private static final Pattern POINT_PATTERN = Pattern.compile("POINT\\s*\\(([-0-9.]+)\\s+([-0-9.]+)\\)");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GeoLocation parameter, JdbcType jdbcType) 
            throws SQLException {
        if (parameter.isLatlon()) {
            var latlon = parameter.latlon();
            // 验证纬度范围
            double lat = latlon.lat();
            double lon = latlon.lon();
            
            if (lat < -90 || lat > 90) {
                throw new SQLException("Latitude must be between -90 and 90 degrees");
            }
            if (lon < -180 || lon > 180) {
                throw new SQLException("Longitude must be between -180 and 180 degrees");
            }
            
            ps.setString(i, String.format("POINT(%f %f)", lon, lat)); // 注意：MySQL期望经度在前，纬度在后
        } else if (parameter.isCoords()) {
            throw new SQLException("Coords format is not supported");
        } else {
            throw new SQLException("Unsupported GeoLocation type");
        }
    }

    @Override
    public GeoLocation getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String point = rs.getString(columnName);
        return parsePoint(point);
    }

    @Override
    public GeoLocation getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String point = rs.getString(columnIndex);
        return parsePoint(point);
    }

    @Override
    public GeoLocation getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String point = cs.getString(columnIndex);
        return parsePoint(point);
    }
    
    private GeoLocation parsePoint(String point) throws SQLException {
        if (point == null || point.trim().isEmpty()) {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher(point.trim());
        if (!matcher.matches()) {
            throw new SQLException("Invalid POINT format: " + point);
        }

        try {
            double lon = Double.parseDouble(matcher.group(1)); // 第一个值是经度
            double lat = Double.parseDouble(matcher.group(2)); // 第二个值是纬度
            
            // 验证坐标范围
            if (lat < -90 || lat > 90) {
                throw new SQLException("Latitude must be between -90 and 90 degrees");
            }
            if (lon < -180 || lon > 180) {
                throw new SQLException("Longitude must be between -180 and 180 degrees");
            }
            
            return GeoLocation.of(b -> b.latlon(l -> l
                    .lat(lat)
                    .lon(lon)));
        } catch (NumberFormatException e) {
            throw new SQLException("Failed to parse coordinates: " + point, e);
        }
    }
}