import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;    //This shows the size of components in the game
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);    //This shows the number of grid cells in the game
	static final int DELAY = 150;    //A very high number gives a slow game
	static final int INITIAL_BODY_PARTS = 6;
	static final int SCORE_ROWS = 2;    //Top rows kept free of apples so they don't sit under the score text
	static final String FONT_NAME = pickFontName();
	final int x[] = new int[GAME_UNITS];    //The x parts of components like the snake
	final int y[] = new int[GAME_UNITS];
	final Color colors[] = new Color[GAME_UNITS];    //A fixed colour for each body segment
	int bodyParts;
	int applesEaten;
	int appleX;    //The x position for which the apple randomly appears
	int appleY;    //The y position for which the apple randomly appears
	char direction;        //The direction the snake moved on the last tick
	char nextDirection;    //The direction the player chose for the next tick
	boolean running = false;
	Timer timer;
	Random random;


	GamePanel()
	{
		random = new Random();
		timer = new Timer(DELAY, this);
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}

	//"Ink Free" only ships with Windows, so fall back to a standard font elsewhere
	private static String pickFontName()
	{
		String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		return Arrays.asList(available).contains("Ink Free") ? "Ink Free" : Font.SANS_SERIF;
	}

	public void startGame()
	{
		bodyParts = INITIAL_BODY_PARTS;
		applesEaten = 0;
		direction = 'R';
		nextDirection = 'R';
		Arrays.fill(x, 0);
		Arrays.fill(y, 0);
		for (int i = 0; i < bodyParts; i++) {
			colors[i] = randomColor();
		}
		newApple();
		running = true;
		timer.restart();
	}

	private Color randomColor()
	{
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g)
	{
		if (running)
		{
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.green);
				}
				else {
					g.setColor(colors[i]);
				}
				g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
			}

			drawScore(g);
		}
		else {
			gameOver(g);
		}
	}

	private void drawScore(Graphics g)
	{
		g.setColor(Color.red);
		g.setFont(new Font(FONT_NAME, Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Score :" + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score :" + applesEaten)) / 2, g.getFont().getSize());
	}

	public void newApple()
	{
		//Keep picking a position until it isn't on the snake's body
		boolean onSnake;
		do {
			appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
			appleY = (SCORE_ROWS + random.nextInt(SCREEN_HEIGHT / UNIT_SIZE - SCORE_ROWS)) * UNIT_SIZE;
			onSnake = false;
			for (int i = 0; i < bodyParts; i++) {
				if (x[i] == appleX && y[i] == appleY) {
					onSnake = true;
					break;
				}
			}
		} while (onSnake);
	}

	public void move()
	{
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		direction = nextDirection;
		switch (direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;

		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;

		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;

		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}

	public void checkApple()
	{
		if ((x[0] == appleX) && (y[0] == appleY)) {
			colors[bodyParts] = randomColor();
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}

	public void checkCollisions()
	{
		//This checks if head collides with body
		for (int i = bodyParts - 1; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		// Checking if head touches left border
		if (x[0] < 0) {
			running = false;
		}
		// Checking if head touches right border
		if (x[0] >= SCREEN_WIDTH) {
			running = false;
		}
		// Checking if head touches top border
		if (y[0] < 0) {
			running = false;
		}
		// Checking if head touches bottom border
		if (y[0] >= SCREEN_HEIGHT) {
			running = false;
		}
		if (!running) {
			timer.stop();
		}
	}

	public void gameOver(Graphics g)
	{
		//Setting Score Text
		drawScore(g);

		//Setting Game Over Text
		g.setColor(Color.red);
		g.setFont(new Font(FONT_NAME, Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

		//Setting Restart Text
		g.setFont(new Font(FONT_NAME, Font.BOLD, 30));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		String restart = "Press SPACE to play again";
		g.drawString(restart, (SCREEN_WIDTH - metrics3.stringWidth(restart)) / 2, SCREEN_HEIGHT / 2 + 60);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {
		//Turns are checked against the direction of the last move, not the last key press,
		//so two quick key presses within one tick can't reverse the snake into itself
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyCode())
			{
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					nextDirection = 'L';
				}
				break;

			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					nextDirection = 'R';
				}
				break;

			case KeyEvent.VK_UP:
				if (direction != 'D') {
					nextDirection = 'U';
				}
				break;

			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					nextDirection = 'D';
				}
				break;

			case KeyEvent.VK_SPACE:
				if (!running) {
					startGame();
					repaint();
				}
				break;
			}
		}
	}
}
