package com.huangbin.smartcommunityelderlycare.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ServiceStatus.ServiceStatusSerializer.class)
@JsonDeserialize(using = ServiceStatus.ServiceStatusDeserializer.class)
public enum ServiceStatus {
    ONLINE("上架"),
    OFFLINE("下架"),
    DISABLED("禁用"),
    DELETED("删除");

    private final String description;

    ServiceStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ServiceStatus fromValue(String value) {
        if (value == null) return null;

        for (ServiceStatus status : values()) {
            if (status.description.equals(value)) {
                return status;
            }
        }

        try {
            return ServiceStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的服务状态: " + value);
        }
    }

    public static class ServiceStatusSerializer extends com.fasterxml.jackson.databind.JsonSerializer<ServiceStatus> {
        @Override
        public void serialize(ServiceStatus value, com.fasterxml.jackson.core.JsonGenerator gen,
                              com.fasterxml.jackson.databind.SerializerProvider serializers) throws java.io.IOException {
            gen.writeString(value.getDescription());
        }
    }

    public static class ServiceStatusDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<ServiceStatus> {
        @Override
        public ServiceStatus deserialize(com.fasterxml.jackson.core.JsonParser p,
                                         com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            String value = p.getText();
            return fromValue(value);
        }
    }
}