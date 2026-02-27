package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.ServiceItem;
import com.huangbin.smartcommunityelderlycare.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/service")
@CrossOrigin(origins = "*")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @PostMapping
    public Result<ServiceItem> addService(@Valid @RequestBody ServiceItem serviceItem) {
        try {
            ServiceItem savedService = serviceService.addService(serviceItem);
            return Result.success("服务添加成功", savedService);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<List<ServiceItem>> getAllServices() {
        try {
            List<ServiceItem> services = serviceService.getAllServices();
            return Result.success(services);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/available")
    public Result<List<ServiceItem>> getAvailableServices() {
        try {
            List<ServiceItem> services = serviceService.getAvailableServices();
            return Result.success(services);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/category/{category}")
    public Result<List<ServiceItem>> getServicesByCategory(@PathVariable("category") String category) {
        try {
            List<ServiceItem> services = serviceService.getServicesByCategory(category);
            return Result.success(services);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 修复：添加正确的查询参数接口
    @GetMapping("/category")
    public Result<List<ServiceItem>> getServicesByCategoryParam(@RequestParam("name") String category) {
        try {
            List<ServiceItem> services = serviceService.getServicesByCategory(category);
            return Result.success(services);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<ServiceItem> getServiceById(@PathVariable("id") Long id) {
        try {
            ServiceItem service = serviceService.getServiceById(id);
            return Result.success(service);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<ServiceItem> updateService(
            @PathVariable("id") Long id,
            @RequestBody ServiceItem serviceItem) {
        try {
            ServiceItem updatedService = serviceService.updateService(id, serviceItem);
            return Result.success("服务更新成功", updatedService);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteService(@PathVariable("id") Long id) {
        try {
            serviceService.deleteService(id);
            return Result.success("服务已下架");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("服务管理接口正常");
    }
}