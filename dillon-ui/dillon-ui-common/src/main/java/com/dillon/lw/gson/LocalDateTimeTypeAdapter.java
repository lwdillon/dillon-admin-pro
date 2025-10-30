package com.dillon.lw.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH::mm::ss");

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType,
                                     JsonSerializationContext context) {

//            return new JsonPrimitive(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT,
                                         JsonDeserializationContext context) throws JsonParseException {


            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(json.getAsLong()), ZoneId.systemDefault());
            return localDateTime;
        }
    }