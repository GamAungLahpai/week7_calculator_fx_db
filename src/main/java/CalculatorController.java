import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML private TextField number1Field;
    @FXML private TextField number2Field;
    @FXML private Label resultLabel;

    @FXML
    private void onCalculateClick() {
        try {
            double num1 = Double.parseDouble(number1Field.getText());
            double num2 = Double.parseDouble(number2Field.getText());

            double sum = num1 + num2;
            double product = num1 * num2;

            resultLabel.setText("Sum: " + sum + ", Product: " + product);

            // Save to DB
            ResultService.saveResult(num1, num2, sum, product);

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers!");
        }
    }

    @FXML
    private void onSubtractClick() {
        try {
            double num1 = Double.parseDouble(number1Field.getText());
            double num2 = Double.parseDouble(number2Field.getText());

            double difference = num1 - num2;

            resultLabel.setText("Subtraction: " + num1 + " - " + num2 + " = " + difference);
            ResultService.saveSubtraction(num1, num2, difference);

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers!");
        }
    }

    @FXML
    private void onDivideClick() {
        try {
            double num1 = Double.parseDouble(number1Field.getText());
            double num2 = Double.parseDouble(number2Field.getText());

            if (num2 == 0) {
                resultLabel.setText("Error: Cannot divide by zero!");
                return;
            }

            double quotient = num1 / num2;

            resultLabel.setText("Division: " + num1 + " / " + num2 + " = " + quotient);
            ResultService.saveDivision(num1, num2, quotient);

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers!");
        }
    }
}