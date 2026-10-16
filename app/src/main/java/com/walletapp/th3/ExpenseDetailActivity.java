package com.walletapp.th3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class ExpenseDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);
        applySystemInsets();

        ((TextView) findViewById(R.id.text_screen_title)).setText(R.string.expense_detail);

        Expense expense = Expense.fromIntent(getIntent());
        if (expense == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.text_detail_name)).setText(expense.getName());
        ((TextView) findViewById(R.id.text_detail_amount)).setText(ExpenseUtils.formatMoney(expense.getAmount()));
        ((TextView) findViewById(R.id.text_detail_category)).setText(expense.getCategory());
        ((TextView) findViewById(R.id.text_detail_date)).setText(expense.getDate());
        ((TextView) findViewById(R.id.text_detail_note)).setText(expense.getNote());

        findViewById(R.id.button_back).setOnClickListener(view -> finish());
        findViewById(R.id.button_return).setOnClickListener(view -> finish());

        // Hiển thị hộp thoại xác nhận khi bấm Xóa
        findViewById(R.id.button_delete_expense).setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_confirm_title)
                    .setMessage(R.string.delete_confirm_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.confirm_delete, (dialog, which) -> {
                        Intent result = new Intent();
                        result.putExtra(Expense.EXTRA_DELETED_ID, expense.getId());
                        setResult(RESULT_OK, result);
                        finish();
                    })
                    .show();
        });
    }
}
