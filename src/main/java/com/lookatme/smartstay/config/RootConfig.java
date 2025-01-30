package com.lookatme.smartstay.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

  @Bean
  public ModelMapper getMapper(){
    ModelMapper modelMapper = new CustomModelMapper();
    modelMapper.getConfiguration()
            .setSkipNullEnabled(true)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
            .setMatchingStrategy(MatchingStrategies.STRICT)
            ;

    return modelMapper;
  }


}
