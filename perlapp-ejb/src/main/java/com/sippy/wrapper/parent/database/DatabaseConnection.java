package com.sippy.wrapper.parent.database;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sippy.wrapper.parent.database.dao.TnbDao;

@Stateless
public class DatabaseConnection {

  @PersistenceContext(unitName = "CustomDB")
  private EntityManager entityManager;

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);

  public int countTheEntries() {
    return ((Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM tnbs").getSingleResult())
        .intValue();
  }

  @SuppressWarnings("unchecked")
  public List<TnbDao> getAllTnbs() {
    Query query = entityManager.createNativeQuery("SELECT * FROM tnbs", TnbDao.class);
    return query.getResultList();
  }

  public TnbDao getTnbById(int id) {
    Query query = entityManager.createNativeQuery("SELECT tnb FROM tnbs WHERE tnb = :id", TnbDao.class);
    query.setParameter("id", id);
    
    try {
        return (TnbDao) query.getSingleResult();
    } catch (NoResultException e) {
        throw new NoResultException("No such tnb with id: " + id);
    }
  }
}
