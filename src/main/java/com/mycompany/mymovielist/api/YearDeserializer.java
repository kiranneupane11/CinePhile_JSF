/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.api;

/**
 *
 * @author kiran
 */
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.time.Year;

@Provider
public class YearDeserializer extends StdDeserializer<Year> {
    public YearDeserializer() {
        super(Year.class);
    }

    @Override
    public Year deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText().trim();
        return Year.of(Integer.parseInt(text));
    }
}