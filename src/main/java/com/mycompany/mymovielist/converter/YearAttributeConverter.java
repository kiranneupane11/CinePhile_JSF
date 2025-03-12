/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.converter;

/**
 *
 * @author kiran
 */
import java.time.Year;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class YearAttributeConverter implements AttributeConverter<Year, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Year attribute) {
        return (attribute != null) ? attribute.getValue() : null;
    }

    @Override
    public Year convertToEntityAttribute(Integer dbData) {
        return (dbData != null) ? Year.of(dbData) : null;
    }
}

