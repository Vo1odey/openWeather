package com.dragunov.openweather.DAO;

import com.dragunov.openweather.models.Location;
import com.dragunov.openweather.models.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LocationRepository {
    private final SessionFactory sessionFactory;
    public LocationRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveLocation(User user, Location location) {
        Transaction tx = null;
        log.info("Start insert location -> {} ", location.getName());
        final String HQL = "SELECT u FROM User u LEFT JOIN FETCH u.locations WHERE u.login = :login";
        log.info("Start insert location -> {}", location);
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            User result = session.createQuery(HQL, User.class).setParameter("login", user.getLogin()).uniqueResult();
            location.setUser(result);
            session.persist(location);
            tx.commit();
            log.info("SUCCESS");
        } catch (HibernateException e) {
            log.error("DAO add location ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }
    public Optional<Location> getLocation(User user, Location location) {
        Transaction tx = null;
        log.info("Start get location <- {} ", location.getName());
        final String HQL = "FROM Location WHERE latitude =:latitude AND longitude =:longitude AND user.id =:id";
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Optional<Location> result = session.createQuery(HQL, Location.class)
                    .setParameter("latitude",location.getLatitude())
                    .setParameter("longitude", location.getLongitude())
                    .setParameter("id", user.getId()).uniqueResultOptional();
            tx.commit();
            log.info("SUCCESS");
            return result;
        } catch (HibernateException e) {
            log.error("DAO location get ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
            return Optional.empty();
        }
    }
    public void removeLocation(Double longitude, Double latitude, User user) {
        Transaction tx = null;
        log.info("Start remove user {} -> ", user.getLogin());
        final String HQL = "DELETE FROM Location WHERE latitude =:latitude AND longitude =:longitude AND user.id =:id";
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.createQuery(HQL)
                    .setParameter("latitude", latitude)
                    .setParameter("longitude", longitude)
                    .setParameter("id", user.getId()).executeUpdate();
            tx.commit();
            log.info("SUCCESS");
        } catch (HibernateException e) {
            log.error("DAO location delete ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }
    public List<Location> index(User user) {
        Transaction tx = null;
        log.info("Start index user <- [] <- {} ", user.getLogin());
        final String HQL = "FROM Location WHERE user.id =:id";
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            List<Location> locations = session.createQuery(HQL, Location.class)
                    .setParameter("id", user.getId()).getResultList();
            tx.commit();
            log.info("SUCCESS");
            return locations;
        } catch (HibernateException e) {
            log.error("DAO index location ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
            return new ArrayList<>();
        }
    }
}
