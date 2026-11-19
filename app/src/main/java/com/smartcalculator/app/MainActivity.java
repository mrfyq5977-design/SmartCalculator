
package com.smartcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.util.TypedValue;

import java.math.BigDecimal;

public class MainActivity extends Activity {

    private final int GOLD = Color.rgb(212, 166, 43);
    private final int LIGHT_GOLD = Color.rgb(245, 211, 120);
    private final int BLACK = Color.rgb(15, 15, 15);
    private final int DARK = Color.rgb(32, 32, 32);

    private TextView expressionView;
    private TextView resultView;

    private String input = "";
    private boolean resultShown = false;

    private final String[][] keys = {
        {"AC", "⌫", "%", "÷"},
        {"7", "8", "9", "×"},
        {"4", "5", "6", "−"},
        {"1", "2", "3", "+"},
        {"√", "0", ".", "="}
    };

    private int dp(float value) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            getResources().getDisplayMetrics()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BLACK);
        getWindow().setNavigationBarColor(BLACK);

        showCalculator();
    }

    private GradientDrawable background(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), GOLD);
        return drawable;
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    private Button makeButton(String value, int color, int textColor) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(16);
        button.setTextColor(textColor);
        button.setAllCaps(false);
        button.setTypeface(null, Typeface.BOLD);
        button.setBackground(background(color, 16));
        return button;
    }

    private void showCalculator() {

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(12), dp(12), dp(12), dp(12));
        screen.setBackgroundColor(BLACK);

        setContentView(screen);

        TextView heading = text(
            "الآلة الحاسبة الذكية",
            21,
            LIGHT_GOLD
        );

        heading.setTypeface(null, Typeface.BOLD);

        screen.addView(heading,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        );

        LinearLayout display = new LinearLayout(this);
        display.setOrientation(LinearLayout.VERTICAL);
        display.setGravity(Gravity.CENTER_VERTICAL);
        display.setPadding(dp(15), dp(12), dp(15), dp(12));
        display.setBackground(background(DARK, 20));

        LinearLayout.LayoutParams displayParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(120)
            );

        displayParams.setMargins(0, dp(12), 0, dp(15));
        screen.addView(display, displayParams);

        expressionView = text("", 17, LIGHT_GOLD);
        expressionView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        resultView = text("0", 29, LIGHT_GOLD);
        resultView.setTypeface(null, Typeface.BOLD);
        resultView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        display.addView(expressionView);
        display.addView(resultView);

        for (String[] row : keys) {

            LinearLayout line = new LinearLayout(this);
            line.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1
                );

            rowParams.setMargins(0, dp(2), 0, dp(2));
            screen.addView(line, rowParams);

            for (String key : row) {

                Button button = makeButton(
                    key,
                    getKeyColor(key),
                    getKeyTextColor(key)
                );

                LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1
                    );

                buttonParams.setMargins(
                    dp(3), dp(4), dp(3), dp(4)
                );

                line.addView(button, buttonParams);

                button.setOnClickListener(v -> press(key));
            }
        }

        updateDisplay();
    }

    private int getKeyColor(String key) {

        if (key.equals("=")) return GOLD;

        if (key.equals("AC") || key.equals("⌫")) {
            return Color.rgb(115, 48, 35);
        }

        if ("÷×−+%√".contains(key)) {
            return Color.rgb(115, 83, 25);
        }

        return DARK;
    }

    private int getKeyTextColor(String key) {
        if (key.equals("=")) return BLACK;
        return LIGHT_GOLD;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '−' ||
               c == '×' || c == '÷';
    }

    private void press(String key) {

        if (key.equals("AC")) {
            input = "";
            resultShown = false;
            updateDisplay();
            return;
        }

        if (key.equals("⌫")) {
            if (!input.isEmpty()) {
                input = input.substring(0, input.length() - 1);
            }

            resultShown = false;
            updateDisplay();
            return;
        }

        if (key.equals("=")) {
            calculateFinal();
            return;
        }

        if (key.equals("%")) {
            applyUnary(false);
            return;
        }

        if (key.equals("√")) {
            applyUnary(true);
            return;
        }

        if ("0123456789".contains(key)) {

            if (resultShown) {
                input = "";
                resultShown = false;
            }

            input += key;
            updateDisplay();
            return;
        }

        if (key.equals(".")) {

            if (resultShown) {
                input = "";
                resultShown = false;
            }

            int start = input.length() - 1;

            while (start >= 0 &&
                   !isOperator(input.charAt(start))) {
                start--;
            }

            String number = input.substring(start + 1);

            if (!number.contains(".")) {
                if (number.isEmpty()) input += "0";
                input += ".";
            }

            updateDisplay();
            return;
        }

        if ("+−×÷".contains(key)) {

            if (input.isEmpty()) {
                if (key.equals("−")) input = "−";
            } else {
                char last = input.charAt(input.length() - 1);

                if (isOperator(last)) {
                    input = input.substring(0, input.length() - 1);
                }

                input += key;
            }

            resultShown = false;
            updateDisplay();
        }
    }

    private void applyUnary(boolean squareRoot) {

        if (input.isEmpty()) return;

        int start = input.length() - 1;

        while (start >= 0 &&
               !isOperator(input.charAt(start))) {
            start--;
        }

        String number = input.substring(start + 1);

        try {
            double value = Double.parseDouble(number);

            if (squareRoot) {
                if (value < 0) {
                    resultView.setText("رقم غير صالح");
                    return;
                }
                value = Math.sqrt(value);
            } else {
                value /= 100.0;
            }

            input = input.substring(0, start + 1) + format(value);
            resultShown = false;
            updateDisplay();

        } catch (Exception e) {
            resultView.setText("خطأ");
        }
    }

    private void updateDisplay() {

        expressionView.setText(input);

        if (input.isEmpty()) {
            resultView.setText("0");
            return;
        }

        String candidate = input;

        if (isOperator(candidate.charAt(candidate.length() - 1))) {
            candidate = candidate.substring(0, candidate.length() - 1);
        }

        if (candidate.isEmpty() || candidate.endsWith(".")) {
            resultView.setText("...");
            return;
        }

        try {
            double answer = new Parser(candidate).parse();
            resultView.setText(format(answer));
        } catch (Exception e) {
            resultView.setText("...");
        }
    }

    private void calculateFinal() {

        if (input.isEmpty()) return;

        try {
            double answer = new Parser(input).parse();

            String oldExpression = input;
            String finalAnswer = format(answer);

            expressionView.setText(oldExpression);
            resultView.setText(finalAnswer);

            input = finalAnswer;
            resultShown = true;

        } catch (ArithmeticException e) {
            resultView.setText("لا يمكن القسمة على صفر");
        } catch (Exception e) {
            resultView.setText("عملية غير مكتملة");
        }
    }

    private String format(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "خطأ";
        }

        return BigDecimal.valueOf(value)
            .stripTrailingZeros()
            .toPlainString();
    }

    private static class Parser {

        private final String text;
        private int position = 0;

        Parser(String text) {
            this.text = text;
        }

        double parse() {
            double value = expression();

            if (position != text.length()) {
                throw new IllegalArgumentException();
            }

            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new ArithmeticException();
            }

            return value;
        }

        private double expression() {
            double value = term();

            while (position < text.length()) {
                char operator = text.charAt(position);

                if (operator == '+' || operator == '−') {
                    position++;
                    double next = term();

                    if (operator == '+') value += next;
                    else value -= next;

                } else {
                    break;
                }
            }

            return value;
        }

        private double term() {
            double value = factor();

            while (position < text.length()) {
                char operator = text.charAt(position);

                if (operator == '×' || operator == '÷') {
                    position++;
                    double next = factor();

                    if (operator == '×') {
                        value *= next;
                    } else {
                        if (next == 0) {
                            throw new ArithmeticException();
                        }
                        value /= next;
                    }

                } else {
                    break;
                }
            }

            return value;
        }

        private double factor() {

            if (position < text.length() &&
                (text.charAt(position) == '−' ||
                 text.charAt(position) == '+')) {

                char sign = text.charAt(position++);
                double value = factor();

                return sign == '−' ? -value : value;
            }

            int start = position;
            boolean dot = false;

            while (position < text.length()) {

                char c = text.charAt(position);

                if (c >= '0' && c <= '9') {
                    position++;
                } else if (c == '.' && !dot) {
                    dot = true;
                    position++;
                } else {
                    break;
                }
            }

            if (start == position) {
                throw new IllegalArgumentException();
            }

            return Double.parseDouble(
                text.substring(start, position)
            );
        }
    }
}
