package com.dillon.lw.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public JsonElement serialize(final LocalDate date, final Type typeOfSrc,
                                     final JsonSerializationContext context) {
            return new JsonPrimitive(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }

        @Override
        public LocalDate deserialize(final JsonElement json, final Type typeOfT,
                                     final JsonDeserializationContext context) throws JsonParseException {

            LocalDate localDate = Instant.ofEpochMilli(json.getAsLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return localDate;
        }
    }