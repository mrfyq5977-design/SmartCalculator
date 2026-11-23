
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
import android.content.Context;
import java.text.DecimalFormat;

public class MainActivity extends Activity {

    private TextView expressionView;
    private TextView resultView;
    private LinearLayout scientificPanel;

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
        root.setPadding(8, 8, 8, 8);
        root.setBackgroundColor(BLACK);

        expressionView = new TextView(this);
        expressionView.setTextColor(WHITE);
        expressionView.setTextSize(24);
        expressionView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        expressionView.setSingleLine(true);
        expressionView.setPadding(8, 4, 8, 4);

        resultView = new TextView(this);
        resultView.setTextColor(WHITE);
        resultView.setTextSize(36);
        resultView.setTypeface(null, Typeface.BOLD);
        resultView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        resultView.setSingleLine(true);
        resultView.setPadding(8, 4, 8, 4);
        resultView.setText("0");

        root.addView(expressionView,
                new LinearLayout.LayoutParams(-1, 55));

        root.addView(resultView,
                new LinearLayout.LayoutParams(-1, 70));

        // مساحة مرنة بين النتيجة ولوحة الأزرار
        View space = new View(this);
        root.addView(space,
                new LinearLayout.LayoutParams(-1, 0, 2f));

        // لوحة الأزرار تستغل المساحة المتبقية
        LinearLayout keypadPanel = new LinearLayout(this);
        keypadPanel.setOrientation(LinearLayout.VERTICAL);

        root.addView(keypadPanel,
                new LinearLayout.LayoutParams(-1, 0, 5f));

        // زر الحذف و SCI
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        addToolbarButton(toolbar, "⌫");
        addToolbarButton(toolbar, "SCI");

        keypadPanel.addView(toolbar,
                new LinearLayout.LayoutParams(-1, 38));

        // الحاسبة العلمية مخفية في البداية
        scientificPanel = new LinearLayout(this);
        scientificPanel.setOrientation(LinearLayout.VERTICAL);
        scientificPanel.setVisibility(View.GONE);

        keypadPanel.addView(scientificPanel,
                new LinearLayout.LayoutParams(-1, 0, 2f));

        String[][] scientificKeys = {
            {"sin", "cos", "tan", "log"},
            {"ln", "π", "e", "x²"},
            {"√", "", "", ""}
        };

        for (String[] row : scientificKeys) {
            addRow(scientificPanel, row);
        }

        // الأزرار العادية بنفس ترتيب الصورة الأولى
        LinearLayout normalPanel = new LinearLayout(this);
        normalPanel.setOrientation(LinearLayout.VERTICAL);

        keypadPanel.addView(normalPanel,
                new LinearLayout.LayoutParams(-1, 0, 5f));

        String[][] normalKeys = {
            {"AC", "()", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "−"},
            {"1", "2", "3", "+"},
            {"+/−", "0", ".", "="}
        };

        for (String[] row : normalKeys) {
            addRow(normalPanel, row);
        }

