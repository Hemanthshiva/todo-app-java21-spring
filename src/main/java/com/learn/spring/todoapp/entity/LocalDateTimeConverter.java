package com.learn.spring.todoapp.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {
    
    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toString();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dateString) {
        return dateString == null ? null : LocalDateTime.parse(dateString);
    }
}
