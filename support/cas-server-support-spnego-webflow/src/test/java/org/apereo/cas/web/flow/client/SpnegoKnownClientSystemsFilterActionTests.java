package org.apereo.cas.web.flow.client;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.RegexUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.Assert.*;

/**
 * Test cases for {@link BaseSpnegoKnownClientSystemsFilterAction}
 * and {@link HostNameSpnegoKnownClientSystemsFilterAction}.
 *
 * @author Sean Baker sean.baker@usuhs.edu
 * @author Misagh Moayyed
 * @since 4.1
 */
@Slf4j
public class SpnegoKnownClientSystemsFilterActionTests {

    private static final String ALTERNATE_REMOTE_IP = "74.125.136.102";

    @Test
    public void ensureRemoteIpShouldBeChecked() {
        final var action =
        new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^192\\.158\\..+"), "", 0);

        final var ctx = new MockRequestContext();
        final var req = new MockHttpServletRequest();
        req.setRemoteAddr("192.158.5.781");
        final var extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        final var ev = action.doExecute(ctx);
        assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureRemoteIpShouldNotBeChecked() {
        final var action =
            new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^192\\.158\\..+"),
                "", 0);

        final var ctx = new MockRequestContext();
        final var req = new MockHttpServletRequest();
        req.setRemoteAddr("193.158.5.781");
        final var extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        final var ev = action.doExecute(ctx);
        assertNotEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureAltRemoteIpHeaderShouldBeChecked() {
        final var action =
            new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^74\\.125\\..+"),
                "alternateRemoteIp", 120);

        final var ctx = new MockRequestContext();
        final var req = new MockHttpServletRequest();
        req.setRemoteAddr("555.555.555.555");
        req.addHeader("alternateRemoteIp", ALTERNATE_REMOTE_IP);
        final var extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        final var ev = action.doExecute(ctx);
        assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureHostnameShouldDoSpnego() {
        final var action =
            new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern(""),
                "", 0, "\\w+\\.\\w+\\.\\w+");

        final var ctx = new MockRequestContext();
        final var req = new MockHttpServletRequest();
        req.setRemoteAddr(ALTERNATE_REMOTE_IP);
        final var extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        final var ev = action.doExecute(ctx);
        assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());
    }

    @Test
    public void ensureHostnameAndIpShouldDoSpnego() {
        final var action =
            new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("74\\..+"),
                "", 0, "\\w+\\.\\w+\\.\\w+");

        final var ctx = new MockRequestContext();
        final var req = new MockHttpServletRequest();
        req.setRemoteAddr(ALTERNATE_REMOTE_IP);
        final var extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        final var ev = action.doExecute(ctx);
        assertEquals(ev.getId(), new EventFactorySupport().yes(this).getId());

    }

    @Test
    public void verifyIpMismatchWhenCheckingHostnameForSpnego() {
        final var action =
            new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("14\\..+"),
                "", 0, "\\w+\\.\\w+\\.\\w+");

        final var ctx = new MockRequestContext();
        final var req = new MockHttpServletRequest();
        req.setRemoteAddr(ALTERNATE_REMOTE_IP);
        final var extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        final var ev = action.doExecute(ctx);
        assertEquals(ev.getId(), new EventFactorySupport().no(this).getId());

    }
}
