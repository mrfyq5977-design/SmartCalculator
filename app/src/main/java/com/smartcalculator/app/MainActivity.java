
package com.smartcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import android.text.InputType;
import android.animation.ValueAnimator;
import android.animation.ArgbEvaluator;

public class MainActivity extends Activity {

    private TextView expressionView;
    private TextView resultView;
    private EditText writingArea;

    private String current = "";
    private String operator = "";
    private String expression = "";

    private double first = 0;
    private boolean newNumber = true;
    private boolean showingResult = false;

    private ValueAnimator backgroundAnimator;
    private GradientDrawable outerBackground;

    private final int WHITE = Color.WHITE;
    private final int BLUE = Color.rgb(25, 130, 210);
    private final int GOLD = Color.rgb(230, 180, 75);
    private final int GREEN = Color.rgb(65, 220, 155);

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable rounded(
            int[] colors, int radius, int strokeColor) {

        GradientDrawable shape = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                colors
        );

        shape.setCornerRadius(dp(radius));

        if (strokeColor != 0) {
            shape.setStroke(dp(1), strokeColor);
        }

        return shape;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(22), dp(16), dp(12));

        outerBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    Color.rgb(8, 17, 42),
                    Color.rgb(18, 35, 75),
                    Color.rgb(30, 20, 65)
                }
        );

        outerBackground.setCornerRadius(dp(0));
        root.setBackground(outerBackground);

        // عنوان التطبيق
        TextView title = new TextView(this);
        title.setText("✦ الحاسبة الذكية ✦");
        title.setTextColor(GOLD);
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(5), 0, dp(8));
        title.setShadowLayer(dp(12), 0, 0, BLUE);

        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("SMART CALCULATOR");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(10);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);

        // شاشة العمليات والنتائج
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_VERTICAL);
        screen.setPadding(dp(18), dp(12), dp(18), dp(12));

        screen.setBackground(rounded(
                new int[]{
                    Color.rgb(25, 45, 75),
                    Color.rgb(12, 27, 53)
                },
                22,
                Color.rgb(50, 130, 190)
        ));

        expressionView = new TextView(this);
        expressionView.setText("");
        expressionView.setTextColor(Color.rgb(190, 210, 235));
        expressionView.setTextSize(23);
        expressionView.setGravity(Gravity.RIGHT);

        screen.addView(expressionView);

        resultView = new TextView(this);
        resultView.setText("0");
        resultView.setTextColor(GREEN);
        resultView.setTextSize(38);
        resultView.setGravity(Gravity.RIGHT);
        resultView.setPadding(0, dp(8), 0, dp(4));
        resultView.setShadowLayer(dp(8), 0, 0, GREEN);

        screen.addView(resultView);

        LinearLayout.LayoutParams screenParams =
                new LinearLayout.LayoutParams(-1, dp(125));

        screenParams.setMargins(0, dp(15), 0, dp(12));
        root.addView(screen, screenParams);

        // لوحة الأزرار
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
                button.setTextSize(21);
                button.setTextColor(WHITE);
                button.setAllCaps(false);
                button.setPadding(0, 0, 0, 0);

                int[] colors;

                if (key.equals("=")) {
                    colors = new int[]{
                        Color.rgb(25, 160, 115),
                        Color.rgb(10, 105, 90)
                    };
                } else if (isOperator(key)) {
                    colors = new int[]{
                        Color.rgb(35, 130, 205),
                        Color.rgb(20, 65, 135)
                    };
                } else {
                    colors = new int[]{
                        Color.rgb(55, 75, 110),
                        Color.rgb(25, 40, 70)
                    };
                }

                button.setBackground(rounded(
                        colors, 17, Color.rgb(75, 125, 175)
                ));

                button.setElevation(dp(4));

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(0, dp(63), 1);

                params.setMargins(dp(4), dp(4), dp(4), dp(4));

                line.addView(button, params);

                button.setOnClickListener(v -> press(key));
            }

            root.addView(line);
        }

        // مساحة الكتابة
        TextView writingTitle = new TextView(this);
        writingTitle.setText("✎ مساحة الكتابة والملاحظات");
        writingTitle.setTextColor(GOLD);
        writingTitle.setTextSize(17);
        writingTitle.setPadding(dp(4), dp(10), dp(4), dp(6));

        root.addView(writingTitle);

        writingArea = new EditText(this);
        writingArea.setHint("اكتب ملاحظاتك هنا...");
        writingArea.setTextColor(WHITE);
        writingArea.setHintTextColor(Color.LTGRAY);
        writingArea.setTextSize(16);
        writingArea.setGravity(Gravity.TOP | Gravity.RIGHT);
        writingArea.setPadding(dp(14), dp(12), dp(14), dp(12));

        writingArea.setBackground(rounded(
                new int[]{
                    Color.rgb(25, 45, 75),
                    Color.rgb(15, 28, 52)
                },
                16,
                Color.rgb(65, 135, 195)
        ));

        writingArea.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        writingArea.setSingleLine(false);
        writingArea.setMinLines(2);
        writingArea.setMaxLines(4);

        LinearLayout.LayoutParams writingParams =
                new LinearLayout.LayoutParams(-1, 0, 1);

        writingParams.setMargins(0, 0, 0, dp(5));
        root.addView(writingArea, writingParams);

        setContentView(root);

        updateDisplay();
        startBackgroundAnimation();
    }

    // حركة ألوان الخلفية
    private void startBackgroundAnimation() {

        int[] firstColors = {
            Color.rgb(8, 17, 42),
            Color.rgb(18, 35, 75),
            Color.rgb(30, 20, 65)
        };

        int[] secondColors = {
            Color.rgb(12, 35, 65),
            Color.rgb(20, 55, 95),
            Color.rgb(45, 25, 80)
        };

        backgroundAnimator = ValueAnimator.ofFloat(0f, 1f);
        backgroundAnimator.setDuration(5000);
        backgroundAnimator.setRepeatCount(
                ValueAnimator.INFINITE
        );
        backgroundAnimator.setRepeatMode(
                ValueAnimator.REVERSE
        );

        backgroundAnimator.addUpdateListener(animation -> {

            float fraction = (float) animation.getAnimatedValue();

            ArgbEvaluator evaluator = new ArgbEvaluator();

            int[] animatedColors = new int[3];

            for (int i = 0; i < 3; i++) {
                animatedColors[i] = (int) evaluator.evaluate(
                        fraction,
                        firstColors[i],
                        secondColors[i]
                );
            }

            outerBackground.setColors(animatedColors);
        });

        backgroundAnimator.start();
    }

    private boolean isOperator(String key) {
        return key.equals("÷") ||
               key.equals("×") ||
               key.equals("−") ||
               key.equals("+") ||
               key.equals("=");
    }

    private void press(String key) {

        if (key.equals("AC")) {
            current = "";
            operator = "";
            expression = "";
            first = 0;
            newNumber = true;
            showingResult = false;
            updateDisplay();
            return;
        }

        if (key.equals("⌫")) {

            if (!current.isEmpty()) {
                current = current.substring(
                        0, current.length() - 1
                );
            }

            newNumber = current.isEmpty();
            showingResult = false;
            updateDisplay();
            return;
        }

        if (key.equals("√")) {

            double n = getNumber();

            if (n < 0) {
                resultView.setText("خطأ");
                return;
            }

            current = format(Math.sqrt(n));
            operator = "";
            expression = "√(" + format(n) + ")";
            newNumber = true;
            showingResult = true;

            updateDisplay();
            return;
        }

        if (key.equals("%")) {

            double n = getNumber();

            current = format(n / 100.0);
            expression = format(n) + "%";
            operator = "";
            newNumber = true;
            showingResult = true;

            updateDisplay();
            return;
        }

        if ("÷×−+".contains(key) && key.length() == 1) {

            if (current.isEmpty() && !showingResult) {
                return;
            }

            first = getNumber();
            operator = key;
            expression = format(first) + " " + operator;

            current = "";
            newNumber = true;
            showingResult = false;

            updateDisplay();
            return;
        }

        if (key.equals("=")) {

            if (operator.isEmpty() || current.isEmpty()) {
                return;
            }

            double second = getNumber();
            double result;

            switch (operator) {

                case "+":
                    result = first + second;
                    break;

                case "−":
                    result = first - second;
                    break;

                case "×":
                    result = first * second;
                    break;

                case "÷":

                    if (second == 0) {
                        resultView.setText(
                                "لا يمكن القسمة على صفر"
                        );
                        return;
                    }

                    result = first / second;
                    break;

                default:
                    return;
            }

            expression = format(first) + " " +
                         operator + " " +
                         format(second);

            current = format(result);
            operator = "";
            newNumber = true;
            showingResult = true;

            updateDisplay();
            return;
        }

        if (key.equals(".")) {

            if (newNumber) {
                current = "0";
                newNumber = false;
            }

            if (!current.contains(".")) {
                current += ".";
            }

            showingResult = false;
            updateDisplay();
            return;
        }

        if (key.matches("[0-9]")) {

            if (newNumber) {
                current = "";
                newNumber = false;
            }

            if (current.length() < 15) {
                current += key;
            }

            showingResult = false;
            updateDisplay();
        }
    }

    private double getNumber() {
        try {
            return current.isEmpty()
                    ? 0
                    : Double.parseDouble(current);
        } catch (Exception e) {
            return 0;
        }
    }

    private String format(double number) {

        if (Double.isNaN(number) ||
            Double.isInfinite(number)) {
            return "خطأ";
        }

        if (number == Math.floor(number) &&
            Math.abs(number) < 1e15) {
            return String.valueOf((long) number);
        }

        return String.valueOf(number);
    }

    private void updateDisplay() {

        if (showingResult) {
            expressionView.setText(expression);
            resultView.setText(
                    current.isEmpty() ? "0" : current
            );
            return;
        }

        if (!operator.isEmpty()) {

            String second = current.isEmpty() ? "" : current;

            expressionView.setText(
                    format(first) + " " + operator + " " + second
            );

            if (!second.isEmpty()) {
                double result = calculateLive(getNumber());
                resultView.setText(format(result));
            } else {
                resultView.setText(format(first));
            }

        } else {

            expressionView.setText(current);

            resultView.setText(
                    current.isEmpty() ? "0" : current
            );
        }
    }

    private double calculateLive(double second) {

        switch (operator) {

            case "+":
                return first + second;

            case "−":
                return first - second;

            case "×":
                return first * second;

            case "÷":
                return second == 0 ? 0 : first / second;

            default:
                return second;
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
