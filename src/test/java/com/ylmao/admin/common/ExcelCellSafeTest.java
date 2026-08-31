package com.ylmao.admin.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExcelCellSafeTest {

    @Test
    void prefixesFormulaChars() {
        assertEquals("'=1+1", ExcelCellSafe.escape("=1+1"));
        assertEquals("'+cmd", ExcelCellSafe.escape("+cmd"));
        assertEquals("'-1", ExcelCellSafe.escape("-1"));
        assertEquals("'@sum", ExcelCellSafe.escape("@sum"));
    }

    @Test
    void leavesNormalText() {
        assertEquals("张三", ExcelCellSafe.escape("张三"));
        assertNull(ExcelCellSafe.escape(null));
        assertEquals("", ExcelCellSafe.escape(""));
    }
}
