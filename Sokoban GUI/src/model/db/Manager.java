package model.db;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


public class Manager {
	private static Manager instance = new Manager();

	public static Manager getInstance() {
		return instance;
	}

	private SessionFactory factory;

	private Manager() {
		// to show the severe msg
		Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

		// reading the xml so he can connect to the Db
		Configuration configuration = new Configuration();
		configuration.configure();
		factory = configuration.buildSessionFactory();
	}

	public void add(Object obj) {
		Session session = null;
		Transaction tx = null;

		try {
			session = factory.openSession();
			tx = session.beginTransaction();

			session.save(obj);
			tx.commit();
		} catch (HibernateException ex) {
			if (tx != null)
				tx.rollback();
			System.out.println(ex.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	public void getAllRecords(){
		Session session = null;
		Record record;
		try {
			session = factory.openSession();
			Query query=session.createQuery("from Records");
			List<Record>list=query.getResultList();
			Iterator<Record>it=list.iterator();
			
			while(it.hasNext()){
				record=it.next();
				System.out.println(record);
			}
		/*	
			for(int i=0;i<list.size();i++){
				Object[] row=(Object[])list.get(i);
				for(int j=0;j<5;j++)
					System.out.print(row[j]+" ");
				System.out.println();	
			}*/
		
		} catch (HibernateException ex) {
			System.out.println(ex.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	
	

	public void close() {
		factory.close();
	}

}