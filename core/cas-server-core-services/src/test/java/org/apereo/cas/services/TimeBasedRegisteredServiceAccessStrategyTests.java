package org.apereo.cas.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * The {@link TimeBasedRegisteredServiceAccessStrategyTests} is responsible for
 * running test cases for {@link TimeBasedRegisteredServiceAccessStrategy}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
public class TimeBasedRegisteredServiceAccessStrategyTests {

    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "timeBasedRegisteredServiceAccessStrategy.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void checkAuthorizationByRangePass() {
        final var authz =
                new TimeBasedRegisteredServiceAccessStrategy(true, true);
        authz.setStartingDateTime(ZonedDateTime.now(ZoneOffset.UTC).toString());
        authz.setEndingDateTime(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(10).toString());
        assertTrue(authz.isServiceAccessAllowed());

    }

    @Test
    public void checkAuthorizationByRangeFailStartTime() {
        final var authz =
                new TimeBasedRegisteredServiceAccessStrategy(true, true);
        authz.setStartingDateTime(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1).toString());
        authz.setEndingDateTime(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(10).toString());
        assertFalse(authz.isServiceAccessAllowed());

    }

    @Test
    public void checkAuthorizationByRangePassEndTime() {
        final var authz =
                new TimeBasedRegisteredServiceAccessStrategy(true, true);
        authz.setStartingDateTime(ZonedDateTime.now(ZoneOffset.UTC).toString());
        authz.setEndingDateTime(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(30).toString());
        assertTrue(authz.isServiceAccessAllowed());
    }

    @Test
    public void verifySerializeATimeBasedRegisteredServiceAccessStrategyToJson() throws IOException {
        final var authWritten =
                new TimeBasedRegisteredServiceAccessStrategy(true, true);

        MAPPER.writeValue(JSON_FILE, authWritten);

        final RegisteredServiceAccessStrategy credentialRead = MAPPER.readValue(JSON_FILE, TimeBasedRegisteredServiceAccessStrategy.class);

        assertEquals(authWritten, credentialRead);
    }
}
