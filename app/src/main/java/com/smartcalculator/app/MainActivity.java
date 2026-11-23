
package com.smartcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;
import java.text.DecimalFormat;

public class MainActivity extends Activity {

    private TextView expressionView;
    private TextView resultView;

    private String current = "";
    private boolean justCalculated = false;

    private final int BLACK = Color.rgb(5, 5, 5);
    private final int GOLD = Color.rgb(210, 165, 45);
    private final int GOLD_LIGHT = Color.rgb(240, 195, 75);
    private final int WHITE = Color.WHITE;
    private final int BUTTON_BLACK = Color.rgb(27, 27, 27);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(BLACK);
        window.setNavigationBarColor(BLACK);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(10, 12, 10, 12);
        root.setBackgroundColor(BLACK);

        // شاشة العملية الحسابية
        expressionView = new TextView(this);
        expressionView.setTextColor(WHITE);
        expressionView.setTextSize(25);
        expressionView.setGravity(Gravity.CENTER);
        expressionView.setSingleLine(true);
        expressionView.setIncludeFontPadding(false);
        expressionView.setPadding(8, 5, 8, 5);

        // شاشة النتيجة
        resultView = new TextView(this);
        resultView.setTextColor(WHITE);
        resultView.setTextSize(36);
        resultView.setTypeface(null, Typeface.BOLD);
        resultView.setGravity(Gravity.CENTER);
        resultView.setSingleLine(true);
        resultView.setIncludeFontPadding(false);
        resultView.setPadding(8, 5, 8, 5);
        resultView.setText("0");

        root.addView(expressionView,
                new LinearLayout.LayoutParams(-1, 65));

        root.addView(resultView,
                new LinearLayout.LayoutParams(-1, 75));

        // مساحة مرنة
        View spacer = new View(this);
        root.addView(spacer,
                new LinearLayout.LayoutParams(-1, 0, 1f));

        // ترتيب الأزرار
        String[][] keys = {
            {"AC", "()", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "−"},
            {"1", "2", "3", "+"},
            {"+/−", "00", "0", "000"},
            {"√", "⌫", ".", "="}
        };

        int widthDp = (int) (
                getResources().getDisplayMetrics().widthPixels /
                getResources().getDisplayMetrics().density
        );

        // حجم موحد للدوائر
        int buttonSize = Math.max(48,
                (widthDp - 20 - 32) / 4);

        for (String[] row : keys) {

            LinearLayout line = new LinearLayout(this);
            line.setGravity(Gravity.CENTER);
            line.setOrientation(LinearLayout.HORIZONTAL);

            for (String key : row) {
                addButton(line, key, buttonSize);
            }

            root.addView(line,
                    new LinearLayout.LayoutParams(
                            -1, buttonSize + 8));
        }

