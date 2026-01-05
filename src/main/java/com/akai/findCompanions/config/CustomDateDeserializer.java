package com.akai.findCompanions.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 自定义日期反序列化器
 * 支持多种日期格式，特别是ISO 8601格式
 *
 * @author Recursion
 * @since 2025-01-05
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText();

        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        // 1. 首先尝试 ISO 8601 格式（使用 Java 8 DateTimeFormatter）
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            TemporalAccessor temporal = isoFormatter.parse(dateStr);
            return Date.from(Instant.from(temporal));
        } catch (Exception e) {
            // 如果不是 ISO 8601 格式，继续尝试其他格式
        }

        // 2. 尝试其他常见格式
        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss.SSS",  // Standard with milliseconds
            "yyyy-MM-dd HH:mm:ss",      // Standard format
            "yyyy-MM-dd"                // Date only
        };

        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(dateStr);
            } catch (Exception e) {
                // 继续尝试下一个格式
            }
        }

        throw new IllegalArgumentException("无法解析日期字符串: " + dateStr);
    }
}
