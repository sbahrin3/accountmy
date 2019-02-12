package lebah.template;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import lebah.db.PersistenceManager;


public class DbPersistence {
	
	private static EntityManager em;
	
	static { 
		try {
			PersistenceManager pm = new PersistenceManager();
			em = pm.getEntityManager();
						
		} catch ( Exception e ) {
			System.out.println("====================");
			e.printStackTrace();
			System.out.println("====================");
		}
	}
	
	public DbPersistence() {
	}
	
	
	public void close() {
		//em.close();
	}
	
	public EntityManager getEntityManager() {
		return em;
	}
	
	
	/*
	 * Synchronize the persistence context with the underlying database
	 */
	public void flush() {
		em.flush();
	}
	
	public void refresh(Object obj) {
		em.refresh(obj);
	}
	
	public void reload() {
		PersistenceManager pm = new PersistenceManager();
		pm.reloadPersistence();
		em = pm.getEntityManager();
	}

	public void entityAdd(Object obj) {
		try {
			begin();
			persist(obj);
			commit();
		} catch ( Exception e ) {
			System.out.println(e);
		}
	}
	
	protected void entityUpdate() throws Exception {
		begin();
		commit();
	}
	
	protected void entityDelete(Object obj) throws Exception {
		begin();
		remove(obj);
		commit();
	}


	public void begin() {
		try {
			em.getTransaction().begin();
		} catch ( Exception e ) {
			System.out.println("Error begin transaction: " + e.getMessage());
			em.getTransaction().rollback();
			em.getTransaction().begin();
		}
	}


	public void commit() throws Exception {
		try {
			em.getTransaction().commit();
		} catch ( Exception e ) {
			System.out.println("error in doing commit...");
			if ( em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}


	public <T> T find(Class<T> klass, Object id) {
		return em.find(klass, id);
	}
	
	public void persist(Object object) {
		em.persist(object);
	}	
	
	public void rollback() {
		em.getTransaction().rollback(); 
	}
	
	public void remove(Object object) {
		em.remove(object);
	}

	public void executeUpdate(String ql) {
		em.createQuery(ql).executeUpdate();
	}
	
	public void executeUpdate(String q, Hashtable h) {
		Query query = em.createQuery(q);
		for ( Enumeration e = h.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			Object value = h.get(key);
			query.setParameter(key, value);
		}
		query.executeUpdate();
	}
	
	
	public List list(String q) {
		List list = null;
		try {
			list = em.createQuery(q).getResultList();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List list(String q, int chunkSize) {
        Query query = em.createQuery(q);
        query = query.setFirstResult(0);
        query = query.setMaxResults(chunkSize);
        return query.getResultList();
    }
	
	public List list(String q, Hashtable h) {
		Query query = em.createQuery(q);
		for ( Enumeration e = h.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			Object value = h.get(key);
			query.setParameter(key, value);
		}
		List list = query.getResultList();
		return list;
	}
	
	
	

	public List list(String q, int chunkSize, Hashtable h) {
        Query query = em.createQuery(q);
		for ( Enumeration e = h.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			Object value = h.get(key);
			query.setParameter(key, value);
		}        
        query = query.setFirstResult(0);
        query = query.setMaxResults(chunkSize);
        return query.getResultList();
    }
	
	public List list(String q, int start, int chunkSize, Hashtable h) {
        Query query = em.createQuery(q);
		for ( Enumeration e = h.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			Object value = h.get(key);
			query.setParameter(key, value);
		}        
        query = query.setFirstResult(start);
        query = query.setMaxResults(chunkSize);
        return query.getResultList();
    }	
	
	public List list(String q, int start, int chunkSize) {
        return em.createQuery(q).setFirstResult(start).setMaxResults(chunkSize).getResultList();
    }
	
	public Object get(String q) {
		List l = list(q);
		return l.size() > 0 ? l.get(0) : null;
	}
	
	public Object get(String q, Hashtable h) {
		List l = list(q, h);
		return l.size() > 0 ? l.get(0) : null;
	}

}
