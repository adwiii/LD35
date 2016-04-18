import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TutorialLevel extends Level implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1533924269474253524L;
	
	public int state = 0;
	
	public static final String [] help = new String [] {
			//0
			"Hello, Cir Squiangle.\nWelcome to your newest adventure.\nUse A and D to move left and right.",
			//1
			"You have the special ability to change shape.\nRight now, you are a Square.",
			//2
			"Press Left Arrow to turn into a Circle\nCircles can roll really fast!",
			//3
			"Press Down Arrow to turn back into a Square.",
			//4
			"While you are a Square, press Space to jump.\nSquare can also jump off walls!",
			//5
			"While you are in the air,\npress Right Arrow to turn into a Triangle",
			//6
			"Triangles can glide through the air, use A and D to tilt!",
			//7
			"Throughout your quests, its important to remember these abilities.",
			//8
			"In each new place you visit, you must try to enter the red gate.",
			
	};
	public TutorialLevel() {
		super();
//		System.out.println("TUT");
		goal = new Rectangle(-1000, -1000, 0, 0);
		f = new Font("", Font.PLAIN, 20);
	}
	int tickTilNext = 0;
	static final int DELAY = 50;
//	int timeout = 100;
	Font f;
	@Override
	public void draw (Graphics2D g) {
		super.draw(g);
		g.setFont(f);
		String [] lines;
		try {
			lines = help[(tickTilNext < DELAY / 2) ? state : state - 1].split("\n");
		} catch(Exception e) {
			lines = help[state].split("\n");
		}
//		timeout = Math.max(0, timeout - 1);
		tickTilNext = Math.max(0, tickTilNext - 1);
//		System.out.println(tickTilNext);
		
		g.setColor(new Color(255, 0, 0, 255 * Math.abs(tickTilNext - DELAY / 2) / (DELAY / 2)));
		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], 250, 200 + g.getFontMetrics().getHeight() * i);
		}
		
	}
	boolean noReset; // force the user to release the key before advancing twice.
	@Override
	public void keyPressed(KeyEvent e) {
		if (noReset) return;
		noReset = true;
//		if (timeout -- > 0) return; // initial so you dont miss anything
		if (tickTilNext-- > 0) return;
		int prevState = state;
		switch (state) {
		case 7: goal = new Rectangle(600, 400, 100, 100);
		case 0:	
//			timeout += 50;
		case 1: state++;
			break;
		case 2:
			if (e.getKeyCode() == KeyEvent.VK_LEFT) state ++;
			break;
		case 3: 
			if (e.getKeyCode() == KeyEvent.VK_DOWN) state ++;
			break;
		case 4:
			if (e.getKeyCode() == KeyEvent.VK_SPACE) state ++;
			break;
		case 5:
			if (!LD35.me.player.onGround && e.getKeyCode() == KeyEvent.VK_RIGHT) state++;
			break;
		case 6:
			if (!LD35.me.player.onGround && LD35.me.player.type == Player.TRIANGLE && (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D)) state++;
			break;
		}
		if (prevState != state) {
			tickTilNext = DELAY;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		noReset = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	

}
