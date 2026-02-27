package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.PaymentRecord;

public interface PaymentService {

    /**
     * 模拟支付
     */
    PaymentRecord simulatePayment(String orderId, Long userId);

    /**
     * 根据订单ID获取支付记录
     */
    PaymentRecord getPaymentByOrderId(String orderId);

    /**
     * 检查订单是否已支付
     */
    boolean isOrderPaid(String orderId);
}