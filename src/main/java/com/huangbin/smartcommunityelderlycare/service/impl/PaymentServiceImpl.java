package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.PaymentRecord;
import com.huangbin.smartcommunityelderlycare.entity.ServiceOrder;
import com.huangbin.smartcommunityelderlycare.enums.OrderStatus;
import com.huangbin.smartcommunityelderlycare.enums.PaymentStatus;
import com.huangbin.smartcommunityelderlycare.repository.PaymentRepository;
import com.huangbin.smartcommunityelderlycare.repository.ServiceOrderRepository;
import com.huangbin.smartcommunityelderlycare.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Override
    @Transactional
    public PaymentRecord simulatePayment(String orderId, Long userId) {
        // 1. 获取订单信息
        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));

        // 2. 验证订单状态和用户 - 修复：避免空指针
        Long orderUserId = order.getUserId();
        if (orderUserId == null || !orderUserId.equals(userId)) {
            throw new RuntimeException("无权支付此订单: 订单属于用户" + orderUserId + "，当前用户" + userId);
        }

        // 使用枚举进行状态验证
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("订单状态不允许支付，当前状态: " + order.getStatus().getDescription());
        }

        // 3. 创建支付记录
        PaymentRecord payment = new PaymentRecord();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(order.getOrderAmount());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionNo(generateTransactionNo());
        payment.setPaymentTime(LocalDateTime.now());

        PaymentRecord savedPayment = paymentRepository.save(payment);

        // 4. 更新订单状态
        order.setStatus(OrderStatus.PAID);
        serviceOrderRepository.save(order);

        System.out.println("支付成功: 订单ID=" + orderId + ", 用户ID=" + userId + ", 金额=" + order.getOrderAmount());
        return savedPayment;
    }

    @Override
    public PaymentRecord getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public boolean isOrderPaid(String orderId) {
        PaymentRecord payment = paymentRepository.findByOrderId(orderId);
        return payment != null && payment.getPaymentStatus() == PaymentStatus.SUCCESS;
    }

    private String generateTransactionNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}