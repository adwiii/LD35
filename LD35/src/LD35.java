
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LD35 implements KeyListener {
	
	public static final String TITLE = "TITLE";
	public static final long PHYSICS_DELAY = 17,
							 GRAPHICS_DELAY = 17;
	
	public double MOVE = 1;
	
	public JFrame f;
	public JPanel p;
	
	public Level level;
	public Player player;
	public int width, height;
	
	public int tx = 0, ty = 0;
	
	public VolatileImage v;
	
	public LD35() {
		f = new JFrame(TITLE);
		p = new JPanel();
		p.addKeyListener(this);
		f.setResizable(false); // maybe
		p.setPreferredSize(new Dimension(width = 800, height = 600));
		f.getContentPane().add(p);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.pack();
		f.setLocationRelativeTo(null);
		v = p.createVolatileImage(width, height);
		
		level = new Level();
		player = new Player(400, 300);
		
		initThreads();
		
		p.grabFocus();
	}
	
	public Thread physics, graphics;
	public static int BUFFER = 5;

	public void initThreads() {
		physics = new Thread(() -> {
			while (true) {
				if (left) {
					System.out.println("left");
					player.x -= MOVE;
				}
				if (right) {
					System.out.println("right");
					player.x += MOVE;
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
//				if (player.x - player.radius - BUFFER < tx) {
//					tx = player.x - player.radius - BUFFER;
//				}
//				if (player.x + player.radius + BUFFER < width - tx) {
//					tx = width - player.x - player.radius - BUFFER;
//				}
//				if (player.y - player.radius - BUFFER < ty) {
//					ty = player.y - player.radius - BUFFER;
//				}
//				if (player.y + player.radius + BUFFER < height - ty) {
//					ty = height - player.y - player.radius - BUFFER;
//				}
				g.translate(tx, ty);
//				level.draw(g);
				player.draw(g);
				player.angle += .01;
				g.translate(-tx, -ty);
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
	}

	public boolean left, right, space;
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			left = true;
			break;
		case KeyEvent.VK_D:
			right = true;
			break;
		case KeyEvent.VK_SPACE:
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

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			left = false;
			break;
		case KeyEvent.VK_D:
			right = false;
			break;
		case KeyEvent.VK_SPACE:
			space = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
