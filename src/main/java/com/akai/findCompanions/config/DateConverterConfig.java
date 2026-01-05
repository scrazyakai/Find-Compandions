package com.akai.findCompanions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 日期转换配置
 * 支持多种日期格式的自动转换
 *
 * @author Recursion
 * @since 2025-01-05
 */
@Configuration
public class DateConverterConfig {

    /**
     * 字符串转日期转换器
     * 支持ISO 8601格式和标准日期格式
     */
    @Bean
    public Converter<String, Date> stringToDateConverter() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                }

                // 1. 首先尝试 ISO 8601 格式（使用 Java 8 DateTimeFormatter）
                try {
                    // ISO 8601 格式器，支持带时区的格式
                    DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
                    TemporalAccessor temporal = isoFormatter.parse(source);
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
                        return sdf.parse(source);
                    } catch (Exception e) {
                        // 继续尝试下一个格式
                    }
                }

                throw new IllegalArgumentException("无法解析日期字符串: " + source);
            }
        };
    }
}
