package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.PaymentRecord;
import com.huangbin.smartcommunityelderlycare.service.PaymentService;  // 导入接口
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;  // 注入接口，而不是实现类

    /**
     * 模拟支付
     */
    @PostMapping("/simulate")
    public Result<PaymentRecord> simulatePayment(@RequestBody Map<String, Object> request) {
        try {
            String orderId = (String) request.get("orderId");
            Long userId = Long.valueOf(request.get("userId").toString());

            PaymentRecord payment = paymentService.simulatePayment(orderId, userId);
            return Result.success("支付成功", payment);
        } catch (Exception e) {
            return Result.error("支付失败: " + e.getMessage());
        }
    }

    /**
     * 查询支付记录
     */
    @GetMapping("/record/{orderId}")
    public Result<PaymentRecord> getPaymentRecord(@PathVariable String orderId) {
        try {
            PaymentRecord payment = paymentService.getPaymentByOrderId(orderId);
            if (payment == null) {
                return Result.success("暂无支付记录", null);
            }
            return Result.success("查询成功", payment);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 支付回调（模拟）
     */
    @PostMapping("/callback")
    public Result<?> paymentCallback(@RequestBody Map<String, String> callbackData) {
        try {
            // 模拟支付回调处理
            String orderId = callbackData.get("orderId");
            String status = callbackData.get("status");

            // 这里可以更新订单状态
            System.out.println("收到支付回调，订单ID: " + orderId + ", 状态: " + status);

            return Result.success("回调处理成功");
        } catch (Exception e) {
            return Result.error("回调处理失败: " + e.getMessage());
        }
    }
}