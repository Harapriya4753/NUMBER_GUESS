import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NumberGuessingGameGUI extends JFrame {

    // Difficulty levels (bonus)
    enum Difficulty {
        EASY("Easy (1-50, 10 attempts)", 50, 10),
        MEDIUM("Medium (1-100, 7 attempts)", 100, 7),
        HARD("Hard (1-200, 5 attempts)", 200, 5);

        final String label;
        final int maxNumber;
        final int maxAttempts;

        Difficulty(String label, int maxNumber, int maxAttempts) {
            this.label = label;
            this.maxNumber = maxNumber;
            this.maxAttempts = maxAttempts;
        }

        @Override
        public String toString() { return label; }
    }

    // Game state
    private final Random random = new Random();
    private Difficulty difficulty = Difficulty.MEDIUM;
    private int secret;
    private int attempts;
    private int round = 0;
    private boolean roundActive = false;

    // GUI components
    private final JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());
    private final JButton newGameButton = new JButton("New Round");
    private final JLabel infoLabel = new JLabel("Choose a difficulty and click New Round", SwingConstants.CENTER);
    private final JLabel attemptsLabel = new JLabel("Attempts: 0/0", SwingConstants.CENTER);
    private final JTextField guessField = new JTextField(8);
    private final JButton guessButton = new JButton("Guess");
    private final JLabel hintLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JTextArea scoreArea = new JTextArea(6, 30);

    public NumberGuessingGameGUI() {
        super("Number Guessing Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top: difficulty + new round
        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("Difficulty:"));
        difficultyBox.setSelectedItem(Difficulty.MEDIUM);
        top.add(difficultyBox);
        top.add(newGameButton);
        add(top, BorderLayout.NORTH);

        // Center: info, attempts, input, hint
        JPanel center = new JPanel(new GridLayout(4, 1, 5, 5));
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        attemptsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        hintLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel inputRow = new JPanel(new FlowLayout());
        inputRow.add(new JLabel("Your guess:"));
        inputRow.add(guessField);
        inputRow.add(guessButton);

        center.add(infoLabel);
        center.add(attemptsLabel);
        center.add(inputRow);
        center.add(hintLabel);
        add(center, BorderLayout.CENTER);

        // Bottom: scoreboard
        scoreArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(scoreArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Scoreboard"));
        add(scroll, BorderLayout.SOUTH);

        // Events
        newGameButton.addActionListener(e -> startRound());
        guessButton.addActionListener(e -> handleGuess());
        guessField.addActionListener(e -> handleGuess());   // Enter key also submits

        setGuessingEnabled(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void setGuessingEnabled(boolean enabled) {
        guessField.setEnabled(enabled);
        guessButton.setEnabled(enabled);
        if (enabled) guessField.requestFocusInWindow();
    }

    private void startRound() {
        difficulty = (Difficulty) difficultyBox.getSelectedItem();
        secret = random.nextInt(difficulty.maxNumber) + 1;   // 1 to maxNumber
        attempts = 0;
        round++;
        roundActive = true;

        infoLabel.setText("Round " + round + ": guess a number between 1 and " + difficulty.maxNumber);
        updateAttempts();
        hintLabel.setText(" ");
        hintLabel.setForeground(Color.BLACK);
        guessField.setText("");
        setGuessingEnabled(true);
    }

    private void updateAttempts() {
        attemptsLabel.setText("Attempts: " + attempts + "/" + difficulty.maxAttempts);
    }

    private void handleGuess() {
        if (!roundActive) return;

        int guess;
        try {
            guess = Integer.parseInt(guessField.getText().trim());
        } catch (NumberFormatException ex) {
            showHint("Enter a whole number!", Color.GRAY);
            return;                                  // invalid input doesn't use an attempt
        }
        if (guess < 1 || guess > difficulty.maxNumber) {
            showHint("Range is 1-" + difficulty.maxNumber, Color.GRAY);
            return;
        }

        attempts++;
        updateAttempts();
        guessField.setText("");

        if (guess > secret) {
            showHint("Too High!", Color.RED);
        } else if (guess < secret) {
            showHint("Too Low!", Color.BLUE);
        } else {
            showHint("Correct!", new Color(0, 130, 0));
            scoreArea.append("Round " + round + " (" + difficulty.name() + ") - guessed in "
                    + attempts + " attempts\n");
            endRound("You won in " + attempts + " attempts!");
            return;
        }

        if (attempts >= difficulty.maxAttempts) {
            showHint("You Lost!", Color.RED);
            scoreArea.append("Round " + round + " (" + difficulty.name() + ") - lost (number was "
                    + secret + ")\n");
            endRound("You Lost! The number was " + secret + ".");
        }
    }

    private void showHint(String text, Color color) {
        hintLabel.setText(text);
        hintLabel.setForeground(color);
    }

    private void endRound(String message) {
        roundActive = false;
        setGuessingEnabled(false);
        int choice = JOptionPane.showConfirmDialog(this,
                message + "\n\nPlay again?", "Round Over",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            startRound();
        } else {
            infoLabel.setText("Click New Round to play again");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberGuessingGameGUI().setVisible(true));
    }
}
