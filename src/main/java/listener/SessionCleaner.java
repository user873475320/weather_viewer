package listener;

import service.SessionService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class SessionCleaner implements ServletContextListener {
    private final SessionService sessionService = new SessionService();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler.scheduleAtFixedRate(sessionService::deleteExpiredSessions, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdown();
    }
}