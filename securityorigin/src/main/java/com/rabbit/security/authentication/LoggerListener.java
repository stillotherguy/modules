package com.weaver.teams.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;

public class LoggerListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LoggerListener.class);

    private boolean logInteractiveAuthenticationSuccessEvents = true;

    @Override
	public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (!logInteractiveAuthenticationSuccessEvents && event instanceof InteractiveAuthenticationSuccessEvent) {
            return;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("Authentication event:");
        builder.append(event.getAuthentication().getName());
        builder.append("; details: ");
        builder.append(event.getAuthentication().getDetails());

        if (event instanceof AbstractAuthenticationFailureEvent) {
            builder.append("; exception: ");
            builder.append(((AbstractAuthenticationFailureEvent) event).getException().getMessage());
        }

        logger.info(builder.toString());
    }

    public boolean isLogInteractiveAuthenticationSuccessEvents() {
        return logInteractiveAuthenticationSuccessEvents;
    }

    public void setLogInteractiveAuthenticationSuccessEvents(
            boolean logInteractiveAuthenticationSuccessEvents) {
        this.logInteractiveAuthenticationSuccessEvents = logInteractiveAuthenticationSuccessEvents;
    }
}
