package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.ServiceItem;
import com.huangbin.smartcommunityelderlycare.enums.ServiceStatus;  // 添加导入
import com.huangbin.smartcommunityelderlycare.repository.ServiceRepository;
import com.huangbin.smartcommunityelderlycare.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public ServiceItem addService(ServiceItem serviceItem) {
        // 验证服务名称唯一性
        if (serviceRepository.findAll().stream()
                .anyMatch(s -> s.getServiceName().equals(serviceItem.getServiceName()))) {
            throw new RuntimeException("服务名称已存在: " + serviceItem.getServiceName());
        }

        // 验证价格大于0
        if (serviceItem.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("服务价格必须大于0");
        }

        // 确保状态使用枚举
        if (serviceItem.getStatus() == null) {
            serviceItem.setStatus(ServiceStatus.ONLINE);
        }

        return serviceRepository.save(serviceItem);
    }

    public List<ServiceItem> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<ServiceItem> getServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }

    public List<ServiceItem> getAvailableServices() {
        return serviceRepository.findByStatus(ServiceStatus.ONLINE);  // 使用枚举
    }

    public ServiceItem getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("服务不存在"));
    }

    public ServiceItem updateService(Long id, ServiceItem serviceItem) {
        ServiceItem existingService = getServiceById(id);

        // 如果要更新服务名称，检查唯一性（排除自身）
        if (serviceItem.getServiceName() != null &&
                !existingService.getServiceName().equals(serviceItem.getServiceName())) {

            boolean nameExists = serviceRepository.findAll().stream()
                    .filter(s -> !s.getServiceId().equals(id))  // 排除自身
                    .anyMatch(s -> s.getServiceName().equals(serviceItem.getServiceName()));

            if (nameExists) {
                throw new RuntimeException("服务名称已存在: " + serviceItem.getServiceName());
            }

            existingService.setServiceName(serviceItem.getServiceName());
        }

        if (serviceItem.getCategory() != null) {
            existingService.setCategory(serviceItem.getCategory());
        }
        if (serviceItem.getDescription() != null) {
            existingService.setDescription(serviceItem.getDescription());
        }
        if (serviceItem.getPrice() != null) {
            existingService.setPrice(serviceItem.getPrice());
        }
        if (serviceItem.getStatus() != null) {
            existingService.setStatus(serviceItem.getStatus());
        }

        return serviceRepository.save(existingService);
    }

    public void deleteService(Long id) {
        ServiceItem serviceItem = getServiceById(id);
        serviceItem.setStatus(ServiceStatus.OFFLINE);  // 使用枚举
        serviceRepository.save(serviceItem);
    }
}