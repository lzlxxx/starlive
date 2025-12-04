package com.starlive.org.config;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class GeoLocationJsonSerializer extends StdSerializer<GeoLocation> {

    public GeoLocationJsonSerializer() {
        this(null);
    }

    public GeoLocationJsonSerializer(Class<GeoLocation> t) {
        super(t);
    }

    @Override
    public void serialize(GeoLocation value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        if (value.isLatlon()) {
            gen.writeNumberField("lat", value.latlon().lat());
            gen.writeNumberField("lon", value.latlon().lon());
        } else if (value.isCoords()) {
            gen.writeArrayFieldStart("coordinates");
            gen.writeNumber(value.coords().get(0));
            gen.writeNumber(value.coords().get(1));
            gen.writeEndArray();
        }
        gen.writeEndObject();
    }
} 