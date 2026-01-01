package com.learn.spring.todoapp.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {
    
    @Override
    public String convertToDatabaseColumn(LocalDate localDate) {
        return localDate == null ? null : localDate.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String dateString) {
        return dateString == null ? null : LocalDate.parse(dateString);
    }
}