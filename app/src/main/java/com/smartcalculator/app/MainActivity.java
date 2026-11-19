
package com.smartcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.animation.ValueAnimator;
import android.animation.ArgbEvaluator;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.ViewGroup;
import android.util.TypedValue;

import java.math.BigDecimal;

public class MainActivity extends Activity {

    private TextView expressionView;
    private TextView resultView;

    private String input = "";
    private boolean resultShown = false;

    private LinearLayout root;
    private ValueAnimator backgroundAnimator;

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

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(25), dp(16), dp(16));

        setContentView(root);

        startBackgroundAnimation();
        createInterface();
    }

    private void startBackgroundAnimation() {
        final int[] colors = {
            Color.rgb(10, 25, 55),
            Color.rgb(20, 75, 105),
            Color.rgb(70, 35, 105),
            Color.rgb(10, 80, 85)
        };

        backgroundAnimator = ValueAnimator.ofObject(
            new ArgbEvaluator(),
            colors[0],
            colors[1],
            colors[2],
            colors[3],
            colors[0]
        );

        backgroundAnimator.setDuration(10000);
        backgroundAnimator.setRepeatCount(ValueAnimator.INFINITE);
        backgroundAnimator.setRepeatMode(ValueAnimator.RESTART);

        backgroundAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();

            GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    color,
                    Color.rgb(12, 20, 45)
                }
            );

            root.setBackground(background);
        });

        backgroundAnimator.start();
    }

    private GradientDrawable shape(
        int color,
        int radius
    ) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), Color.argb(45, 255, 255, 255));
        return drawable;
    }

    private TextView makeText(
        String text,
        int size,
        int color
    ) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    private void createInterface() {

        TextView title = makeText(
            "الآلة الحاسبة الذكية",
            25,
            Color.WHITE
        );

        title.setTypeface(null, 1);

        root.addView(title, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(65)
        ));

        LinearLayout display = new LinearLayout(this);
        display.setOrientation(LinearLayout.VERTICAL);
        display.setGravity(Gravity.CENTER_VERTICAL);
        display.setPadding(dp(18), dp(12), dp(18), dp(12));
        display.setBackground(shape(
            Color.rgb(22, 37, 58),
            22
        ));

        LinearLayout.LayoutParams displayParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(135)
            );

        displayParams.setMargins(0, dp(10), 0, dp(20));
        root.addView(display, displayParams);

        expressionView = makeText("", 22, Color.LTGRAY);
        expressionView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        resultView = makeText("0", 34, Color.WHITE);
        resultView.setTypeface(null, 1);
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

            rowParams.setMargins(0, dp(3), 0, dp(3));
            root.addView(line, rowParams);

            for (String key : row) {

                Button button = new Button(this);
                button.setText(key);
                button.setTextSize(21);
                button.setTextColor(Color.WHITE);
                button.setAllCaps(false);
                button.setBackground(shape(
                    getButtonColor(key),
                    18
                ));

                LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1
                    );

                buttonParams.setMargins(dp(4), dp(4), dp(4), dp(4));

                line.addView(button, buttonParams);

                button.setOnClickListener(v -> press(key));
            }
        }

        updateDisplay();
    }

    private int getButtonColor(String key) {

        if (key.equals("=")) {
            return Color.rgb(15, 145, 110);
        }

        if (key.equals("AC") || key.equals("⌫")) {
            return Color.rgb(170, 65, 75);
        }

        if ("÷×−+%√".contains(key)) {
            return Color.rgb(35, 95, 145);
        }

        return Color.rgb(35, 48, 68);
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
                if (number.isEmpty()) {
                    input += "0";
                }
                input += ".";
            }

            updateDisplay();
            return;
        }

        if ("+−×÷".contains(key)) {

            if (input.isEmpty()) {
                if (key.equals("−")) {
                    input = "−";
                }
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
                value = value / 100.0;
            }

            input = input.substring(0, start + 1) + format(value);
            resultShown = false;
            updateDisplay();

        } catch (Exception e) {
            resultView.setText("خطأ");
        }
    }

    private void updateDisplay() {

        expressionView.setText(input.isEmpty() ? "" : input);

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

                    if (operator == '+') {
                        value += next;
                    } else {
                        value -= next;
                    }
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

    @Override
    protected void onDestroy() {
        if (backgroundAnimator != null) {
            backgroundAnimator.cancel();
        }
        super.onDestroy();
    }
}
