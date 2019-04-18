package com.winters.shoppinglist.db.dao;

import com.winters.shoppinglist.db.entity.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@LocalBean
@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class Dao implements DaoInterface
{
    Logger logger = LogManager.getLogger(Dao.class);

    @Resource
    UserTransaction tx;
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public List<Entry> getAllEntries()
    {
      try
      {
        tx.begin();
        Query query = em.createNamedQuery("getAll");
        List<Entry> retval = query.getResultList();
        tx.commit();
        return retval;
      }
      catch(Exception e)
      {
        return null;
      }
    }

    public int addItem(Entry entry)
    {
      if(entry.getName().isEmpty() || entry.getStore().isEmpty())
      {
        return 2;
      }

      try
      {
        tx.begin();

        Query query = em.createNamedQuery("getByNameAndStore");
        query.setParameter("name", entry.getName());
        query.setParameter("store", entry.getStore());
        List<Entry> resultList = query.getResultList();

        if(resultList.isEmpty())
        {
          em.persist(entry);
          em.flush();
          em.clear();
          tx.commit();
          return 0;
        }
        else
        {
          tx.commit();
          return 1;
        }
      }
      catch(Exception e)
      {
        logger.error(e, e);
        return -1;
      }
    }

  public void deleteEntries(List<Integer> toDelete) 
  {
    try
    {
      tx.begin();
      Query query = em.createNamedQuery("getAll");
      List<Entry> entries = query.getResultList();

      for(Entry e : entries)
      {
        if(toDelete.contains(e.getId()))
        {
          System.out.println("removing element " + e);
          em.remove(e);
        }
      }
      tx.commit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    } 
  }

  public List<String> getListOfStores()
  {
    try
    {
      Query query = em.createNamedQuery("getAll");
      List<Entry> entries = query.getResultList();

      List<String> retval = new ArrayList<String>();
      for(Entry e : entries)
      {
        if(!e.getStore().toLowerCase().equals("any") 
        && !retval.contains(e.getStore().toLowerCase()))
        {
          retval.add(e.getStore().toLowerCase());
        }
      }
      Collections.sort(retval);

      return retval;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return new ArrayList<String>();
    }
  }
}
