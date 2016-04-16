import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class Player {
	
	public static final int CIRCLE = 0,
					  		SQUARE = 1,
					  		TRIANGLE = 2;
	
	public int type = 0;
	
	public double angle = 0;
	public int x = 0, y = 0, radius  = 50;
	public Color c = Color.WHITE;
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int [] xar = new int [4], yar = new int [4];
	public void draw(Graphics2D g) {
		g.setColor(c);
		g.drawLine(x, y, x, y);
		Polygon p = null;
		switch (type) {
		case CIRCLE:
			g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
			//draw half line showing where we are in angle
			g.drawLine((int) Math.round(x + Math.cos(angle) * radius * .33), (int) Math.round(y + Math.sin(angle) * radius * .33), 
					   (int) Math.round(x + Math.cos(angle) * radius), (int) Math.round(y + Math.sin(angle) * radius));
			break;
		case SQUARE:
			//start at top left corner
			for (int i = 0; i < 4; i++) {
				xar[i] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * i / 2) * radius);
				yar[i] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * i / 2) * radius);
			}
			p = new Polygon(xar, yar, 4);
			break;
		case TRIANGLE:
			for (int i = 0; i < 3; i++) {
				xar[i] = (int) Math.round(x + Math.cos(angle + Math.PI / 2 + 2 * Math.PI * i / 3) * radius);
				yar[i] = (int) Math.round(y + Math.sin(angle + Math.PI / 2 + 2 * Math.PI * i / 3) * radius);
			}
			p = new Polygon(xar, yar, 3);
			break;
		}
		if (p != null) {
			g.draw(p);
		}
	}

}
