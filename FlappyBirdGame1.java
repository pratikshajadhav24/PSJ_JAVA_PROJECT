import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdGame1 extends JPanel implements ActionListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int BIRD_WIDTH = 30;
    private static final int BIRD_HEIGHT = 30;
    private static final int PIPE_WIDTH = 80;
    private static final int PIPE_GAP = 200;
    private static final int PIPE_SPEED = 8;
    private static final int GRAVITY = 1;

    private Timer timer;
    private int birdY;
    private int birdVelocity;
    private ArrayList<Rectangle> pipes;
    private Random random;
    private boolean gameOver;
    private int score;
    private boolean gameStarted;
    private JButton startButton;

    public FlappyBirdGame1() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(135, 206, 250)); // Sky blue background
        setFocusable(true);




        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
        startButton.setBounds(WIDTH / 2 - 70, HEIGHT / 2 - 20, 140, 40);
        startButton.addActionListener(e -> startGame());

        setLayout(null);
        add(startButton); // Add button to panel

        timer = new Timer(20, this);
        random = new Random();
        initializeGame(); // Initialize game
    }

    private void initializeGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        pipes = new ArrayList<>();
        gameOver = false;
        score = 0; // Reset score
        gameStarted = false; // Game not started yet
        addPipe(); // Initial pipes
        startButton.setVisible(true); // Show the start button
    }

    private void startGame() {
        gameStarted = true; // Mark the game as started
        startButton.setVisible(false); // Hide the start button
        timer.start(); // Start the game timer
    }

    private void flap() {
        birdVelocity = -10; // Move bird up
    }

    private void addPipe() {
        int pipeHeight = random.nextInt(HEIGHT - PIPE_GAP - 50) + 50;
        pipes.add(new Rectangle(WIDTH, 0, PIPE_WIDTH, pipeHeight));
        pipes.add(new Rectangle(WIDTH, pipeHeight + PIPE_GAP, PIPE_WIDTH, HEIGHT - pipeHeight - PIPE_GAP));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!gameStarted) {
            // Display start screen
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Flappy Bird", 80, HEIGHT / 2 - 30);
            return;
        }

        g.setColor(Color.ORANGE); // Colorful bird
        g.fillOval(50, birdY, BIRD_WIDTH, BIRD_HEIGHT);

        g.setColor(Color.GREEN); // Green pipes
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // Ground
        g.setColor(new Color(139, 69, 19)); // Brown color for the ground
        g.fillRect(0, HEIGHT - 50, WIDTH, 50);

        // Display score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);

        // Game Over Message
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", 50, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to Restart", 70, HEIGHT / 2 + 50);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && gameStarted) {
            birdY += birdVelocity;
            birdVelocity += GRAVITY;

            // Check for collision with ground
            if (birdY >= HEIGHT - BIRD_HEIGHT - 50) {
                gameOver = true;
            }

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= PIPE_SPEED;

                // Remove pipes that have gone off screen
                if (pipe.x + PIPE_WIDTH < 0) {
                    pipes.remove(i);
                    i--;
                } else if (pipe.x + PIPE_WIDTH == 50) {
                    // Increment score when bird passes a pipe
                    score++;
                }
            }

            // Add new pipes
            if (pipes.isEmpty() || pipes.get(pipes.size() - 1).x < WIDTH - 200) {
                addPipe();
            }

            checkCollision();
        }
        repaint();
    }

    private void checkCollision() {
        Rectangle birdRect = new Rectangle(50, birdY, BIRD_WIDTH, BIRD_HEIGHT);
        for (Rectangle pipe : pipes) {
            if (birdRect.intersects(pipe)) {
                gameOver = true;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird Game");
        FlappyBirdGame1 game = new FlappyBirdGame1();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Key listener for bird flap and restarting the game
        game.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (game.gameOver) {
                        game.initializeGame(); // Restart the game
                    } else {
                        game.flap(); // Flap the bird
                    }
                }
            }
        });
    }
}
