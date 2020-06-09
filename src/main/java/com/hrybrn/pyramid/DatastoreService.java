package com.hrybrn.pyramid;


import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityManagerFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class DatastoreService {
  @Getter
  private EntityManager entityManager;

  public DatastoreService() {
    EntityManagerFactory emf = EntityManagerFactory.getInstance();
    entityManager = emf.createEntityManager("zoom-pyramid",
        System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
  }
}
