package plm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	   private static final SessionFactory sessionFactory;
	   private static final StandardServiceRegistry serviceRegistry;
	   static {
	      try {
	         Configuration config =
	            new Configuration().configure("hibernate.cfg.xml");
	         serviceRegistry = new StandardServiceRegistryBuilder()
	            .applySettings(config.getProperties()).build();
	         sessionFactory = config.buildSessionFactory(serviceRegistry);
	      } catch (Throwable ex) {
	         System.err.println("Initial SessionFactory creation failed." + ex);
	         throw new ExceptionInInitializerError(ex);
	      }
	   }
	   public static Session openSession() {
	      return sessionFactory.openSession();
	   }
}