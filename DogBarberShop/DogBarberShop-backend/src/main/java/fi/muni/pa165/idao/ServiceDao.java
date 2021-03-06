/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.muni.pa165.idao;

import fi.muni.pa165.entity.Service;
import java.util.List;
import org.joda.time.Duration;

/**
 * @author Honza
 */
public interface ServiceDao {
  public Service addService(Service s);
  public void delService(Long id);
  public Service updateService(Service s);

  public Service getServiceById(Long id);
  public List<Service> getServiceByName(String name);
  public List<Service> getServiceByPrice(Long price);
  public List<Service> getServiceByDuration(Duration duration);
  // doplnena
  public List<Service> getAllServices();
}