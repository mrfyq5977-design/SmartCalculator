
package com.smartcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;
import java.text.DecimalFormat;

public class MainActivity extends Activity {

    private TextView expressionView;
    private TextView resultView;
    private String current = "";

    private final int BLACK = Color.rgb(5, 5, 5);
    private final int GOLD = Color.rgb(220, 175, 55);
    private final int WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(14, 20, 14, 16);
        root.setBackgroundColor(BLACK);

        expressionView = new TextView(this);
        expressionView.setTextColor(WHITE);
        expressionView.setTextSize(25);
        expressionView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        expressionView.setSingleLine(true);
        expressionView.setPadding(8, 12, 8, 8);

        resultView = new TextView(this);
        resultView.setTextColor(WHITE);
        resultView.setTextSize(34);
        resultView.setTypeface(null, Typeface.BOLD);
        resultView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        resultView.setPadding(8, 8, 8, 18);
        resultView.setText("0");

        root.addView(expressionView,
            new LinearLayout.LayoutParams(-1, 65));
        root.addView(resultView,
            new LinearLayout.LayoutParams(-1, 75));

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
            line.setOrientation(LinearLayout.HORIZONTAL);

            for (String key : row) {
                Button button = new Button(this);
                button.setText(key);
                button.setTextSize(21);
                button.setAllCaps(false);
                button.setPadding(0, 0, 0, 0);

                boolean operation =
                    key.equals("÷") || key.equals("×") ||
                    key.equals("−") || key.equals("+") ||
                    key.equals("=") || key.equals("√") ||
                    key.equals("%") || key.equals("AC") ||
                    key.equals("⌫");

                button.setTextColor(operation ? GOLD : WHITE);

                GradientDrawable shape = new GradientDrawable();
                shape.setColor(Color.rgb(20, 20, 20));
                shape.setCornerRadius(14);
                shape.setStroke(1, Color.rgb(65, 65, 65));
                button.setBackground(shape);

                LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, 62, 1);
                params.setMargins(4, 5, 4, 5);

                line.addView(button, params);
                button.setOnClickListener(v -> press(key));
            }

            root.addView(line,
                new LinearLayout.LayoutParams(-1, 72));
        }

        setContentView(root);
    }

    private void press(String key) {
        if (key.equals("AC")) {
            current = "";
            expressionView.setText("");
            resultView.setText("0");
            return;
        }

        if (key.equals("⌫")) {
            if (!current.isEmpty()) {
                current = current.substring(0, current.length() - 1);
            }
            expressionView.setText(current);
            return;
        }

        if (key.equals("=")) {
            try {
                double value = calculate(current);
                resultView.setText(format(value));
                expressionView.setText(current);
                current = format(value);
            } catch (Exception e) {
                resultView.setText("خطأ");
            }
            return;
        }

        if (key.equals("√")) {
            try {
                double value = calculate(current);
                if (value < 0) {
                    resultView.setText("خطأ");
                    return;
                }
                current = format(Math.sqrt(value));
                expressionView.setText("√");
                resultView.setText(current);
            } catch (Exception e) {
                resultView.setText("خطأ");
            }
            return;
        }

        if (key.equals("%")) {
            try {
                double value = calculate(current);
                current = format(value / 100.0);
                expressionView.setText(current);
                resultView.setText(current);
            } catch (Exception e) {
                resultView.setText("خطأ");
            }
            return;
        }

        current += key;
        expressionView.setText(current);
    }

    private String format(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "خطأ";
        }
        return new DecimalFormat("0.##########").format(value);
    }

    private double calculate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }
        return new Parser(input).parse();
    }

    private static class Parser {
        private final String s;
        private int pos = -1;
        private int ch;

        Parser(String s) {
            this.s = s.replace("×", "*")
                      .replace("÷", "/")
                      .replace("−", "-");
        }

        void nextChar() {
            ch = (++pos < s.length()) ? s.charAt(pos) : -1;
        }

        boolean eat(int c) {
            while (ch == ' ') nextChar();
            if (ch == c) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            nextChar();
            double x = expression();
            if (pos < s.length()) {
                throw new RuntimeException("خطأ");
            }
            return x;
        }

        double expression() {
            double x = term();
            while (true) {
                if (eat('+')) x += term();
                else if (eat('-')) x -= term();
                else return x;
            }
        }

        double term() {
            double x = factor();
            while (true) {
                if (eat('*')) x *= factor();
                else if (eat('/')) {
                    double d = factor();
                    if (d == 0) throw new RuntimeException("قسمة على صفر");
                    x /= d;
                } else return x;
            }
        }

        double factor() {
            if (eat('+')) return factor();
            if (eat('-')) return -factor();

            double x;
            int start = pos;

            if (eat('(')) {
                x = expression();
                if (!eat(')')) throw new RuntimeException("قوس");
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') {
                    nextChar();
                }
                x = Double.parseDouble(s.substring(start, pos));
            } else {
                throw new RuntimeException("رقم غير صحيح");
            }

            return x;
        }
    }
}
