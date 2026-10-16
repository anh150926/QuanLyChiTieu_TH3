package com.walletapp.th3;

import android.content.Intent;
import android.os.Build;

import java.io.Serializable;
import java.util.UUID;

/** Một khoản chi được truyền giữa các Activity qua Intent. */
public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String EXTRA_EXPENSE = "com.walletapp.th3.EXPENSE";
    public static final String EXTRA_DELETED_ID = "com.walletapp.th3.DELETED_ID";
    private final String id;
    private final String name;
    private final long amount;
    private final String category;
    private final String date;
    private final String note;

    public Expense(String name, long amount, String category, String date, String note) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public long getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getNote() { return note; }

    @SuppressWarnings("deprecation")
    public static Expense fromIntent(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getSerializableExtra(EXTRA_EXPENSE, Expense.class);
        }
        Serializable value = intent.getSerializableExtra(EXTRA_EXPENSE);
        return value instanceof Expense ? (Expense) value : null;
    }
}
