package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.ServiceOrder;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {

    // 创建订单
    ServiceOrder createOrder(Long userId, Long serviceId, LocalDateTime scheduledTime);

    // 获取用户的所有订单
    List<ServiceOrder> getOrdersByUserId(Long userId);

    // 获取订单详情
    ServiceOrder getOrderById(String orderId);

    // 更新订单状态
    ServiceOrder updateOrderStatus(String orderId, String status);

    // 取消订单
    ServiceOrder cancelOrder(String orderId);

    // 提交评价
    ServiceOrder submitReview(String orderId, String review, Integer rating);

    // 获取所有订单（管理员用）
    List<ServiceOrder> getAllOrders();

    // 分页查询用户订单
    Page<ServiceOrder> getOrdersByUserIdWithPage(Long userId, String status, int page, int size);

    // 获取订单统计
    Map<String, Object> getOrderStatistics(String startDate, String endDate);
}