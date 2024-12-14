package listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import service.SessionService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionCleaner implements ServletContextListener {
    private SessionService sessionService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        this.sessionService = (SessionService) context.getAttribute("sessionService");

        scheduler.scheduleAtFixedRate(sessionService::deleteExpiredSessions, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdown();
    }
}