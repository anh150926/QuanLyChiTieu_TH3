package com.walletapp.th3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private static final String STATE_EXPENSES = "expenses";
    private ArrayList<Expense> expenses;
    private ExpenseAdapter adapter;
    private TextView totalView;
    private ListView listView;
    private TextView emptyView;

    // Nhận dữ liệu khoản chi mới trả về từ AddExpenseActivity
    private final ActivityResultLauncher<Intent> addExpenseLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Expense expense = Expense.fromIntent(result.getData());
                    if (expense != null) {
                        expenses.add(expense);
                        refreshExpenses();
                    }
                }
            });

    // Nhận ID khoản chi bị xóa trả về từ ExpenseDetailActivity
    private final ActivityResultLauncher<Intent> detailLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String deletedId = result.getData().getStringExtra(Expense.EXTRA_DELETED_ID);
                    expenses.removeIf(expense -> expense.getId().equals(deletedId));
                    refreshExpenses();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applySystemInsets();

        expenses = restoreExpenses(savedInstanceState);
        totalView = findViewById(R.id.text_total);
        emptyView = findViewById(R.id.text_empty_list);
        listView = findViewById(R.id.list_expenses);

        adapter = new ExpenseAdapter(this, expenses);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ExpenseDetailActivity.class);
            intent.putExtra(Expense.EXTRA_EXPENSE, expenses.get(position));
            detailLauncher.launch(intent);
        });

        findViewById(R.id.button_add_expense).setOnClickListener(view ->
                addExpenseLauncher.launch(new Intent(this, AddExpenseActivity.class)));

        refreshExpenses();
    }

    @SuppressWarnings("deprecation")
    private ArrayList<Expense> restoreExpenses(Bundle state) {
        ArrayList<Expense> restored = new ArrayList<>();
        if (state != null && state.getSerializable(STATE_EXPENSES) instanceof ArrayList<?>) {
            for (Object item : (ArrayList<?>) state.getSerializable(STATE_EXPENSES)) {
                if (item instanceof Expense) {
                    restored.add((Expense) item);
                }
            }
            return restored;
        }
        // Dữ liệu mẫu ban đầu theo đề bài
        restored.add(new Expense("Ăn sáng", 30000, "Ăn uống", "12/10/2026", ""));
        restored.add(new Expense("Đổ xăng", 50000, "Đi lại", "12/10/2026", ""));
        restored.add(new Expense("Mua sách", 120000, "Học tập", "13/10/2026", "Sách Lập trình Android"));
        return restored;
    }

    private void refreshExpenses() {
        adapter.notifyDataSetChanged();
        totalView.setText(ExpenseUtils.formatMoney(ExpenseUtils.total(expenses)));
        emptyView.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_EXPENSES, expenses);
        super.onSaveInstanceState(outState);
    }
}
