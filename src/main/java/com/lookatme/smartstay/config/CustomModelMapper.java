package com.lookatme.smartstay.config;

import org.modelmapper.ModelMapper;

public class CustomModelMapper extends ModelMapper {

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        if (source == null) {
            return null; // null 반환
        }

        return super.map(source, destinationType);
    }
}
