import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class LevelEditor implements MouseListener, MouseMotionListener, KeyListener {

	public static final Color GOAL = new Color(0x80FF0000);

	public ArrayList<Point> vertices = new ArrayList<Point>();
	public ArrayList<Connection> connections = new ArrayList<Connection>();

	public Rectangle goal;

	public Point start;

	public boolean playing;
	public boolean paused;

	public boolean goalSelected = false, 
			startSelected = false;

	public int oldGoalX, oldGoalY;

	public LevelEditor (int width, int height) {
		goal = new Rectangle(width /2, height / 2, width / 10, height / 10);
		start = new Point(width / 2, height / 4);
	}

	public static final int RAD = 10;

	public ArrayList<Point> selected = new ArrayList<Point>();

	public int tx = 0, ty = 0;
	public void drawEditor(Graphics2D g) {
		if (playing) {
			if (paused) LD35.me.pausedGraphics(g);
			LD35.me.gameGraphics(g);
		} else {
			g.setColor(Color.white);
			for (Point p : vertices) {
				g.drawOval(p.x - RAD, p.y - RAD, RAD * 2, RAD * 2);
			}
			for (Connection c : connections) {
				c.draw(g);
			}
			g.setColor(Color.magenta);
			for (Point p : selected) {
				g.drawOval(p.x - RAD, p.y - RAD, RAD * 2, RAD * 2);
			}
			g.setColor(Color.red);
			g.draw(goal);
			g.setColor(GOAL);
			g.fill(goal);

			g.setColor(Color.green);
			g.drawOval(start.x - RAD, start.y - RAD, RAD * 2, RAD * 2);
		}
	}
	public void physics() {
		if (playing && !paused) {
			LD35.me.player.physics();
			if (LD35.me.player.win || LD35.me.player.dead) playing = false;
		}
	}
	public int cx = 0, cy = 0;
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON2) {
			tx = cx - e.getX();
			ty = cy - e.getY();
		}
		if (selected.size() == 1) {
			//			System.out.println("dragging");
			selected.get(0).x = e.getX() - tx;
			selected.get(0).y = e.getY() - ty;
		}
		if (goalSelected) {
			goal.x = oldGoalX - cx + e.getX();
			goal.y = oldGoalY - cy + e.getY();
		}
		if (startSelected) {
			start.x = e.getX();
			start.y = e.getY();
		}

	}
	public Level generateLevel() {
		ArrayList<Line2D.Double> lines = new ArrayList<>();
		for (Connection c : connections) {
			lines.add(c.getLine());
		}
		return new Level(lines, goal, start);
	}
	public void preview() {
		LD35.me.level =  generateLevel();
		LD35.me.player = new Player(start.x, start.y, LD35.me.level);
		playing = true;
		paused = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		if (playing) return;
		cx = e.getX();
		cy = e.getY();
		//		System.out.println(e.getPoint());
		boolean found = false;
		for (Point p : vertices) {
			if (Math.hypot(p.x - cx, p.y - cy) < RAD) {
				if (selected.contains(p)) selected.clear();
				selected.add(p);
				found = true;
				break;
			}
		}
		if (!found) {
			selected.clear();
			if (goal.contains(e.getPoint())) {
				oldGoalX = goal.x;
				oldGoalY = goal.y;
				goalSelected = true;
			}
			if (Math.hypot(start.x - cx, start.y - cy) < RAD) {
				startSelected = true;
			}
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (selected.size() == 0){
				vertices.add(new Point(e.getX() - tx, e.getY() - ty));
			} else if (selected.size() == 2) {
				if (found) {
					connections.add(new Connection(selected));
				} else {
					Connection check = new Connection(selected);
					for (Connection c : connections) {
						c.equals(check);
						connections.remove(c);
						break;
					}
				}
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		goalSelected = false;
		startSelected = false;
	}

	public static class Connection {
		public Point v1;
		public Point v2;
		public Connection (ArrayList<Point> points) {
			v1 = points.get(0);
			v2 = points.get(1);
		}
		public Line2D.Double getLine() {
			return new Line2D.Double(v1.x, v1.y, v2.x, v2.y);
		}
		@Override
		public boolean equals(Object other) {
			return other instanceof Connection && ((Connection) other).v1 == v1 && ((Connection) other).v2 == v2;
		}
		public void draw(Graphics2D g) {
			g.drawLine(v1.x, v1.y, v2.x, v2.y);			
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
			String s = (String)JOptionPane.showInputDialog(
                    null,
                    "Level Number?",
                    "Customized Dialog",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
			LevelIO.writeLevel(generateLevel(), "lvl/" + s + ".lvl");
		}
		if (playing) {
			if (!paused) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_A:
					LD35.me.left = true;
					break;
				case KeyEvent.VK_D:
					LD35.me.right = true;
					break;
				case KeyEvent.VK_SPACE:
					if (!LD35.me.space) LD35.me.jump = true;
					LD35.me.space = true;
					break;
				case KeyEvent.VK_LEFT:
					LD35.me.player.transition(Player.CIRCLE);
					break;
				case KeyEvent.VK_DOWN:
					LD35.me.player.transition(Player.SQUARE);
					break;
				case KeyEvent.VK_RIGHT:
					LD35.me.player.transition(Player.TRIANGLE);
					break;
				case KeyEvent.VK_ESCAPE:
					paused = true;
					break;	
				}
			} else {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					paused = false;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				playing = false;
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				selected.clear();
				break;
			case KeyEvent.VK_ENTER:
				preview();
				break;
			case KeyEvent.VK_DELETE:
				//				if (selected.size() =) {
				Point p;
				vertices.remove(p = selected.remove(0));
				for (int i = 0; i < connections.size(); i++) {
					if (connections.get(i).v1.equals(p) || connections.get(i).v2.equals(p)) {
						connections.remove(i--);
					}
				}
			}
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		if (playing && !paused) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				LD35.me.left = false;
				break;
			case KeyEvent.VK_D:
				LD35.me.right = false;
				break;
			case KeyEvent.VK_SPACE:
				LD35.me.space = false;
				LD35.me.jump = false;
				break;
			}
		}
	}


	@Override
	public void keyTyped(KeyEvent e) {}
}
