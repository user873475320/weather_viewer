package utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    private HibernateUtils() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtils.class) {
                if (sessionFactory == null) {
                    Configuration configuration = new Configuration().configure();
                    configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
                            sessionFactory = configuration.buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }
}
