package com.weaver.teams.security.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.util.Assert;

public class TeamsSessionRegistry extends SessionRegistryImpl{

    /** <principal:Object,SessionIdSet> */
    private final ConcurrentMap<Object,Set<String>> principals = new ConcurrentHashMap<Object,Set<String>>();
    /** <sessionId:Object,SessionInformation> */
    private final Map<String, TeamsSessionInformation> sessionIds = new ConcurrentHashMap<String, TeamsSessionInformation>();

    //~ Methods ========================================================================================================

    @Override
	public List<Object> getAllPrincipals() {
        return new ArrayList<Object>(principals.keySet());
    }

    @Override
	public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        final Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            return Collections.emptyList();
        }

        List<SessionInformation> list = new ArrayList<SessionInformation>(sessionsUsedByPrincipal.size());

        for (String sessionId : sessionsUsedByPrincipal) {
            SessionInformation sessionInformation = getSessionInformation(sessionId);

            if (sessionInformation == null) {
                continue;
            }

            if (includeExpiredSessions || !sessionInformation.isExpired()) {
                list.add(sessionInformation);
            }
        }

        return list;
    }

    @Override
	public SessionInformation getSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        return sessionIds.get(sessionId);
    }
    public TeamsSessionInformation getTeamsSessionInformation(String sessionId) {
    	return sessionIds.get(sessionId);
    }

    @Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
        String sessionId = event.getId();
        removeSessionInformation(sessionId);
    }

    @Override
	public void refreshLastRequest(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        SessionInformation info = getSessionInformation(sessionId);

        if (info != null) {
            info.refreshLastRequest();
        }
    }

	public void registerNewSession(HttpSession session, Object principal) {
        Assert.notNull(principal, "Principal required as per interface contract");

        if (logger.isDebugEnabled()) {
            logger.debug("Registering session " + session.getId() +", for principal " + principal);
        }

        if (getSessionInformation(session.getId()) != null) {
            removeSessionInformation(session.getId());
        }

        sessionIds.put(session.getId(), new TeamsSessionInformation(principal, session, new Date()));

        Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            sessionsUsedByPrincipal = new CopyOnWriteArraySet<String>();
            Set<String> prevSessionsUsedByPrincipal = principals.putIfAbsent(principal,
                    sessionsUsedByPrincipal);
            if (prevSessionsUsedByPrincipal != null) {
                sessionsUsedByPrincipal = prevSessionsUsedByPrincipal;
            }
        }

        sessionsUsedByPrincipal.add(session.getId());

        if (logger.isTraceEnabled()) {
            logger.trace("Sessions used by '" + principal + "' : " + sessionsUsedByPrincipal);
        }
    }

	 @Override
    public void registerNewSession(String sessionId, Object principal) {

    }

    @Override
	public void removeSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        SessionInformation info = getSessionInformation(sessionId);

        if (info == null) {
            return;
        }

        if (logger.isTraceEnabled()) {
            logger.debug("Removing session " + sessionId + " from set of registered sessions");
        }

        sessionIds.remove(sessionId);

        Set<String> sessionsUsedByPrincipal = principals.get(info.getPrincipal());

        if (sessionsUsedByPrincipal == null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Removing session " + sessionId + " from principal's set of registered sessions");
        }

        sessionsUsedByPrincipal.remove(sessionId);

        if (sessionsUsedByPrincipal.isEmpty()) {
            // No need to keep object in principals Map anymore
            if (logger.isDebugEnabled()) {
                logger.debug("Removing principal " + info.getPrincipal() + " from registry");
            }
            principals.remove(info.getPrincipal());
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Sessions used by '" + info.getPrincipal() + "' : " + sessionsUsedByPrincipal);
        }
    }


}
