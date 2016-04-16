import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Level {

	public Color levelCol = new Color(0xAAAAAA);
	
	public Color goalCol = Color.red;
	
	public ArrayList<Line2D.Double> lines = new ArrayList<Line2D.Double>();
	public Rectangle goal;
	public Rectangle walls;
	
	public Level(ArrayList<Line2D.Double> lines, Rectangle goal, Rectangle walls) {
		this.lines = lines;
		this.goal = goal;
		this.walls = walls;
	}
	
	public Level() {
		lines.add(new Line2D.Double(100, 100, 100, 500));
		lines.add(new Line2D.Double(100, 500, 300, 700));
		lines.add(new Line2D.Double(300, 700, 700, 700));
		lines.add(new Line2D.Double(700, 700, 900, 600));
		lines.add(new Line2D.Double(900, 100, 900, 600));
		goal = new Rectangle(0,0,0,0);
		walls = new Rectangle(0,0,1024,768);
	}
	
	public void draw(Graphics2D g) {
		g.setColor(levelCol);
		for (Line2D.Double line : lines) {
			g.draw(line);
		}
		g.draw(walls);
		
		g.setColor(goalCol);
		g.draw(goal);
	}
	
}