        setContentView(root);
    }

    // زر الحذف وزر العلمية
    private void addToolbarButton(LinearLayout toolbar, String key) {

        TextView button = new TextView(this);

        button.setText(key);
        button.setTextColor(GOLD_LIGHT);
        button.setTextSize(key.equals("SCI") ? 15 : 25);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(3, 0, 3, 0);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(42, 38);

        params.setMargins(2, 0, 2, 0);
        toolbar.addView(button, params);

        if (key.equals("SCI")) {
            button.setOnClickListener(v -> {
                if (scientificPanel.getVisibility() == View.GONE) {
                    scientificPanel.setVisibility(View.VISIBLE);
                } else {
                    scientificPanel.setVisibility(View.GONE);
                }
            });
        }

        if (key.equals("⌫")) {
            button.setOnClickListener(v -> press("⌫"));

            button.setOnLongClickListener(v -> {
                clearAll();
                return true;
            });
        }
    }

    // زر دائري
    private class CircleButton extends Button {

        public CircleButton(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec,
                                 int heightMeasureSpec) {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int side = Math.min(
                    getMeasuredWidth(),
                    getMeasuredHeight()
            );

            setMeasuredDimension(side, side);
        }
    }

    // صفوف مرنة تتكيف مع حجم الشاشة
    private void addRow(LinearLayout root, String[] keys) {

        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setGravity(Gravity.CENTER);

        for (String key : keys) {
            addButton(line, key);
        }

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(-1, 0, 1f);

        root.addView(line, rowParams);
    }

    private void addButton(LinearLayout line, String key) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, -1, 1f);

        params.setMargins(3, 3, 3, 3);
        params.gravity = Gravity.CENTER;

        if (key.isEmpty()) {
            line.addView(new View(this), params);
            return;
        }

        CircleButton button = new CircleButton(this);

        button.setText(key);
        button.setAllCaps(false);
        button.setTypeface(null, Typeface.BOLD);
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
                key.equals("√") ||
                key.equals("()") ||
                key.equals("+/−") ||
                key.equals("sin") ||
                key.equals("cos") ||
                key.equals("tan") ||
                key.equals("log") ||
                key.equals("ln") ||
                key.equals("π") ||
                key.equals("e") ||
                key.equals("x²");

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
        button.setTextSize(key.length() > 2 ? 16 : 22);

        line.addView(button, params);

        button.setOnClickListener(v -> press(key));
    }

    private void clearAll() {
        current = "";
        justCalculated = false;
        expressionView.setText("");
        resultView.setText("0");
    }

    private void press(String key) {

        if (key.equals("AC")) {
            clearAll();
            return;
        }

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
            calculateFinal();
            return;
        }

        if (key.equals("√")) {
            justCalculated = false;
            current += "sqrt(";
            updateDisplay();
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

        switch (key) {

            case "sin":
            case "cos":
            case "tan":
            case "log":
            case "ln":
                current += key + "(";
                break;

            case "π":
                current += "pi";
                break;

            case "e":
                current += "e";
                break;

            case "x²":
                current += "^2";
                break;

            case "()":

                if (current.isEmpty() ||
                        endsWithOperator(current) ||
                        current.endsWith("(")) {
                    current += "(";
                } else {
                    current += ")";
                }
                break;

            default:
                current += key;
                break;
        }

        updateDisplay();
    }

    private void calculateFinal() {

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
    }

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
               c == '(' || c == '^';
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

    // محلل العمليات الحسابية والعلمية
    private static class Parser {

        private final String s;
        private int pos = -1;
        private int ch;

        Parser(String input) {

            s = input.replace("×", "*")
                     .replace("÷", "/")
                     .replace("−", "-")
                     .replace("π", "pi");
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

            double value = power();

            while (true) {

                if (eat('*')) {
                    value *= power();

                } else if (eat('/')) {

                    double divisor = power();

                    if (divisor == 0) {
                        throw new RuntimeException("قسمة على صفر");
                    }

                    value /= divisor;

                } else {
                    return value;
                }
            }
        }

        double power() {

            double value = factor();

            if (eat('^')) {
                value = Math.pow(value, power());
            }

            return value;
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

            } else if ((ch >= '0' && ch <= '9') || ch == '.') {

                int start = pos;
                boolean hasDigit = false;
                boolean hasDot = false;

                while ((ch >= '0' && ch <= '9') || ch == '.') {

                    if (ch == '.') {
                        if (hasDot) {
                            throw new RuntimeException("رقم غير صحيح");
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

                value = Double.parseDouble(s.substring(start, pos));

            } else if (Character.isLetter(ch)) {

                int start = pos;

                while (Character.isLetter(ch)) {
                    nextChar();
                }

                String name = s.substring(start, pos);

                switch (name) {

                    case "pi":
                        value = Math.PI;
                        break;

                    case "e":
                        value = Math.E;
                        break;

                    case "sin":
                    case "cos":
                    case "tan":
                    case "log":
                    case "ln":
                    case "sqrt":

                        if (!eat('(')) {
                            throw new RuntimeException("قوس مطلوب");
                        }

                        double argument = expression();

                        if (!eat(')')) {
                            throw new RuntimeException("قوس غير مكتمل");
                        }

                        switch (name) {

                            case "sin":
                                value = Math.sin(Math.toRadians(argument));
                                break;

                            case "cos":
                                value = Math.cos(Math.toRadians(argument));
                                break;

                            case "tan":
                                value = Math.tan(Math.toRadians(argument));
                                break;

                            case "log":
                                value = Math.log10(argument);
                                break;

                            case "ln":
                                value = Math.log(argument);
                                break;

                            default:
                                value = Math.sqrt(argument);
                                break;
                        }

                        break;

                    default:
                        throw new RuntimeException("عملية غير معروفة");
                }

            } else {
                throw new RuntimeException("رقم غير صحيح");
            }

            while (eat('%')) {
                value /= 100.0;
            }

            return value;
        }
    }
}
