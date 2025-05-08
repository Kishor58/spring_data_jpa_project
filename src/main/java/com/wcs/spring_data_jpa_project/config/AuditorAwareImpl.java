package com.wcs.spring_data_jpa_project.config;


import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Static name for demo purpose since there's no security
        return Optional.of("admin-user");
    }
}
