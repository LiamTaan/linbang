package cn.iocoder.yudao.module.system.framework.captcha.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PictureWordCaptchaServiceImplTest {

    @Test
    void generateRandomText_usesExpectedAlphabetAndLength() {
        String value = PictureWordCaptchaServiceImpl.generateRandomText(128);

        assertEquals(128, value.length());
        assertTrue(value.matches("[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]+"));
    }

    @Test
    void generateRandomText_rejectsNegativeLength() {
        assertThrows(IllegalArgumentException.class,
                () -> PictureWordCaptchaServiceImpl.generateRandomText(-1));
    }

}
