package com.dragunov.openweather.DAO;

import com.dragunov.openweather.models.Sessions;
import com.dragunov.openweather.models.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

@Slf4j
public class SessionRepository {
    private final SessionFactory sessionFactory;
    public SessionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    public void saveCurrentSession(String login, Sessions currentSession) {
        Transaction tx = null;
        log.info("Start insert session -> {} ", login);
        final String HQL = "SELECT u FROM User u LEFT JOIN FETCH u.sessions WHERE u.login = :login";
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            User user = session.createQuery(HQL, User.class).setParameter("login", login).uniqueResult();
            currentSession.setUser(user);
            session.persist(currentSession);
            tx.commit();
            log.info("SUCCESS");
        } catch (HibernateException e) {
            log.error("DAO userSession add ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }
    public Optional<Sessions> getSessions(String sessionID) {
        Transaction tx = null;
        log.info("Start get session <- {} ", sessionID);
        final String HQL = "FROM Sessions WHERE id = :sessionID";
        try (Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            Optional<Sessions> sessions = session.createQuery(HQL, Sessions.class)
                    .setParameter("sessionID", sessionID).uniqueResultOptional();
            tx.commit();
            log.info("SUCCESS");
            return sessions;
        } catch (HibernateException e) {
            log.error("DAO userSession get ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
            return Optional.empty();
        }
    }
    public void removeSession(User user) {
        Transaction tx = null;
        log.info("Start remove session {} -> ", user.getLogin());
        final String HQL = "DELETE FROM Sessions WHERE user.id =:id";
        try (Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.createQuery(HQL).setParameter("id", user.getId()).executeUpdate();
            tx.commit();
            log.info("DAO userSession delete SUCCESS");
        } catch (HibernateException e) {
            log.error("DAO userSession delete ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    public void removeSessionById(String id) {
        Transaction tx = null;
        log.info("Start remove session {} -> ", id);
        final String HQL = "DELETE FROM Sessions WHERE id =:id";
        try (Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.createQuery(HQL).setParameter("id", id).executeUpdate();
            tx.commit();
            log.info("DAO userSession delete SUCCESS");
        } catch (HibernateException e) {
            log.error("DAO userSession delete ERROR " + e);
            if (tx != null) {
                tx.rollback();
            }
        }
    }
}
