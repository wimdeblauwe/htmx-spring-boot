package io.github.wimdeblauwe.htmx.spring.boot.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HxLocationBoostedRedirectStrategyTest {

    @Test
    void shouldEscapeNonLatin1CharactersInBoostedLocation() {
        var strategy = new HxLocationBoostedRedirectStrategy();

        String location = strategy.boosted("/users/Łukasz");

        assertThat(location)
                .contains("\"path\":\"/users/\\u0141ukasz\"")
                .isASCII();
    }

}
