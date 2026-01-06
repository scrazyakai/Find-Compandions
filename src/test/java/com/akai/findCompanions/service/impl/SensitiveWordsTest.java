package com.akai.findCompanions.service.impl;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import org.junit.jupiter.api.Test;

public class SensitiveWordsTest {

    @Test
    public void testSensitiveWords() {
        SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance().init();
        if (SensitiveWordHelper.contains("")) {
            System.out.println("成功截获");
        } else {
            System.out.println("<UNK>");
        }
    }
}

