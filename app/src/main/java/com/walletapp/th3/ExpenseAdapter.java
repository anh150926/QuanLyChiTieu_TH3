package com.walletapp.th3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpenseAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final ArrayList<Expense> expenses;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        this.inflater = LayoutInflater.from(context);
        this.expenses = expenses;
    }

    @Override public int getCount() { return expenses.size(); }
    @Override public Expense getItem(int position) { return expenses.get(position); }
    @Override public long getItemId(int position) { return position; }

    static class ViewHolder {
        TextView textName;
        TextView textAmount;
        TextView textCategory;
        TextView textDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_expense, parent, false);
            holder = new ViewHolder();
            holder.textName = convertView.findViewById(R.id.text_expense_name);
            holder.textAmount = convertView.findViewById(R.id.text_expense_amount);
            holder.textCategory = convertView.findViewById(R.id.text_expense_category);
            holder.textDate = convertView.findViewById(R.id.text_expense_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Expense expense = getItem(position);
        holder.textName.setText(expense.getName());
        holder.textAmount.setText(ExpenseUtils.formatMoney(expense.getAmount()));
        holder.textCategory.setText(expense.getCategory());
        holder.textDate.setText(expense.getDate());

        return convertView;
    }
}
