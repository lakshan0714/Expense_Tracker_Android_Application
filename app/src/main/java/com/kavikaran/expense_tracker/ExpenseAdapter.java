package com.kavikaran.expense_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kavikaran.expense_tracker.model.Expense;

import java.util.List;

public class ExpenseAdapter extends BaseAdapter {
    private Context context;
    private List<Expense> expenses;
    private LayoutInflater inflater;

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateExpenses(List<Expense> newExpenses) {
        if (this.expenses != null) {
            this.expenses.clear();
            if (newExpenses != null) {
                this.expenses.addAll(newExpenses);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return expenses != null ? expenses.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (expenses != null && position >= 0 && position < expenses.size()) {
            return expenses.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (expenses != null && position >= 0 && position < expenses.size()) {
            Expense expense = expenses.get(position);
            return expense != null ? expense.getId() : position;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expense_list_item, parent, false);
            holder = new ViewHolder();
            holder.titleText = convertView.findViewById(R.id.expenseTitle);
            holder.categoryText = convertView.findViewById(R.id.expenseCategory);
            holder.expenseAmount = convertView.findViewById(R.id.expenseAmount);
            holder.expenseDate = convertView.findViewById(R.id.expenseDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Add null checks
        if (expenses != null && position >= 0 && position < expenses.size()) {
            Expense expense = expenses.get(position);

            if (expense != null) {
                // Set title with null check
                if (holder.titleText != null) {
                    holder.titleText.setText(expense.getTitle() != null ? expense.getTitle() : "");
                }

                // Set category with null check
                if (holder.categoryText != null) {
                    holder.categoryText.setText(expense.getCategory() != null ? expense.getCategory() : "");
                }

                // Uncomment and add null checks for amount and date
                if (holder.expenseAmount != null) {
                    holder.expenseAmount.setText(String.format("$%.2f", expense.getAmount()));
                }

                if (holder.expenseDate != null) {
                    holder.expenseDate.setText(expense.getDate() != null ? expense.getDate() : "");
                }
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView titleText;
        TextView categoryText;
        TextView expenseAmount;
        TextView expenseDate;
    }
}