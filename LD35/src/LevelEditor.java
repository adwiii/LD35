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
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;

public class LevelEditor implements MouseListener, MouseMotionListener, KeyListener {

	public static final Color GOAL = new Color(0x80FF0000);

	public ArrayList<Point> vertices = new ArrayList<Point>();
	public ArrayList<Connection> connections = new ArrayList<Connection>();

	public Rectangle goal;

	public Point start;

	public boolean goalSelected = false, 
			startSelected = false;

	public int oldGoalX, oldGoalY;

	/*
	 * TODO set the mode of LD35 to EDITOR
	 * TODO add this as MouseListener and MouseMotionListener and KeyListener
	 */
	
	public LevelEditor (int width, int height) {
		goal = new Rectangle(width /2, height / 2, width / 10, height / 10);
	}

	public static final int RAD = 3;

	public ArrayList<Point> selected = new ArrayList<Point>();

	public int tx = 0, ty = 0;
	public void drawEditor(Graphics2D g) {
		g.setColor(Color.white);
		for (Point p : vertices) {
			g.drawOval(p.x - RAD * 2, p.y - RAD * 2, RAD * 2, RAD * 2);
		}
		for (Connection c : connections) {
			c.draw(g);
		}
		g.setColor(Color.red);
		g.draw(goal);
		g.setColor(GOAL);
		g.fill(goal);

		g.setColor(Color.green);
		g.fillOval(start.x - RAD * 2, start.y - RAD * 2, RAD * 2, RAD * 2);
	}
	public int cx = 0, cy = 0;
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON2) {
			tx = cx - e.getX();
			ty = cy - e.getY();
		}
		if (selected.size() == 1) {
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

	public void preview() {
		ArrayList<Line2D.Double> lines = new ArrayList<>();
		for (Connection c : connections) {
			lines.add(c.getLine());
		}
		Level l = new Level(lines, goal);
		Player p = new Player(start.x, start.y, l);

		//TODO render, etc.
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
		cx = e.getX();
		cy = e.getY();
		boolean found = false;
		for (Point p : vertices) {
			if (Math.hypot(p.x - cx, p.y - cy) < RAD) {
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
			if (selected.size() == 1){
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
	public void mouseReleased(MouseEvent e) {}

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
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			selected.clear();
			break;
		case KeyEvent.VK_SPACE:
			preview();
			break;
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {}


	@Override
	public void keyTyped(KeyEvent e) {}
}
