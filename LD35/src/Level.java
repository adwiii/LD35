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
	public Rectangle border;
	
	public Level(ArrayList<Line2D.Double> lines, Rectangle goal) {
		this.lines = lines;
		this.goal = goal;
		int minx = 0, miny = 0, maxx = 1024, maxy = 768;
		for (Line2D.Double line : lines) {
			minx = (int) Math.min(minx, Math.min(line.getX1(), line.getX2()));
			maxx = (int) Math.max(maxx, Math.max(line.getX1(), line.getX2()));
			miny = (int) Math.min(miny, Math.min(line.getY1(), line.getY2()));
			maxy = (int) Math.max(maxy, Math.max(line.getY1(), line.getY2()));
		}
		border = new Rectangle(minx - 100, miny - 100, maxx + 100, maxy + 100);
	}
	
	public Level() {
		lines.add(new Line2D.Double(100, 100, 100, 500));
		lines.add(new Line2D.Double(100, 500, 300, 700));
		lines.add(new Line2D.Double(300, 700, 700, 700));
		lines.add(new Line2D.Double(450, 700, 600, 550));
		lines.add(new Line2D.Double(700, 700, 900, 600));
		lines.add(new Line2D.Double(900, 100, 900, 600));
		lines.add(new Line2D.Double(800, 100, 800, 600));
		lines.add(new Line2D.Double(200, 100, 200, 500));
//		lines.add(new Line2D.Double(100, 400, 900, 400));
		goal = new Rectangle(0,0,0,0);
		border = new Rectangle(-100,-100,1024+200,768+200);
	}
	
	public void draw(Graphics2D g) {
		g.setColor(levelCol);
		for (Line2D.Double line : lines) {
			g.draw(line);
		}
		
		g.setColor(goalCol);
		g.draw(goal);
	}
	
}