        setContentView(root);
    }

    private void addButton(
            LinearLayout line, String key, int buttonSize) {

        // زر دائري
        Button button = new Button(this);

        button.setText(key);
        button.setTextSize(23);
        button.setTypeface(null, Typeface.BOLD);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setIncludeFontPadding(false);
        button.setPadding(0, 0, 0, 0);
        button.setMinWidth(0);
        button.setMinHeight(0);
        button.setMinimumWidth(0);
        button.setMinimumHeight(0);

        boolean operation =
                key.equals("÷") ||
                key.equals("×") ||
                key.equals("−") ||
                key.equals("+") ||
                key.equals("%") ||
                key.equals("AC") ||
                key.equals("⌫") ||
                key.equals("√") ||
                key.equals("()") ||
                key.equals("+/−");

        boolean equals = key.equals("=");

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);

        if (equals) {
            circle.setColor(GOLD_LIGHT);
            circle.setStroke(2, GOLD_LIGHT);
            button.setTextColor(BLACK);

        } else if (operation) {
            circle.setColor(GOLD);
            circle.setStroke(1, GOLD_LIGHT);
            button.setTextColor(BLACK);

        } else {
            circle.setColor(BUTTON_BLACK);
            circle.setStroke(2, GOLD);
            button.setTextColor(WHITE);
        }

        button.setBackground(circle);
        button.setStateListAnimator(null);

        // عرض متساوٍ لكل زر مع دائرة مربعة
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0, buttonSize, 1f);

        params.setMargins(4, 4, 4, 4);

        line.addView(button, params);

        // ضغطة واحدة
        button.setOnClickListener(v -> press(key));

        // ضغطة مطولة على الحذف تمسح الكل
        if (key.equals("⌫")) {
            button.setOnLongClickListener(v -> {
                current = "";
                justCalculated = false;
                expressionView.setText("");
                resultView.setText("0");
                return true;
            });
        }
    }

    private void press(String key) {

        if (key.equals("AC")) {
            current = "";
            justCalculated = false;
            expressionView.setText("");
            resultView.setText("0");
            return;
        }

        // حذف رقم واحد
        if (key.equals("⌫")) {
            justCalculated = false;

            if (!current.isEmpty()) {
                current = current.substring(
                        0, current.length() - 1);
            }

            updateDisplay();
            return;
        }

        if (key.equals("=")) {
            if (current.isEmpty()) return;

            try {
                String oldExpression = current;
                double value = calculate(current);

                current = format(value);
                expressionView.setText(oldExpression);
                resultView.setText(current);
                justCalculated = true;

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

                String oldExpression = current;
                current = format(Math.sqrt(value));

                expressionView.setText("√(" + oldExpression + ")");
                resultView.setText(current);
                justCalculated = true;

            } catch (Exception e) {
                resultView.setText("خطأ");
            }

            return;
        }

        if (key.equals("+/−")) {
            if (!current.isEmpty()) {
                if (current.startsWith("-")) {
                    current = current.substring(1);
                } else {
                    current = "-" + current;
                }
            } else {
                current = "-";
            }

            justCalculated = false;
            updateDisplay();
            return;
        }

        if (justCalculated) {
            boolean isNumber =
                    key.matches("[0-9]+") ||
                    key.equals(".");

            if (isNumber) {
                current = "";
                expressionView.setText("");
            }

            justCalculated = false;
        }

        if (key.equals("()")) {

            if (current.isEmpty() ||
                    endsWithOperator(current) ||
                    current.endsWith("(")) {
                current += "(";
            } else {
                current += ")";
            }

        } else {
            current += key;
        }

        updateDisplay();
    }

    // تحديث النتيجة أثناء الكتابة
    private void updateDisplay() {

        expressionView.setText(current);

        if (current.trim().isEmpty()) {
            resultView.setText("0");
            return;
        }

        try {
            double value = calculate(current);
            resultView.setText(format(value));

        } catch (Exception e) {
            resultView.setText("");
        }
    }

    private boolean endsWithOperator(String value) {
        if (value.isEmpty()) return true;

        char c = value.charAt(value.length() - 1);

        return c == '+' || c == '−' ||
               c == '×' || c == '÷' ||
               c == '(';
    }

    private String format(double value) {

        if (Double.isNaN(value) ||
                Double.isInfinite(value)) {
            return "خطأ";
        }

        return new DecimalFormat("0.##########")
                .format(value);
    }

    private double calculate(String input) {

        if (input == null || input.trim().isEmpty()) {
            return 0;
        }

        return new Parser(input).parse();
    }

    // محلل العمليات الحسابية
    private static class Parser {

        private final String s;
        private int pos = -1;
        private int ch;

        Parser(String input) {
            s = input.replace("×", "*")
                     .replace("÷", "/")
                     .replace("−", "-");
        }

        void nextChar() {
            ch = (++pos < s.length())
                    ? s.charAt(pos) : -1;
        }

        boolean eat(int character) {

            while (ch == ' ') {
                nextChar();
            }

            if (ch == character) {
                nextChar();
                return true;
            }

            return false;
        }

        double parse() {

            nextChar();

            double value = expression();

            while (ch == ' ') {
                nextChar();
            }

            if (ch != -1) {
                throw new RuntimeException("خطأ");
            }

            return value;
        }

        double expression() {

            double value = term();

            while (true) {

                if (eat('+')) {
                    value += term();

                } else if (eat('-')) {
                    value -= term();

                } else {
                    return value;
                }
            }
        }

        double term() {

            double value = factor();

            while (true) {

                if (eat('*')) {
                    value *= factor();

                } else if (eat('/')) {

                    double divisor = factor();

                    if (divisor == 0) {
                        throw new RuntimeException(
                                "قسمة على صفر");
                    }

                    value /= divisor;

                } else {
                    return value;
                }
            }
        }

        double factor() {

            if (eat('+')) return factor();
            if (eat('-')) return -factor();

            double value;

            if (eat('(')) {

                value = expression();

                if (!eat(')')) {
                    throw new RuntimeException("قوس غير مكتمل");
                }

            } else {

                int start = pos;
                boolean hasDigit = false;
                boolean hasDot = false;

                while ((ch >= '0' && ch <= '9') || ch == '.') {

                    if (ch == '.') {
                        if (hasDot) {
                            throw new RuntimeException(
                                    "رقم غير صحيح");
                        }
                        hasDot = true;
                    } else {
                        hasDigit = true;
                    }

                    nextChar();
                }

                if (!hasDigit) {
                    throw new RuntimeException("رقم غير صحيح");
                }

                value = Double.parseDouble(
                        s.substring(start, pos));
            }

            while (eat('%')) {
                value /= 100.0;
            }

            return value;
        }
    }
}
