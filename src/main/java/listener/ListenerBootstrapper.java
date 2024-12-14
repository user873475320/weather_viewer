package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ListenerBootstrapper implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MainServletContextListener mainListener = new MainServletContextListener();
        mainListener.contextInitialized(sce);

        SessionCleaner sessionCleaner = new SessionCleaner();
        sessionCleaner.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SessionCleaner sessionCleaner = new SessionCleaner();
        sessionCleaner.contextDestroyed(sce);

        MainServletContextListener mainListener = new MainServletContextListener();
        mainListener.contextDestroyed(sce);
    }
}
