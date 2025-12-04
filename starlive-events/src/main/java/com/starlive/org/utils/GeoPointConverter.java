package com.starlive.org.utils;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;
import com.vividsolutions.jts.io.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class GeoPointConverter {
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final int outputDimension = 2;
    private static final int BYTE_ORDER = ByteOrderValues.BIG_ENDIAN;

    /**
     * Convert byte array containing SRID + WKB Geometry into GeoLocation object
     */
    public GeoLocation from(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            // Skip SRID
            inputStream.skip(4);
            
            // Read Geometry
            WKBReader wkbReader = new WKBReader(geometryFactory);
            Point point = (Point) wkbReader.read(new InputStreamInStream(inputStream));
            
            // 创建LatLonGeoLocation
            return GeoLocation.of(b -> b.latlon(l -> l
                    .lat(point.getY())
                    .lon(point.getX())));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert bytes to GeoLocation", e);
        }
    }

    /**
     * Convert GeoLocation object into byte array containing SRID + WKB Geometry
     */
    public byte[] to(GeoLocation location) {
        if (location == null) {
            return null;
        }

        double latitude;
        double longitude;

        if (location.isLatlon()) {
            // 处理Latlon类型
            LatLonGeoLocation latLon = location.latlon();
            latitude = latLon.lat();
            longitude = latLon.lon();
        } else if (location.isCoords()) {
            // 处理Coords类型
            List<Double> coords = location.coords();
            if (coords != null && coords.size() >= 2) {
                longitude = coords.get(0);
                latitude = coords.get(1);
            } else {
                throw new IllegalArgumentException("Invalid coordinates");
            }
        } else if (location.isText()) {
            // 处理Text类型
            String[] parts = location.text().split(",");
            if (parts.length >= 2) {
                try {
                    latitude = Double.parseDouble(parts[0]);
                    longitude = Double.parseDouble(parts[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid text coordinates", e);
                }
            } else {
                throw new IllegalArgumentException("Invalid text coordinates format");
            }
        } else {
            throw new IllegalArgumentException("Unsupported GeoLocation type");
        }

        try {
            Coordinate coord = new Coordinate(longitude, latitude);
            Point point = geometryFactory.createPoint(coord);
            point.setSRID(4326); // WGS84

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // Write SRID
            byte[] sridBytes = new byte[4];
            ByteOrderValues.putInt(point.getSRID(), sridBytes, BYTE_ORDER);
            outputStream.write(sridBytes);
            
            // Write Geometry
            WKBWriter wkbWriter = new WKBWriter(outputDimension, BYTE_ORDER);
            wkbWriter.write(point, new OutputStreamOutStream(outputStream));
            
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to convert GeoLocation to bytes", e);
        }
    }
}