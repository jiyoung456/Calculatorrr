import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class calculator extends JFrame {

    private JTextField inputSpace; // 결과 창
    private JTextArea processDisplay; // 과정 창
    private String num = "";
    private String prev_operaton = "";
    private ArrayList<String> equation = new ArrayList<>();
    private JLabel infoLabel;

    public calculator() {
        setLayout(new BorderLayout());

        JPanel displayPanel = new JPanel(new BorderLayout());

        processDisplay = new JTextArea();
        processDisplay.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        processDisplay.setLineWrap(true);
        processDisplay.setWrapStyleWord(true);
        processDisplay.setEditable(false);
        processDisplay.setFont(new Font("Arial", Font.PLAIN, 20));
        JScrollPane scrollPane = new JScrollPane(processDisplay);
        displayPanel.add(scrollPane, BorderLayout.NORTH);

        inputSpace = new JTextField("");
        inputSpace.setEditable(false);
        inputSpace.setBackground(Color.WHITE);
        inputSpace.setHorizontalAlignment(JTextField.RIGHT);
        inputSpace.setFont(new Font("Arial", Font.BOLD, 50));
        displayPanel.add(inputSpace, BorderLayout.CENTER);

        add(displayPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4, 10, 10));

        String button_names[] = {"%", "CE", "C", "<<", "1/x", "x²", "2√x", "÷", "7", "8", "9", "×", "4", "5", "6", "-", "1", "2", "3", "+", "+/-", "0", ".", "="};
        JButton buttons[] = new JButton[button_names.length];

        for (int i = 0; i < button_names.length; i++) {
            buttons[i] = new JButton(button_names[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 20));
            if (button_names[i].equals("=")) buttons[i].setBackground(Color.red);
            else buttons[i].setBackground(Color.white);
            buttons[i].setForeground(Color.black);
            buttons[i].setBorderPainted(false);
            buttons[i].addActionListener(new PadActionListener());
            buttonPanel.add(buttons[i]);
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.CENTER);

        setTitle("계산기");
        setVisible(true);
        setSize(350, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class PadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String operation = e.getActionCommand();

            DecimalFormat df = new DecimalFormat("#.##########");

            if (operation.equals("C")) {
                inputSpace.setText("");
                num = "";
                prev_operaton = "";
                infoLabel.setText("");

            } else if (operation.equals("<<")) {
                String currentText = inputSpace.getText();
                if (currentText.length() > 0) {
                    inputSpace.setText(currentText.substring(0, currentText.length() - 1));
                }

            } else if (operation.equals("%")) {
                double inputValue = Double.parseDouble(inputSpace.getText());
                double percentageValue = inputValue * 0.01;
                inputSpace.setText(df.format(percentageValue));;
            } else if (operation.equals("CE")) {
                inputSpace.setText("");
            } else if (operation.equals("1/x")) {
                double inputValue = Double.parseDouble(inputSpace.getText());
                double reciprocalValue = 1.0 / inputValue;
                inputSpace.setText(df.format(reciprocalValue));
            } else if (operation.equals("x²")) {
                double inputValue = Double.parseDouble(inputSpace.getText());
                double squaredValue = inputValue * inputValue;
                inputSpace.setText(df.format(squaredValue));
            } else if (operation.equals("2√x")) {
                double inputValue = Double.parseDouble(inputSpace.getText());
                double sqrtValue = Math.sqrt(inputValue);
                inputSpace.setText(df.format(sqrtValue));

            } else if (operation.equals("=")) {
                double doubleResult = calculate(inputSpace.getText());
                String result;
                // 결과 값이 정수인 경우 정수로 반환
                if (doubleResult == (int) doubleResult) {
                    result = Integer.toString((int) doubleResult);
                } else {
                    result = Double.toString(doubleResult);
                }
                inputSpace.setText(result);
                num = "";

                // 이전 입력 숫자와 연산 기록 표시
                processDisplay.setText(equationToString(equation));
                equation.clear();

                // 연산자 중복 입력 방지
                // 비어있는 창에 연산자 입력 방지("-"인 경우 제외)
            } else if (operation.equals("+") || operation.equals("-") || operation.equals("×") || operation.equals("÷")) {
                if (inputSpace.getText().equals("") && operation.equals("-")) {
                    inputSpace.setText(inputSpace.getText() + e.getActionCommand());
                } else if (!inputSpace.getText().equals("") && !prev_operaton.equals("+") && !prev_operaton.equals("-") && !prev_operaton.equals("×") && !prev_operaton.equals("÷")) {
                    inputSpace.setText(inputSpace.getText() + e.getActionCommand());
                }
            } else {
                inputSpace.setText(inputSpace.getText() + e.getActionCommand());
            }
            prev_operaton = e.getActionCommand();
        }
    }

    private String equationToString(ArrayList<String> equation) {
        StringBuilder builder = new StringBuilder();
        for (String s : equation) {
            builder.append(s);
        }
        return builder.toString();
    }

    private void fullTextParsing(String inputText) {
        equation.clear();

        for (int i = 0; i < inputText.length(); i++) {
            char ch = inputText.charAt(i);

            if (ch == '-' | ch == '+' | ch == '×' | ch == '÷') {
                equation.add(num);
                num = "";
                equation.add(ch + "");
            } else {
                num = num + ch;
            }
        }
        equation.add(num);
        // 결과값이 음수인 경우
        equation.remove("");
    }

    public double calculate(String inputText) {
        fullTextParsing(inputText);

        double prev = 0;
        double current = 0;
        String mode = "";

        // 결과값이 소수일 경우, 소수점 아래 열 번째까지 표시되게
        DecimalFormat df = new DecimalFormat("#.##########");

        for (String s : equation) {
            if (s.equals("+")) {
                mode = "add";
            } else if (s.equals("-")) {
                mode = "sub";
            } else if (s.equals("×")) {
                mode = "mul";
            } else if (s.equals("÷")) {
                mode = "div";
            } else {
                current = Double.parseDouble(s);
                if (mode.equals("add")) {
                    prev += current;
                } else if (mode.equals("sub")) {
                    prev -= current;
                } else if (mode.equals("mul")) {
                    prev *= current;
                } else if (mode.equals("div")) {
                    prev /= current;
                } else {
                    prev = current;
                }
            }
        }
        String result;
        if (prev == (int) prev) {
            result = Integer.toString((int) prev);
        } else {
            result = df.format(prev);
        }
        return Double.valueOf(result);
    }

    public static void main(String[] args) {
        new calculator();
    }
}