package com.walletapp.th3;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public final class ExpenseUtils {
    private ExpenseUtils() { }

    // Kiểm tra và chuyển đổi số tiền nhập vào
    public static long parseAmount(String input) {
        String value = input.trim();
        if (!value.matches("[0-9]+")) {
            throw new IllegalArgumentException("Số tiền không hợp lệ");
        }
        long amount = Long.parseLong(value);
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }
        return amount;
    }

    // Định dạng tiền tệ theo chuẩn Việt Nam (vd: 50.000 đ)
    public static String formatMoney(Number amount) {
        return NumberFormat.getIntegerInstance(new Locale("vi", "VN")).format(amount) + " đ";
    }

    // Tính tổng số tiền các khoản chi
    public static BigInteger total(List<Expense> expenses) {
        BigInteger total = BigInteger.ZERO;
        for (Expense expense : expenses) {
            total = total.add(BigInteger.valueOf(expense.getAmount()));
        }
        return total;
    }

    // Định dạng ngày chi tiêu dd/MM/yyyy
    public static String formatDate(int year, int month, int day) {
        return String.format(Locale.ROOT, "%02d/%02d/%04d", day, month + 1, year);
    }
}
