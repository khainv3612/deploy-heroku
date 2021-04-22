package com.finacial.config;

import com.finacial.model.Status;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigResource {

    @Bean
    public Status statusAccActived() {
        return new Status(1L, 1L, "USER_ACTIVED", "ACCOUNT");
    }

    @Bean
    public Status statusAccNotActived() {
        return new Status(3L, 0L, "USER_NOT_ACTIVED", "ACCOUNT");
    }

    @Bean
    public Status statusAccBanned() {
        return new Status(2L, 2L, "USER_BANNED", "ACCOUNT");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Status sttTokenNotActived(){
        return new Status(6L, 0L, "TOKEN_NOT_ACTIVED", "TOKEN");
    }

    @Bean
    public Status sttTokenActived(){
        return new Status(5L, 1L, "TOKEN_ACTIVED", "TOKEN");
    }

}
