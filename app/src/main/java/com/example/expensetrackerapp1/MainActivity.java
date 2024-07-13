package com.example.expensetrackerapp1;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private EditText Description, Amount;
    private Button AddExpense;
    private TextView Total;
    private ListView Expenses;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> expenseList;
    private double total = 0.0;
    private static final String PREFS_NAME = "ExpenseTrackerPrefs";
    private static final String KEY_EXPENSE_LIST = "expenseList";
    private static final String KEY_TOTAL = "total";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Description = findViewById(R.id.description);
        Amount = findViewById(R.id.amount);
        AddExpense = findViewById(R.id.add_expense);
        Total = findViewById(R.id.total);
        Expenses = findViewById(R.id.expenses);

        expenseList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseList);
        Expenses.setAdapter(adapter);

        AddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });

        Expenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = expenseList.get(position);
                Toast.makeText(MainActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

        // Restore the state
        if (savedInstanceState != null) {
            expenseList = savedInstanceState.getStringArrayList(KEY_EXPENSE_LIST);
            total = savedInstanceState.getDouble(KEY_TOTAL, 0.0);
        } else {
            loadPreferences();
        }

        adapter.notifyDataSetChanged();
        Total.setText("Total: $" + String.format("%.2f", total));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_EXPENSE_LIST, expenseList);
        outState.putDouble(KEY_TOTAL, total);
        savePreferences();
    }

    private void addExpense() {
        String description = Description.getText().toString().trim();
        String amountText = Amount.getText().toString().trim();

        if (!description.isEmpty() && !amountText.isEmpty()) {
            double amount = Double.parseDouble(amountText);
            total += amount;
            expenseList.add(description + ": $" + String.format("%.2f", amount));
            adapter.notifyDataSetChanged();
            Total.setText("Total: $" + String.format("%.2f", total));
            Description.setText("");
            Amount.setText("");
        }
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_EXPENSE_LIST, new HashSet<>(expenseList));
        editor.putFloat(KEY_TOTAL, (float) total);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> expenseSet = sharedPreferences.getStringSet(KEY_EXPENSE_LIST, new HashSet<>());
        expenseList.addAll(expenseSet);
        total = sharedPreferences.getFloat(KEY_TOTAL, 0.0f);
    }
}
