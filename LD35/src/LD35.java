import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LD35 implements KeyListener {
	
	public static final String TITLE = "TITLE";
	
	public JFrame f;
	public JPanel p;
	
	public Level level;
	public Player player;
	public int width, height;
	
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
		
		player = new Player(400, 300);
		
		initThreads();
	}
	
	public Thread physics, graphics;

	public void initThreads() {
		physics = new Thread(() -> {
			while (true) {
				
			}
		});
		physics.start();
		graphics = new Thread(() -> {
			while (true) {
				Graphics2D g = (Graphics2D) v.createGraphics();
				g.fillRect(-1, -1, width + 2, height + 2);
//				level.draw(g);
				player.draw(g);
				player.angle += .01;
				g.dispose();
				p.getGraphics().drawImage(v, 0, 0, null);
				try {
					Thread.sleep(17);
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
