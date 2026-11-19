
package com.smartcalculator.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import android.content.res.ColorStateList;

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

    private final int BG = Color.rgb(12, 20, 36);
    private final int PANEL = Color.rgb(25, 38, 57);
    private final int BUTTON = Color.rgb(35, 49, 70);
    private final int BLUE = Color.rgb(20, 125, 180);
    private final int GREEN = Color.rgb(65, 210, 145);
    private final int WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 22, 16, 12);
        root.setBackgroundColor(BG);

        TextView title = new TextView(this);
        title.setText("الحاسبة الذكية");
        title.setTextColor(WHITE);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // منطقة عرض العملية والنتيجة
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_VERTICAL);
        screen.setPadding(18, 12, 18, 12);
        screen.setBackgroundColor(PANEL);

        expressionView = new TextView(this);
        expressionView.setText("");
        expressionView.setTextColor(Color.LTGRAY);
        expressionView.setTextSize(23);
        expressionView.setGravity(Gravity.RIGHT);
        screen.addView(expressionView);

        resultView = new TextView(this);
        resultView.setText("0");
        resultView.setTextColor(GREEN);
        resultView.setTextSize(38);
        resultView.setGravity(Gravity.RIGHT);
        resultView.setPadding(0, 8, 0, 4);
        screen.addView(resultView);

        LinearLayout.LayoutParams screenParams =
            new LinearLayout.LayoutParams(
                -1, 125
            );
        screenParams.setMargins(0, 14, 0, 12);
        root.addView(screen, screenParams);

        // أزرار الحاسبة
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

                int color = isOperator(key)
                    ? BLUE : BUTTON;

                button.setBackgroundTintList(
                    ColorStateList.valueOf(color)
                );

                LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, 65, 1);

                params.setMargins(4, 3, 4, 3);

                line.addView(button, params);

                button.setOnClickListener(v -> press(key));
            }

            root.addView(line);
        }

        // مساحة الكتابة أسفل الأزرار
        TextView writingTitle = new TextView(this);
        writingTitle.setText("مساحة الكتابة");
        writingTitle.setTextColor(WHITE);
        writingTitle.setTextSize(17);
        writingTitle.setPadding(4, 10, 4, 5);
        root.addView(writingTitle);

        writingArea = new EditText(this);
        writingArea.setHint("اكتب ملاحظاتك هنا...");
        writingArea.setTextColor(WHITE);
        writingArea.setHintTextColor(Color.GRAY);
        writingArea.setTextSize(16);
        writingArea.setGravity(Gravity.TOP | Gravity.RIGHT);
        writingArea.setPadding(12, 10, 12, 10);
        writingArea.setBackgroundTintList(
            ColorStateList.valueOf(BLUE)
        );
        writingArea.setInputType(
            InputType.TYPE_CLASS_TEXT |
            InputType.TYPE_TEXT_FLAG_MULTI_LINE |
            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );
        writingArea.setSingleLine(false);
        writingArea.setMinLines(2);
        writingArea.setMaxLines(4);

        LinearLayout.LayoutParams writingParams =
            new LinearLayout.LayoutParams(
                -1, 0, 1
            );

        writingParams.setMargins(0, 0, 0, 4);
        root.addView(writingArea, writingParams);

        setContentView(root);
        updateDisplay();
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
                        resultView.setText("لا يمكن القسمة على صفر");
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
            resultView.setText(current.isEmpty() ? "0" : current);
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
                return second == 0
                    ? 0
                    : first / second;

            default:
                return second;
        }
    }
}
