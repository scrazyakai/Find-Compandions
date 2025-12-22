package com.akai.findCompanions.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensitiveWordBsConfig {
    @Bean(initMethod = "init")
    public SensitiveWordBs sensitiveWordBs(){
        return SensitiveWordBs.newInstance()
                .wordDeny(WordDenys.defaults())
                .wordAllow(WordAllows.defaults())
                .ignoreCase(true)
                .ignoreWidth(true);
    }
}
