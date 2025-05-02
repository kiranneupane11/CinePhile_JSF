/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.converter;
import java.time.Year;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.enterprise.inject.Vetoed;
/**
 *
 * @author kiran
 */
@FacesConverter(value = "yearConverter", forClass = Year.class)
@Vetoed
public class YearConverter implements Converter<Year> {

    @Override
    public Year getAsObject(FacesContext ctx, UIComponent comp, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Year.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new javax.faces.convert.ConverterException(
                "Invalid year: " + value, e
            );
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Year year) {
        return (year == null ? "" : String.valueOf(year.getValue()));
    }
}
