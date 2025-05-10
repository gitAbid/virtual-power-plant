package com.chellenge.vpp.controller;

import com.chellenge.vpp.service.VppService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public VppService vppService() {
        return Mockito.mock(VppService.class);
    }
}
