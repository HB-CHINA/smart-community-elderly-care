package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.ServiceOrder;
import com.huangbin.smartcommunityelderlycare.service.OrderService;
import com.huangbin.smartcommunityelderlycare.service.StatisticsService;
import com.huangbin.smartcommunityelderlycare.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StatisticsService statisticsService;
    /**
     * 创建订单
     */
    @PostMapping
    public Result<ServiceOrder> createOrder(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long serviceId = Long.valueOf(request.get("serviceId").toString());

            // 解析预约时间
            String scheduledTimeStr = request.get("scheduledTime").toString();
            LocalDateTime scheduledTime = LocalDateTime.parse(scheduledTimeStr,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            ServiceOrder order = orderService.createOrder(userId, serviceId, scheduledTime);
            return Result.success("订单创建成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户的所有订单
     */
    @GetMapping("/user/{userId}")
    public Result<List<ServiceOrder>> getOrdersByUser(@PathVariable("userId") Long userId) {
        try {
            List<ServiceOrder> orders = orderService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<ServiceOrder> getOrderById(@PathVariable("orderId") String orderId) {
        try {
            ServiceOrder order = orderService.getOrderById(orderId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderId}/status")
    public Result<ServiceOrder> updateOrderStatus(
            @PathVariable("orderId") String orderId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            ServiceOrder order = orderService.updateOrderStatus(orderId, status);
            return Result.success("订单状态更新成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result<ServiceOrder> cancelOrder(@PathVariable("orderId") String orderId) {
        try {
            ServiceOrder order = orderService.cancelOrder(orderId);
            return Result.success("订单已取消", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交评价
     */
    @PostMapping("/{orderId}/review")
    public Result<ServiceOrder> submitReview(
            @PathVariable("orderId") String orderId,
            @RequestBody Map<String, Object> request) {
        try {
            String review = (String) request.get("review");
            Integer rating = request.get("rating") != null ?
                    Integer.valueOf(request.get("rating").toString()) : null;

            ServiceOrder order = orderService.submitReview(orderId, review, rating);
            return Result.success("评价提交成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有订单（管理员用）
     */
    @GetMapping("/all")
    public Result<List<ServiceOrder>> getAllOrders() {
        try {
            List<ServiceOrder> orders = orderService.getAllOrders();
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }



    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("订单管理接口正常");
    }

    /**
     * 获取订单统计（ECharts用）
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (start.isAfter(end)) {
                return Result.error("开始日期不能晚于结束日期");
            }

            Map<String, Object> stats = statisticsService.getOrderStatistics(start, end);
            return Result.success("订单统计查询成功", stats);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取服务分布统计
     */
    @GetMapping("/service-distribution")
    public Result<Map<String, Object>> getServiceDistribution() {
        try {
            Map<String, Object> distribution = statisticsService.getServiceDistribution();
            return Result.success("服务分布查询成功", distribution);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

}