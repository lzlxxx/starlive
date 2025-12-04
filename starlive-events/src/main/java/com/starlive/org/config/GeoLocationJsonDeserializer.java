package com.starlive.org.config;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class GeoLocationJsonDeserializer extends StdDeserializer<GeoLocation> {

    public GeoLocationJsonDeserializer() {
        this(null);
    }

    public GeoLocationJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GeoLocation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        if (node.has("lat") && node.has("lon")) {
            double lat = node.get("lat").asDouble();
            double lon = node.get("lon").asDouble();
            return GeoLocation.of(b -> b.latlon(l -> l.lat(lat).lon(lon)));
        } else if (node.has("coordinates")) {
            JsonNode coords = node.get("coordinates");
            return GeoLocation.of(b -> b.coords(Arrays.asList(
                coords.get(0).asDouble(),
                coords.get(1).asDouble()
            )));
        }
        
        throw new IOException("Invalid GeoLocation format");
    }
} 