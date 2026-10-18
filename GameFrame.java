import javax.swing.JFrame;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	GameFrame()
	{
		this.add(new GamePanel());
		this.setTitle("Snake");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();  //This line ensures JFrame covers all components called
		this.setLocationRelativeTo(null);  //Centre the window before showing it so it doesn't jump
		this.setVisible(true);
	}
}
