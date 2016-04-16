import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class Player {

	public static final int CIRCLE = 0,
			SQUARE = 1,
			TRIANGLE = 2;

	public int type = 0;

	public double angle = 0;
	public int x = 0, y = 0, radius  = 15;
	public Color [] colors = new Color[]{
			Color.cyan,
			Color.magenta,
			Color.green
	};
	public Color c = Color.WHITE;

	public static final int TRANSITION = 10; //default ticks
	public int transition = 0; //ticks of the transition animation
	public int transitionType; // to + from << 2
	public int to;

	public static final int CIRCLE_SQUARE = 0 + (1 << 2),
			SQUARE_CIRCLE = 1 + (0 << 2),
			CIRCLE_TRIANGLE = 0 + (2 << 2),
			TRIANGLE_CIRCLE = 2 + (0 << 2),
			SQUARE_TRIANGLE = 1 + (2 << 2),
			TRIANGLE_SQUARE = 2 + (1 << 2);

	public Player(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int resolution = 10;
	
	public int [] xar = new int [resolution],
			yar = new int [resolution];
	public void draw(Graphics2D g) {
//		g.setColor(c);
		//		g.drawLine(x, y, x, y);

		Polygon p = null;

		if (transition == 0) {
			g.setColor(colors[type]);
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
		} else {
			int ticks = TRANSITION - transition--; // 0 to transition (also decrement)
			c = new Color((int) Math.round(colors[type].getRed() * transition / TRANSITION + colors[to].getRed() * ticks / TRANSITION),
						  (int) Math.round(colors[type].getGreen() * transition / TRANSITION + colors[to].getGreen() * ticks / TRANSITION),
						  (int) Math.round(colors[type].getBlue() * transition / TRANSITION + colors[to].getBlue() * ticks / TRANSITION));
			int sides = 0;
			switch (transitionType) {
			case SQUARE_CIRCLE: ticks = TRANSITION - ticks;
			case CIRCLE_SQUARE:
				sides = (int) Math.round(((double) ticks / TRANSITION) * (xar.length - 4) + 4);
				for (int i = 0; i < sides; i++) {
					xar[i] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + i * 2 * Math.PI / sides) * radius);
					yar[i] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + i * 2 * Math.PI / sides) * radius);
				}
				break;
			case TRIANGLE_CIRCLE: ticks = TRANSITION - ticks;
			case CIRCLE_TRIANGLE:
				sides = (int) Math.round(((double) ticks / TRANSITION) * (xar.length - 3) + 3);
				for (int i = 0; i < sides; i++) {
					xar[i] = (int) Math.round(x + Math.cos(angle + Math.PI / 2 + i * 2 * Math.PI / sides) * radius);
					yar[i] = (int) Math.round(y + Math.sin(angle + Math.PI / 2 + i * 2 * Math.PI / sides) * radius);
				}
				break;
			case TRIANGLE_SQUARE: ticks = TRANSITION - ticks;
			case SQUARE_TRIANGLE:
				sides = 4;
				xar[0] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * 0 / 2) * radius * ticks / TRANSITION + 
						Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 0 / 3) * radius * (TRANSITION - ticks) / TRANSITION);
				yar[0] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 0 / 2) * radius * ticks / TRANSITION + 
						Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 0 / 3) * radius * (TRANSITION - ticks) / TRANSITION);
				
				xar[1] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * 1 / 2) * radius * ticks / TRANSITION + 
						Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 1 / 6) * radius * Math.tan(Math.PI / 6) * .5 * (TRANSITION - ticks) / TRANSITION);
				yar[1] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 1 / 2) * radius * ticks / TRANSITION + 
						Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 1 / 6) * radius * Math.tan(Math.PI / 6) * .5 * (TRANSITION - ticks) / TRANSITION);
				
				xar[2] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * 2 / 2) * radius * ticks / TRANSITION + 
						Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 1 / 3) * radius * (TRANSITION - ticks) / TRANSITION);
				yar[2] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 2 / 2) * radius * ticks / TRANSITION + 
						Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 1 / 3) * radius * (TRANSITION - ticks) / TRANSITION);
				
				xar[3] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * 3 / 2) * radius * ticks / TRANSITION + 
						 Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 2 / 3) * radius * (TRANSITION - ticks) / TRANSITION);
				yar[3] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 3 / 2) * radius * ticks / TRANSITION + 
						 Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 2 / 3) * radius * (TRANSITION - ticks) / TRANSITION);
				break;
			}
			p = new Polygon(xar, yar, sides);
			if (transition == 0) {
				type = to;
			}
		}
		if (p != null) {
			g.setColor(c);
			g.draw(p);
		}
	}
	public void transition(int to) {
		if (to == type || transition > 0) return;
		this.to = to;
		transitionType = to + (type << 2);
		transition = TRANSITION;
	}

}
