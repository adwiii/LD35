
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LD35 implements KeyListener {

	public static final String TITLE = "TITLE";
	public static final String PRESS_ANY = "Press any key to begin...";
	public static final long PHYSICS_DELAY = 10,
			GRAPHICS_DELAY = 17;

	public Font menuFont, menuFontSmall;
	public Color menuColor = Color.white;
	public double MOVE = 1;

	public JFrame f;
	public JPanel p;

	public static final int MENU = 0,
			PLAY = 1,
			PAUSE = 2,
			EDITOR = 4;

	public int state = EDITOR;

	public Level level;
	public Player player;
	public int width, height;

	public int tx = 0, ty = 0;

	public VolatileImage v;

	public LevelEditor editor;

	public LD35() {

	}

	public void initGUIAndStart() {
		f = new JFrame(TITLE);
		p = new JPanel();
		p.addKeyListener(this);
		f.setResizable(false); // maybe
		p.setPreferredSize(new Dimension(width = 1024, height = 768));
		f.getContentPane().add(p);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.pack();
		f.setLocationRelativeTo(null);
		v = p.createVolatileImage(width, height);

		editor = new LevelEditor(width, height);
		p.addMouseListener(editor);
		p.addMouseMotionListener(editor);
		p.addKeyListener(editor);


		menuFont = new Font(null, Font.BOLD, height / 10);
		menuFontSmall = new Font(null, Font.PLAIN, height / 20);

		level = new Level();
		player = new Player(400, 600, level);

		initThreads();

		p.grabFocus();
	}

	public Thread physics, graphics;
	public static int BUFFER = 5;

	public void initThreads() {
		physics = new Thread(() -> {
			while (true) {
				switch (state) {
				case PLAY: gamePhysics();
				break;
				case EDITOR: editor.physics();
				break;
				}
				try {
					Thread.sleep(PHYSICS_DELAY);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		physics.start();
		graphics = new Thread(() -> {
			while (true) {
				Graphics2D g = (Graphics2D) v.createGraphics();
				g.fillRect(-1, -1, width + 2, height + 2);
				switch (state) {
				case PAUSE: pausedGraphics(g);
				case PLAY: gameGraphics(g);
				break;
				case MENU: menuGraphics(g);
				break;
				case EDITOR: editor.drawEditor(g);
				break;
				}
				g.dispose();
				p.getGraphics().drawImage(v, 0, 0, null);
				try {
					Thread.sleep(GRAPHICS_DELAY);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		graphics.start();
	}

	public static LD35 me;

	public static void main(String [] args) {
		me = new LD35();
		me.initGUIAndStart();
	}


	public void gamePhysics() {
		player.physics();
	}

	public void menuGraphics(Graphics2D g) {
		Font temp = g.getFont();
		g.setColor(menuColor);
		g.setFont(menuFont);
		g.drawString(TITLE, (width - SwingUtilities.computeStringWidth(g.getFontMetrics(), TITLE)) / 2, height / 4);
		g.setFont(menuFontSmall);
		g.drawString(PRESS_ANY, (width - SwingUtilities.computeStringWidth(g.getFontMetrics(), PRESS_ANY)) / 2, height * 3 / 4);
		g.setFont(temp);
	}
	final static int PADDING = 100;
	public void gameGraphics(Graphics2D g) {
		if (player.x < tx + PADDING) tx = player.x - PADDING;
		if (player.x > tx + width - PADDING) tx = player.x - width + PADDING;
		if (player.y < ty + PADDING) ty = player.y - PADDING;
		if (player.y > ty + height - PADDING) ty = player.y - height + PADDING;
		tx = Math.max(level.border.x, Math.min(level.border.width-width, tx));
		ty = Math.max(level.border.y, Math.min(level.border.height-height, ty));

		g.translate(-tx, -ty);

		level.draw(g);
		//		player.angle += .1;
		player.draw(g);

		g.translate(tx, ty);
	}

	public boolean left, right, space, jump;

	@Override
	public void keyPressed(KeyEvent e) {
		if (state == PLAY) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				left = true;
				break;
			case KeyEvent.VK_D:
				right = true;
				break;
			case KeyEvent.VK_SPACE:
				if (!space) jump = true;
				space = true;
				break;
			case KeyEvent.VK_LEFT:
				player.transition(Player.CIRCLE);
				break;
			case KeyEvent.VK_DOWN:
				player.transition(Player.SQUARE);
				break;
			case KeyEvent.VK_RIGHT:
				player.transition(Player.TRIANGLE);
				break;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (state == PAUSE) {
				state = PLAY;
			} else if (state == PLAY) {
				state = PAUSE;
			}
		}
		if (state == MENU) {
			state = PLAY;
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (state == PLAY) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				left = false;
				break;
			case KeyEvent.VK_D:
				right = false;
				break;
			case KeyEvent.VK_SPACE:
				space = false;
				jump = false;
				break;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	public void pausedGraphics(Graphics2D g) {
		g.setColor(Color.red);
		g.drawString("PAUSED", 10, 15);
	}
}
