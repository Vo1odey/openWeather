package com.dragunov.openweather.utils;

import com.dragunov.openweather.models.Location;
import com.dragunov.openweather.models.Sessions;
import com.dragunov.openweather.models.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
@Slf4j
public class HibernateUtil {

    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory(){
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
                configuration.addAnnotatedClass(User.class).addAnnotatedClass(Location.class)
                        .addAnnotatedClass(Sessions.class);
                sessionFactory = configuration.buildSessionFactory();
                log.info("sessionFactory build success");
            } catch (HibernateException e) {
                log.error("sessionFactory build error");
            }
        }
        return sessionFactory;
    }
}
