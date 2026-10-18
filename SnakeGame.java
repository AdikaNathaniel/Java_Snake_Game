import javax.swing.SwingUtilities;

public class SnakeGame {

	public static void main(String[] args)
	{
		// Swing components must be created on the Event Dispatch Thread
		SwingUtilities.invokeLater(GameFrame::new);
	}
}
