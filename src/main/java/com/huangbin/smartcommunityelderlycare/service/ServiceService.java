package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.ServiceItem;
import java.util.List;

public interface ServiceService {
    ServiceItem addService(ServiceItem serviceItem);
    List<ServiceItem> getAllServices();
    List<ServiceItem> getServicesByCategory(String category);
    List<ServiceItem> getAvailableServices();
    ServiceItem getServiceById(Long id);
    ServiceItem updateService(Long id, ServiceItem serviceItem);
    void deleteService(Long id);
}