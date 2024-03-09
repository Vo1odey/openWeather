package com.dragunov.openweather.DAO;

import com.dragunov.openweather.models.User;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;

import java.util.Optional;

@Slf4j
public class UserRepository {
    private final SessionFactory sessionFactory;
    public UserRepository (SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveUser(User user) {
        Transaction tx = null;
        log.info("Start insert user -> {} ", user.getLogin());
        try (Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            log.info("SUCCESS");
        } catch (PersistenceException e) {
            log.error("DAO this login ALREADY USING");
            if (tx != null) {
                tx.rollback();
            }
        }
    }
    public Optional<User> getUser(String login) {
        Transaction tx = null;
        log.info("Start get user <- {} ", login);
        final String HQL = "FROM User WHERE login = :login";
        try (Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            Optional<User> result = session.createQuery(HQL, User.class)
                    .setParameter("login", login).uniqueResultOptional();
            tx.commit();
            log.info("SUCCESS");
            return result;
        } catch (HibernateException e) {
            log.error("DAO user get ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
            return Optional.empty();
        }
    }
    public void removeUser(User user) {
        Transaction tx = null;
        log.info("Start remove user {} -> ", user.getLogin());
        final String HQL = "DELETE FROM User WHERE login = :login";
        try (Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.createQuery(HQL).setParameter("login", user.getLogin()).executeUpdate();
            tx.commit();
            log.info("SUCCESS");
        } catch (HibernateException e) {
            log.error("DAO user delete ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }
}
