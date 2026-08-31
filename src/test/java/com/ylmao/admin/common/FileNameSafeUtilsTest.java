package com.ylmao.admin.common;

import com.ylmao.admin.config.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileNameSafeUtilsTest {

    @Test
    void keepsSimpleName() {
        assertEquals("报表.xlsx", FileNameSafeUtils.normalizeOriginalName("报表.xlsx"));
    }

    @Test
    void stripsPathSegments() {
        assertEquals("a.xlsx", FileNameSafeUtils.normalizeOriginalName("../a.xlsx"));
        assertEquals("b.png", FileNameSafeUtils.normalizeOriginalName("foo/bar/b.png"));
    }

    @Test
    void rejectsDotSegments() {
        assertThrows(BusinessException.class, () -> FileNameSafeUtils.normalizeOriginalName(".."));
        assertThrows(BusinessException.class, () -> FileNameSafeUtils.normalizeOriginalName("."));
    }

    @Test
    void stripsControlChars() {
        assertEquals("ok.txt", FileNameSafeUtils.normalizeOriginalName("ok\u0000.txt"));
    }

    @Test
    void rejectsBlank() {
        assertThrows(BusinessException.class, () -> FileNameSafeUtils.normalizeOriginalName("   "));
        assertThrows(BusinessException.class, () -> FileNameSafeUtils.normalizeOriginalName(null));
    }
}
