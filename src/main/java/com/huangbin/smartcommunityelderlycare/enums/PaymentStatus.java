package com.huangbin.smartcommunityelderlycare.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = PaymentStatus.PaymentStatusSerializer.class)
@JsonDeserialize(using = PaymentStatus.PaymentStatusDeserializer.class)
public enum PaymentStatus {
    PENDING("待支付"),
    SUCCESS("支付成功"),
    FAILED("支付失败"),
    REFUNDED("已退款"),
    CANCELLED("已取消");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PaymentStatus fromValue(String value) {
        if (value == null) return null;

        for (PaymentStatus status : values()) {
            if (status.description.equals(value)) {
                return status;
            }
        }

        try {
            return PaymentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的支付状态: " + value);
        }
    }

    public static class PaymentStatusSerializer extends com.fasterxml.jackson.databind.JsonSerializer<PaymentStatus> {
        @Override
        public void serialize(PaymentStatus value, com.fasterxml.jackson.core.JsonGenerator gen,
                              com.fasterxml.jackson.databind.SerializerProvider serializers) throws java.io.IOException {
            gen.writeString(value.getDescription());
        }
    }

    public static class PaymentStatusDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<PaymentStatus> {
        @Override
        public PaymentStatus deserialize(com.fasterxml.jackson.core.JsonParser p,
                                         com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            String value = p.getText();
            return fromValue(value);
        }
    }
}