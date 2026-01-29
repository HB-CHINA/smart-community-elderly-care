// src/main/java/com/huangbin/smartcommunityelderlycare/service/impl/OrderServiceImpl.java
package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.ServiceOrder;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.entity.ServiceItem;
import com.huangbin.smartcommunityelderlycare.enums.OrderStatus;
import com.huangbin.smartcommunityelderlycare.repository.ServiceOrderRepository;
import com.huangbin.smartcommunityelderlycare.repository.UserRepository;
import com.huangbin.smartcommunityelderlycare.repository.ServiceRepository;
import com.huangbin.smartcommunityelderlycare.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    @Transactional
    public ServiceOrder createOrder(Long userId, Long serviceId, LocalDateTime scheduledTime) {
        // 1. 验证用户和服务存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        ServiceItem service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("服务不存在: " + serviceId));

        // 2. 检查预约时间是否冲突（可选）
        boolean hasConflict = serviceOrderRepository.existsByUser_UserIdAndScheduledTimeBetween(
                userId,
                scheduledTime.minusHours(2),
                scheduledTime.plusHours(2)
        );

        if (hasConflict) {
            throw new RuntimeException("该时间段已有预约，请选择其他时间");
        }

        // 3. 生成订单ID
        String orderId = generateOrderId();

        // 4. 创建订单
        ServiceOrder order = new ServiceOrder();
        order.setOrderId(orderId);
        order.setUser(user);
        order.setService(service);
        order.setScheduledTime(scheduledTime);
        order.setStatus(OrderStatus.PENDING);  // 使用枚举
        order.setAmount(service.getPrice());

        return serviceOrderRepository.save(order);
    }

    private String generateOrderId() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return timePart + randomPart;
    }

    @Override
    public List<ServiceOrder> getOrdersByUserId(Long userId) {
        return serviceOrderRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public ServiceOrder getOrderById(String orderId) {
        return serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));
    }

    @Override
    @Transactional
    public ServiceOrder updateOrderStatus(String orderId, String status) {
        ServiceOrder order = getOrderById(orderId);

        // 将字符串状态转换为枚举
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.fromDescription(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的订单状态: " + status);
        }

        order.setStatus(orderStatus);  // 使用枚举
        return serviceOrderRepository.save(order);
    }

    @Override
    @Transactional
    public ServiceOrder cancelOrder(String orderId) {
        ServiceOrder order = getOrderById(orderId);

        // 只能取消待接单或已接单的订单（使用枚举）
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.ACCEPTED) {
            throw new RuntimeException("当前状态的订单不能取消: " + order.getStatus().getDescription());
        }

        order.setStatus(OrderStatus.CANCELLED);  // 使用枚举
        return serviceOrderRepository.save(order);
    }

    @Override
    @Transactional
    public ServiceOrder submitReview(String orderId, String review, Integer rating) {
        ServiceOrder order = getOrderById(orderId);

        // 只能评价已完成的订单（使用枚举）
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("只能评价已完成的订单");
        }

        order.setReview(review);
        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new RuntimeException("评分必须在1-5之间");
            }
            order.setRating(rating);
        }

        order.setStatus(OrderStatus.REVIEWED);  // 更新为已评价状态（使用枚举）
        return serviceOrderRepository.save(order);
    }

    @Override
    public List<ServiceOrder> getAllOrders() {
        return serviceOrderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Page<ServiceOrder> getOrdersByUserIdWithPage(Long userId, String status, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        if (status == null || status.isEmpty()) {
            return serviceOrderRepository.findByUser_UserId(userId, pageable);
        } else {
            // 将字符串状态转换为枚举
            OrderStatus orderStatus;
            try {
                orderStatus = OrderStatus.fromDescription(status);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的订单状态: " + status);
            }

            // 调用枚举版本的方法 - 注意：这里需要ServiceOrderRepository有这个方法
            return serviceOrderRepository.findByUser_UserIdAndStatus(userId, orderStatus, pageable);
        }
    }

    @Override
    public Map<String, Object> getOrderStatistics(String startDate, String endDate) {
        // 实现统计逻辑
        // 暂时返回空map，后续可以调用StatisticsService
        return Map.of(
                "totalOrders", 0,
                "totalAmount", 0.0,
                "averageOrderValue", 0.0
        );
    }

    // 可选：添加一个使用枚举参数的重载方法
    @Transactional
    public ServiceOrder updateOrderStatus(String orderId, OrderStatus status) {
        ServiceOrder order = getOrderById(orderId);
        order.setStatus(status);
        return serviceOrderRepository.save(order);
    }
}