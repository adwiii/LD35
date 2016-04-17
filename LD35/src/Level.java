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
