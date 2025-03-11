import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class oop_project {
    private static final int CODE_LENGTH = 4; // Length of the secret code
    private static final int MAX_ATTEMPTS = 10; // Maximum attempts allowed
    private static int[] secretCode;
    private static int attempts = 0;

    public static void main(String[] args) {
        generateSecretCode();
        createGameWindow();
    }

    private static void generateSecretCode() {
        Random random = new Random();
        secretCode = new int[CODE_LENGTH];
        for (int i = 0; i < CODE_LENGTH; i++) {
            secretCode[i] = random.nextInt(10); // Digits between 0 and 9
        }
    }

    private static void createGameWindow() {
        // Show instructions to the user
        JOptionPane.showMessageDialog(
            null,
            "Welcome to Code Breaker!\n\n" +
            "Instructions:\n" +
            "1. The secret code is a 4-digit number.\n" +
            "2. Each digit is between 0 and 9.\n" +
            "3. Enter your guess in the input field and press Submit.\n" +
            "4. Feedback will be given for each guess:\n" +
            "   - 'C' means correct digit and correct position.\n" +
            "   - 'W' means correct digit but wrong position.\n" +
            "5. You have 10 attempts to crack the code.\n\n" +
            "Good luck!",
            "Game Instructions",
            JOptionPane.INFORMATION_MESSAGE
        );

        // Create the game window
        JFrame frame = new JFrame("Code Breaker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Code Breaker", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.BLUE);
        frame.add(title, BorderLayout.NORTH);

        // Main Game Panel
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        JLabel inputLabel = new JLabel("Enter your guess: ");
        JTextField inputField = new JTextField(10);
        JButton submitButton = new JButton("Submit");
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(submitButton);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Panel to hold message and image side by side
        JPanel messageImagePanel = new JPanel();
        messageImagePanel.setLayout(new BorderLayout());
        frame.add(messageImagePanel, BorderLayout.EAST);

        // Message Panel (for messages)
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout());
        JLabel messageLabel = new JLabel("Good luck cracking the code!");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        messagePanel.add(messageLabel);
        messageImagePanel.add(messagePanel, BorderLayout.NORTH);

        // Image Panel (for images)
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        messageImagePanel.add(imagePanel, BorderLayout.CENTER);

        // Button Action Listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processGuess(inputField, frame, gamePanel, messageLabel, imageLabel, messagePanel, imagePanel);
            }
        });

        // KeyListener for Enter key
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processGuess(inputField, frame, gamePanel, messageLabel, imageLabel, messagePanel, imagePanel);
                }
            }
        });

        frame.setVisible(true);
    }

    private static void processGuess(JTextField inputField, JFrame frame, JPanel gamePanel, JLabel messageLabel, JLabel imageLabel, JPanel messagePanel, JPanel imagePanel) {
        String input = inputField.getText().trim();

        // Validate Input
        if (input.length() != CODE_LENGTH || !input.matches("\\d+")) {
            JOptionPane.showMessageDialog(frame, "Invalid input! Enter a " + CODE_LENGTH + "-digit number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] guess = convertToArray(input);
        String feedback = provideFeedback(guess);

        // Add feedback to the game panel
        JLabel attemptLabel = new JLabel("Attempt " + (attempts + 1) + ": " + input + " - " + feedback);
        attemptLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gamePanel.add(attemptLabel);
        gamePanel.revalidate();

        attempts++;

        // Check for Win or Loss
        if (isCorrectGuess(guess)) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You've cracked the code!", "You Win!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (attempts >= MAX_ATTEMPTS) {
            StringBuilder codeString = new StringBuilder();
            for (int digit : secretCode) codeString.append(digit);
            JOptionPane.showMessageDialog(frame, "Game Over! The code was: " + codeString, "Game Over", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Update the message and image panel
        String encouragement = getEncouragement(feedback);
        messageLabel.setText(encouragement);
        imageLabel.setIcon(getEncouragementImage(feedback));

        messagePanel.revalidate();
        imagePanel.revalidate();

        inputField.setText("");
    }

    private static int[] convertToArray(String guess) {
        int[] array = new int[guess.length()];
        for (int i = 0; i < guess.length(); i++) {
            array[i] = guess.charAt(i) - '0';
        }
        return array;
    }

    private static String provideFeedback(int[] guess) {
        boolean[] usedInCode = new boolean[CODE_LENGTH];
        boolean[] usedInGuess = new boolean[CODE_LENGTH];
        StringBuilder feedback = new StringBuilder();

        // Correct digit and position
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (guess[i] == secretCode[i]) {
                feedback.append("C"); // Correct digit and position
                usedInCode[i] = true;
                usedInGuess[i] = true;
            }
        }

        // Correct digit but wrong position
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (!usedInGuess[i]) {
                for (int j = 0; j < CODE_LENGTH; j++) {
                    if (!usedInCode[j] && guess[i] == secretCode[j]) {
                        feedback.append("W"); // Correct digit, wrong position
                        usedInCode[j] = true;
                        break;
                    }
                }
            }
        }

        return feedback.toString();
    }

    private static String getEncouragement(String feedback) {
        if (feedback.contains("C")) {
            return "Great job! Keep it up, you're getting closer!";
        } else if (feedback.contains("W")) {
            return "Almost there! Some digits are correct.";
        } else {
            return "Don't give up! You'll get it next time.";
        }
    }

    private static ImageIcon getEncouragementImage(String feedback) {
        String path;
        if (feedback.contains("C")) {
            path = "C:\\Users\\saiteja\\OneDrive\\Desktop\\49-492356_success-png-transparent-images-stairs-to-success-png.png"; // Success image
        } else if (feedback.contains("W")) {
            path = "C:\\Users\\saiteja\\Downloads\\—Pngtree—like emoji thumbs up face_18956094.png"; // Image for partial success
        } else {
            path = "C:\\Users\\saiteja\\OneDrive\\Desktop\\pngtree-never-give-up-motivation-poster-concept-black-and-white-illustration-png-image_2154318.jpg"; // Motivational image
        }

        // Load and scale the image
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private static boolean isCorrectGuess(int[] guess) {
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (guess[i] != secretCode[i]) {
                return false;
            }
        }
        return true;
    }
}
