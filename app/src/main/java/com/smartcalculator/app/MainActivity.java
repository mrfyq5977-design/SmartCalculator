
package com.smartcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    private TextView display;
    private String current = "";
    private double first = 0;
    private String operator = "";
    private boolean newNumber = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 30, 18, 18);
        root.setBackgroundColor(Color.rgb(12, 20, 36));

        TextView title = new TextView(this);
        title.setText("الحاسبة الذكية");
        title.setTextColor(Color.WHITE);
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        display = new TextView(this);
        display.setText("0");
        display.setTextColor(Color.WHITE);
        display.setTextSize(42);
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setPadding(12, 20, 12, 20);
        root.addView(display, new LinearLayout.LayoutParams(
            -1, 150
        ));

        String[][] keys = {
            {"AC", "⌫", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "−"},
            {"1", "2", "3", "+"},
            {"√", "0", ".", "="}
        };

        for (String[] row : keys) {
            LinearLayout line = new LinearLayout(this);
            line.setGravity(Gravity.CENTER);
            for (String key : row) {
                Button button = new Button(this);
                button.setText(key);
                button.setTextSize(22);
                button.setTextColor(Color.WHITE);
                button.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                        isOperator(key)
                            ? Color.rgb(20, 125, 180)
                            : Color.rgb(35, 49, 70)
                    )
                );

                LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, 75, 1);
                params.setMargins(4, 5, 4, 5);
                line.addView(button, params);

                button.setOnClickListener(v -> press(key));
            }
            root.addView(line);
        }

        setContentView(root);
    }

    private boolean isOperator(String key) {
        return key.equals("÷") || key.equals("×")
            || key.equals("−") || key.equals("+")
            || key.equals("=");
    }

    private void press(String key) {
        if (key.equals("AC")) {
            current = "";
            operator = "";
            first = 0;
            newNumber = true;
            show("0");
            return;
        }

        if (key.equals("⌫")) {
            if (!current.isEmpty()) {
                current = current.substring(0, current.length() - 1);
                show(current.isEmpty() ? "0" : current);
            }
            return;
        }

        if (key.equals("√")) {
            double n = getNumber();
            if (n < 0) {
                show("خطأ");
                current = "";
                return;
            }
            current = format(Math.sqrt(n));
            show(current);
            return;
        }

        if (key.equals("%")) {
            current = format(getNumber() / 100.0);
            show(current);
            return;
        }

        if ("÷×−+".contains(key) && key.length() == 1) {
            first = getNumber();
            operator = key;
            newNumber = true;
            return;
        }

        if (key.equals("=")) {
            if (operator.isEmpty()) return;

            double second = getNumber();
            double result = 0;

            switch (operator) {
                case "+": result = first + second; break;
                case "−": result = first - second; break;
                case "×": result = first * second; break;
                case "÷":
                    if (second == 0) {
                        show("لا يمكن القسمة على صفر");
                        current = "";
                        operator = "";
                        newNumber = true;
                        return;
                    }
                    result = first / second;
                    break;
            }

            current = format(result);
            operator = "";
            newNumber = true;
            show(current);
            return;
        }

        if (key.equals(".")) {
            if (newNumber) {
                current = "0";
                newNumber = false;
            }
            if (!current.contains(".")) current += ".";
            show(current);
            return;
        }

        if (key.matches("[0-9]")) {
            if (newNumber) {
                current = "";
                newNumber = false;
            }
            if (current.length() < 15) current += key;
            show(current);
        }
    }

    private double getNumber() {
        try {
            return current.isEmpty() ? 0 : Double.parseDouble(current);
        } catch (Exception e) {
            return 0;
        }
    }

    private String format(double number) {
        if (Double.isNaN(number) || Double.isInfinite(number))
            return "خطأ";
        if (number == Math.floor(number)
                && Math.abs(number) < 1e15)
            return String.valueOf((long) number);
        return String.valueOf(number);
    }

    private void show(String value) {
        display.setText(value);
    }
}
