package org.apereo.cas.audit.spi;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.DigestUtils;
import org.apereo.inspektr.audit.spi.support.ReturnValueAsStringResourceResolver;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This is {@link ShortenedReturnValueAsStringResourceResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class ShortenedReturnValueAsStringResourceResolver extends ReturnValueAsStringResourceResolver {
    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Object retval) {
        final var resources = super.resolveFrom(auditableTarget, retval);
        if (resources != null) {
            return Arrays.stream(resources)
                    .map(DigestUtils::abbreviate)
                    .collect(Collectors.toList())
                    .toArray(new String[]{});
        }
        return resources;
    }
}
