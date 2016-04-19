
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;
import java.awt.image.renderable.RenderContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LD35 implements KeyListener {

	public static final String SHORT_TITLE = "Cir Squiangle";
	public static final String TITLE = "The Adventures of";
	public static final String PRESS_ANY = "Press any key to begin...";
	public static final long PHYSICS_DELAY = 10,
			GRAPHICS_DELAY = 17;

	public Font menuFont, menuFontSmall;
	public Color menuColor = Color.white;
	public double MOVE = 1;

	public boolean customLevel;
	
	public JFrame f;
	public JPanel p;
	
	public String[] levels;

	public static final int MENU = 0,
			PLAY = 1,
			PAUSE = 2,
			EDITOR = 4;

//	public int state = EDITOR;
	public int state = MENU;

	public Level level;
	public Player player;
	public int width, height;

	public int tx = 0, ty = 0;

	public VolatileImage v;

	public LevelEditor editor;

	public LD35() {

	}

	public void initGUIAndStart() {
		f = new JFrame(SHORT_TITLE);
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
		
//		levelNum = -1;
//		nextLevel();
		
		menuFont = new Font(null, Font.BOLD, height / 10);
		menuFontSmall = new Font(null, Font.PLAIN, height / 20);

		initThreads();

		p.grabFocus();
	}

	public Thread physics, graphics;
	public static int BUFFER = 5;

	public void initThreads() {
		physics = new Thread() {
			public void run() {
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
			}
		};
		physics.start();
		graphics = new Thread() {
			public void run() {	
				while (true) {
					Graphics2D g = (Graphics2D) v.createGraphics();
					g.fillRect(-1, -1, width + 2, height + 2);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
			}
		};
		graphics.start();
	}

	public static LD35 me;

	public static void main(String [] args) {
//		Level l = new Level();
//		LevelIO.writeLevel(l, "test.lvl");
//		File f = new File("/lvl/");
//		f.createNewFile();
		me = new LD35();
		me.loadLevels();
		me.initGUIAndStart();
	}
	private void loadLevels() {
		levels = null;
		CodeSource src = LD35.class.getProtectionDomain().getCodeSource();
	    URL jar = src.getLocation();
		if (jar.toString().endsWith(".jar")) {
		    try {
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				ZipEntry ze = null;
				ArrayList<String> list = new ArrayList<String>();

			    while ((ze = zip.getNextEntry()) != null) {
			        String entryName = ze.getName();
			        if (entryName.endsWith(".lvl") ) {
			            list.add(entryName);
			        }
			    }
			    levels = new String[list.size()];
			    levels = list.toArray(levels);
			    Arrays.sort(levels);
			} catch (IOException e) {}
		} else {
			File dir = new File("lvl");
			levels = dir.list();
			Arrays.sort(levels);
		}
	}

	int phase;
	int levelNum = 0;
	Level oldLevel;
	Player oldPlayer;
	int otx, oty;
	static final int PHASE = 20;
	public void nextLevel() {
		if (customLevel) {
			player = new Player(level);
			return;
		}
		oldLevel = level;
		oldPlayer = player;
		if (level instanceof TutorialLevel) p.removeKeyListener((TutorialLevel) level);
		otx = tx;
		oty = ty;
		tx = 0;
		ty = 0;
		phase = PHASE;
//		level = new Level();
		if (levelNum >= levels.length) {
			resetLevel();
		} else {
			if (levelNum < 0) {
				level = new TutorialLevel();
				p.addKeyListener((TutorialLevel) level);
				levelNum++;
			} else {
				level = LevelIO.readLevel(levels[levelNum++]);
			}
			player = new Player(level);
		}
	}
	
	public void resetLevel() {
		levelNum--;
		nextLevel();
	}

	public void gamePhysics() {
		player.physics();
		if (player.win) nextLevel();
		if (player.dead) resetLevel();
	}

	public void menuGraphics(Graphics2D g) {
		Font temp = g.getFont();
		g.setColor(menuColor);
		g.setFont(menuFont);
		g.drawString(TITLE, (width - SwingUtilities.computeStringWidth(g.getFontMetrics(), TITLE)) / 2, height / 4);
		g.drawString(SHORT_TITLE, (width - SwingUtilities.computeStringWidth(g.getFontMetrics(), SHORT_TITLE)) / 2, height / 4 + g.getFontMetrics().getHeight());
		g.setFont(menuFontSmall);
		g.drawString(PRESS_ANY, (width - SwingUtilities.computeStringWidth(g.getFontMetrics(), PRESS_ANY)) / 2, height * 3 / 4);
		g.setFont(temp);
	}
	final static int PADDING = 100;
	public void gameGraphics(Graphics2D g) {
		if (phase > PHASE/2) {
			if (oldLevel == null) {
				menuGraphics(g);
			} else {
				g.translate(-otx, -oty);
				oldLevel.draw(g);
				oldPlayer.draw(g);
				g.translate(otx, oty);
			}
			g.setColor(new Color(0, 0, 0, 2*(1-(float)(phase-1)/PHASE)));
			g.fillRect(0, 0, width, height);
			phase--;
			return;
		}
		
		if (player.x < tx + PADDING) tx = player.x - PADDING;
		if (player.x > tx + width - PADDING) tx = player.x - width + PADDING;
		if (player.y < ty + PADDING) ty = player.y - PADDING;
		if (player.y > ty + height - PADDING) ty = player.y - height + PADDING;
		tx = Math.max(level.border.x, Math.min(level.border.x+level.border.width-width, tx));
		ty = Math.max(level.border.y, Math.min(level.border.y+level.border.height-height, ty));

		g.translate(-tx, -ty);

		level.draw(g);
		//		player.angle += .1;
		player.draw(g);

		g.translate(tx, ty);
		
		Font f = g.getFont();
		g.setFont(f.deriveFont(20f));
		g.setColor(Color.white);
		if (levelNum == 0) {
			g.drawString("Tutorial", 10, 30);
		} else if (levelNum < levels.length) {
			g.drawString("Level " + levelNum + " of " + ~-levels.length, 10, 30);
		}
		g.setFont(f);
		
		if (phase > 0) {
			try {
				g.setColor(new Color(0, 0, 0, 2f*phase/PHASE));
				g.fillRect(0, 0, width, height);
			} catch (Exception e) {}
			phase--;
		}
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
			case KeyEvent.VK_O:
				if (e.isControlDown()) {
					String s = (String)JOptionPane.showInputDialog(
							null,
							"Level Number?",
							"Level Editor",
							JOptionPane.PLAIN_MESSAGE,
							null,
							null,
							"");
					customLevel = true;
					level = LevelIO.readLevel(s + ".lvl");
					player = new Player(level);
				}
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
			levelNum = -1;
			level = null;
			nextLevel();
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
		Font f = g.getFont();
		g.setFont(f.deriveFont(40f));
		g.drawString("Paused", (width-SwingUtilities.computeStringWidth(g.getFontMetrics(), "Paused"))/2, (height+40)/2);
		g.setFont(f);
	}
}
