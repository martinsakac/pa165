/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.muni.pa165.dao.impl;

import fi.muni.pa165.entity.Employee;
import fi.muni.pa165.idao.ServiceDao;
import fi.muni.pa165.entity.Service;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.joda.time.Duration;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

/**
 * @author Honza
 */
@Repository
public class ServiceDaoImpl implements ServiceDao {

  @PersistenceContext
  private EntityManager em;  // emf.createEntityManager();

  public ServiceDaoImpl() {
  }

  public ServiceDaoImpl(EntityManager em) {
    this.em = em;
  }

  @Override
  public Service addService(Service s) {
    List<Employee> employees = s.getEmployees();
    try {
        if(employees != null){
            s.setEmployees(null);
            em.persist(s);
            s.setEmployees(new ArrayList<Employee>());
            for (Employee e : employees){
                s.addEmployee(e);
                e.addService(s);
                em.merge(e);
            }
            //s.setEmployees(employees);
            em.merge(s);
        }
        else {
            em.persist(s);
        }
    }
    catch (Exception e) {
      throw new DataAccessException("Could not persist given Service.") {};
    }
    return s;
  }

  @Override
  public void delService(Long id) {
    Service service = null;  
    try {
      service = getServiceById(id);
      for (Employee employee : service.getEmployees()){
          employee.removeService(service);
          em.merge(employee);
      }
      // merge first to prevent entity detaching
      em.remove(em.merge(service));
    }
    catch (Exception e) {
      throw new DataAccessException("Could not remove Service with id " +
              id.toString() + ".") {};
    }
  }

  @Override
  public Service updateService(Service s) {
    try {
      em.merge(s);
    }
    catch (Exception e) {
      throw new DataAccessException("Could not update/merge given Service.") {};
    }
    return s;
  }

  @Override
  public Service getServiceById(Long id) {
    TypedQuery<Service> q = em.createQuery(
            "SELECT s FROM Service AS s WHERE s.id = :id", Service.class);
    q.setParameter("id", id);
    Service s = null;
    try {
      s = q.getSingleResult();  // throws excptn if 0 || >1 results
    }
    catch (Exception e) {
      throw new DataAccessException(
              "Select unexpectedly returned 0 || >1 results.") {};
    }
    return s;
  }

  @Override
  public List<Service> getServiceByName(String name) {
    TypedQuery<Service> q = em.createQuery(
            "SELECT s FROM Service AS s where s.name = :name", Service.class);
    q.setParameter("name", name);
    List<Service> l = null;
    try {
      l = q.getResultList();
    }
    catch (Exception e) {
      throw new DataAccessException("TypeQuery.getResultList() threw exception.") {};
    }
    return l;
  }

  @Override
  public List<Service> getServiceByPrice(Long price) {
    TypedQuery<Service> q = em.createQuery(
            "SELECT s FROM Service AS s where s.price = :price", Service.class);
    q.setParameter("price", price);
    List<Service> l = null;
    try {
      l = q.getResultList();
    }
    catch (Exception e) {
      throw new DataAccessException("TypeQuery.getResultList() threw exception.") {};
    }
    return l;
  }

  @Override
  public List<Service> getServiceByDuration(Duration duration) {
    TypedQuery<Service> q = em.createQuery(
            "SELECT s FROM Service AS s where s.duration = :duration",
            Service.class);
    q.setParameter("duration", duration);
    List<Service> l = null;
    try {
      l = q.getResultList();
    }
    catch (Exception e) {
      throw new DataAccessException("TypeQuery.getResultList() threw exception.") {};
    }
    return l;
  }

  @Override
  public List<Service> getAllServices() {
    TypedQuery<Service> q = em.createQuery(
            "select s from Service s", Service.class);
    List<Service> l = null;
    try {
      l = q.getResultList();
    }
    catch (Exception e) {
      throw new DataAccessException("TypeQuery.getResultList() threw exception.") {};
    }
    return l;
  }
}