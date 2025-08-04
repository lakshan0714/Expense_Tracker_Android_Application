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
        this.expenses.clear();
        this.expenses.addAll(newExpenses);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return expenses != null ? expenses.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return expenses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return expenses.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Create simple layout if expense_list_item.xml doesn't exist
            convertView = createSimpleItemView(parent);
            holder = new ViewHolder();
            holder.titleText = convertView.findViewById(android.R.id.text1);
            holder.categoryText = convertView.findViewById(android.R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Expense expense = expenses.get(position);

        // Set title
        holder.titleText.setText(expense.getTitle());

        // Set category and amount info
        String categoryInfo = expense.getCategory() + " - Rs. " + expense.getAmount() + " (" + expense.getDate() + ")";
        holder.categoryText.setText(categoryInfo);

        return convertView;
    }

    private View createSimpleItemView(ViewGroup parent) {
        // Create a simple two-line list item if custom layout doesn't exist
        return inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
    }

    static class ViewHolder {
        TextView titleText;
        TextView categoryText;
    }
}