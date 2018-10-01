package com.ingenico.epay.util;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class RandomStanGenerator implements StanGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
