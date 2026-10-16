package com.walletapp.th3;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ExpenseUtilsTest {
    @Test
    public void amountMustBeAPositiveIntegerWithinLongRange() {
        assertEquals(45000, ExpenseUtils.parseAmount("45000"));
        for (String invalid : Arrays.asList("", " ", "0", "-1", "1.5", "abc",
                "45000đ", "999999999999999999999999")) {
            assertThrows(invalid, IllegalArgumentException.class,
                    () -> ExpenseUtils.parseAmount(invalid));
        }
    }

    @Test
    public void moneyUsesVietnameseThousandsSeparator() {
        assertEquals("120.000 đ", ExpenseUtils.formatMoney(120000));
        assertEquals("0 đ", ExpenseUtils.formatMoney(0));
    }

    @Test
    public void totalMatchesSamplesAndHandlesEmptyList() {
        assertEquals(BigInteger.valueOf(200000), ExpenseUtils.total(Arrays.asList(
                new Expense("Ăn sáng", 30000, "Ăn uống", "12/10/2026", ""),
                new Expense("Đổ xăng", 50000, "Đi lại", "12/10/2026", ""),
                new Expense("Mua sách", 120000, "Học tập", "13/10/2026", ""))));
        assertEquals(BigInteger.ZERO, ExpenseUtils.total(Collections.emptyList()));
    }

    @Test
    public void totalDoesNotOverflowWithLargeExpenses() {
        Expense large = new Expense("Khoản chi", Long.MAX_VALUE, "Khác", "16/10/2026", "");
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TWO),
                ExpenseUtils.total(Arrays.asList(large, large)));
    }

    @Test
    public void datePickerMonthIsConvertedToCalendarMonth() {
        assertEquals("01/01/2026", ExpenseUtils.formatDate(2026, 0, 1));
        assertEquals("31/12/2026", ExpenseUtils.formatDate(2026, 11, 31));
    }
}
