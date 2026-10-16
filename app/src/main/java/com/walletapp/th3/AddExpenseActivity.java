package com.walletapp.th3;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

public class AddExpenseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        applySystemInsets();

        ((TextView) findViewById(R.id.text_screen_title)).setText(R.string.add_expense);
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        EditText nameView = findViewById(R.id.edit_expense_name);
        EditText amountView = findViewById(R.id.edit_expense_amount);
        EditText noteView = findViewById(R.id.edit_expense_note);
        DatePicker datePicker = findViewById(R.id.date_expense);
        styleDatePicker(datePicker);

        Spinner categoryView = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> categories = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, R.layout.spinner_category_item);
        categories.setDropDownViewResource(R.layout.spinner_category_dropdown_item);
        categoryView.setAdapter(categories);

        findViewById(R.id.button_save_expense).setOnClickListener(view -> {
            String name = nameView.getText().toString().trim();
            if (name.isEmpty()) {
                nameView.setError(getString(R.string.expense_name_required));
                nameView.requestFocus();
                return;
            }

            long amount;
            try {
                amount = ExpenseUtils.parseAmount(amountView.getText().toString());
            } catch (IllegalArgumentException exception) {
                amountView.setError(getString(R.string.expense_amount_invalid));
                amountView.requestFocus();
                return;
            }

            datePicker.clearFocus();
            String date = ExpenseUtils.formatDate(datePicker.getYear(),
                    datePicker.getMonth(), datePicker.getDayOfMonth());

            Expense expense = new Expense(name, amount, categoryView.getSelectedItem().toString(),
                    date, noteView.getText().toString().trim());

            Intent result = new Intent();
            result.putExtra(Expense.EXTRA_EXPENSE, expense);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    private void styleDatePicker(View view) {
        if (view instanceof NumberPicker) {
            NumberPicker picker = (NumberPicker) view;
            picker.setBackgroundResource(R.drawable.background_date_selection);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                picker.setSelectionDividerHeight(0);
                picker.setTextColor(getColor(R.color.text_secondary));
            }
        }
        if (view instanceof EditText) {
            EditText selectedValue = (EditText) view;
            selectedValue.setTextColor(getColor(R.color.status_bar));
            selectedValue.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                styleDatePicker(group.getChildAt(index));
            }
        }
    }
}
