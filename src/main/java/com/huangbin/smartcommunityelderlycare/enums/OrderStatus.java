package com.huangbin.smartcommunityelderlycare.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = OrderStatus.OrderStatusSerializer.class)
@JsonDeserialize(using = OrderStatus.OrderStatusDeserializer.class)
public enum OrderStatus {
    PENDING("待接单"),
    ACCEPTED("已接单"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    PAID("已支付"),
    REVIEWED("已评价");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    // 简单反序列化方法
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OrderStatus fromValue(String value) {
        if (value == null) return null;

        // 按中文描述查找
        for (OrderStatus status : values()) {
            if (status.description.equals(value)) {
                return status;
            }
        }

        // 按英文名称查找（不区分大小写）
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的订单状态: " + value);
        }
    }

    public static OrderStatus fromDescription(String description) {
        return fromValue(description); // 直接调用现有的 fromValue 方法
    }

    // 自定义序列化器
    public static class OrderStatusSerializer extends com.fasterxml.jackson.databind.JsonSerializer<OrderStatus> {
        @Override
        public void serialize(OrderStatus value, com.fasterxml.jackson.core.JsonGenerator gen,
                              com.fasterxml.jackson.databind.SerializerProvider serializers) throws java.io.IOException {
            gen.writeString(value.getDescription());
        }
    }

    // 自定义反序列化器
    public static class OrderStatusDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<OrderStatus> {
        @Override
        public OrderStatus deserialize(com.fasterxml.jackson.core.JsonParser p,
                                       com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            String value = p.getText();
            return fromValue(value);
        }
    }
}