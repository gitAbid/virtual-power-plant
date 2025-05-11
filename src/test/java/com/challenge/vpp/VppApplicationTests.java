package com.challenge.vpp;

import com.challenge.vpp.config.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VppApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }

}
