import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;

public class Player {

	public static final int CIRCLE = 0,
			SQUARE = 1,
			TRIANGLE = 2;
	static final double G = .1;
	static final double TERMINAL = 5;
	public int type = 1;

	public double angle = 0;
	public int x = 0, y = 0, radius  = 15;
	public double vx = 0, vy = 0, vr = 0;
	public double dx = 0, dy = 0;
	public Color [] colors = new Color[]{
			Color.cyan,
			Color.magenta,
			Color.green
	};
	public Color c = Color.WHITE;

	public Level l;

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

	public Player(int x, int y, Level l) {
		this.x = x;
		this.y = y;
		dx = x;
		dy = y;
		this.l = l;
	}

	public int resolution = 10;

	public int [] xar = new int [resolution],
			yar = new int [resolution];
	public void draw(Graphics2D g) {
		//		g.setColor(c);
		//		g.drawLine(x, y, x, y);

		Polygon p = null;

		if (transition == 0) {
			c = colors[type];
			g.setColor(c);
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
		anglemoment = 0;
		this.to = to;
		transitionType = to + (type << 2);
		transition = TRANSITION;
	}
	boolean onGround;
	boolean onLeft;
	boolean onRight;
	double vxmoment;
	double anglemoment;
	public void physics() {
		switch (type) {
		case CIRCLE:
			dx += vx;
			dy += vy;
			angle += anglemoment;
			boolean hit = false;
			for (Line2D line : l.lines) {
				double da = Math.hypot(line.getX1()-dx, line.getY1()-dy);
				double db = Math.hypot(line.getX2()-dx, line.getY2()-dy);
				double len = Math.hypot(line.getX1()-line.getX2(), line.getY1()-line.getY2());
				double cross = ((line.getX1()-dx)*(line.getY1()-line.getY2()) - (line.getY1()-dy)*(line.getX1()-line.getX2()))/len;
				cross = Math.abs(cross);
				if (da <= radius || db <= radius || (Math.abs(cross) <= radius && da <= len && db <= len)) {
					double angle = Math.atan2(line.getY2()-line.getY1(), line.getX2()-line.getX1());
					System.out.println(Math.toDegrees(angle));
					hit = true;
					anglemoment += Math.sin(angle) * G / radius;
					anglemoment += (LD35.me.left ? -.01 : (LD35.me.right ? .01 : 0));
					if (Math.abs(anglemoment) > TERMINAL / radius) {
						anglemoment = Math.signum(anglemoment) * TERMINAL / radius;
					}
					if (line.getX1()==line.getX2() || Math.abs(angle) == Math.PI / 2) {
						// just fall
						System.out.println("FALL");
						hit = false;
						anglemoment = 0;
						vx = 0;
//						vy = 0;
//						dx -= (radius-cross) * Math.sin(angle);
//						dy -= (radius-cross) * Math.cos(angle);
					} else {
						vx = Math.cos(angle) * anglemoment * radius;
						vy = Math.sin(angle) * anglemoment * radius + 0;
						onGround = true;
						if (Math.abs(angle) > Math.PI/2) angle -= Math.signum(angle) * Math.PI;
						if (vy >= 0) {
							onGround = true;
							dx += (radius-cross) * Math.sin(angle);
							dy -= (radius-cross) * Math.cos(angle);
						} else {
							dx += (radius-cross) * Math.sin(angle);
							dy -= (radius-cross) * Math.cos(angle);
							//							vy = 0;
						}
					}
				}
			}
			if (!hit) {
				System.out.println("NOT HIT");
				vy += G;
			}
			break;
		case SQUARE:
			double rad = radius/Math.sqrt(2);
			if (onGround) {
				System.out.println("on ground");
				onLeft = false;
				onRight = false;
				if (LD35.me.jump) {
					System.out.println("spaaaaaace");
					vy = -5;
					LD35.me.jump = false;
				}
			}
			else vy += G;
			if (onLeft && LD35.me.jump) {
				vy = -5;
				vxmoment = 6;
				onLeft = false;
				LD35.me.jump = false;
			} else if (onRight && LD35.me.jump) {
				vy = -5;
				vxmoment = -6;
				onRight = false;
				LD35.me.jump = false;
			}
			vx = 0;
			if (LD35.me.left) {
				vx -= 2;
				onRight = false;
			}
			if (LD35.me.right) {
				vx += 2;
				onLeft = false;
			}
			if (vxmoment > 0) {
				vxmoment = Math.max(0, vxmoment - (LD35.me.left? .075 : 0.1));
				if (LD35.me.right) vxmoment = 0;
			}
			if (vxmoment < 0) {
				vxmoment = Math.min(0, vxmoment + .075);
				if (LD35.me.left) vxmoment = 0;
			}
			vx += vxmoment;
			vy = Math.max(-TERMINAL, Math.min(TERMINAL, vy));
			if (onLeft || onRight) vy = Math.min(.5, vy);
			dx += vx;
			dy += vy;
			onGround = false;
			onLeft = false;
			onRight = false;
			for (Line2D line : l.lines) {
				double da = Math.hypot(line.getX1()-dx, line.getY1()-dy);
				double db = Math.hypot(line.getX2()-dx, line.getY2()-dy);
				double len = Math.hypot(line.getX1()-line.getX2(), line.getY1()-line.getY2());
				double cross = ((line.getX1()-dx)*(line.getY1()-line.getY2()) - (line.getY1()-dy)*(line.getX1()-line.getX2()))/len;
				cross = Math.abs(cross);
				if (da <= rad || db <= rad || (Math.abs(cross) <= rad && da <= len && db <= len)) {
					double angle = Math.atan2(line.getY2()-line.getY1(), line.getX2()-line.getX1());
					this.angle = angle;
					if (line.getX1()==line.getX2()) {
						if (angle < 0) angle += Math.PI;
						dy += (rad-cross) * Math.cos(angle);
						if (dx > line.getX1()) {
							onLeft = true;
							onRight = false;
							dx += (rad-cross) * Math.sin(angle);
						} else {
							onRight = true;
							onLeft = false;
							dx -= (rad-cross) * Math.sin(angle);
						}
					} else {
						if (Math.abs(angle) > Math.PI/2) angle -= Math.signum(angle) * Math.PI;
						if (vy >= 0) {
							onGround = true;
							vy = 0;
							dx += (rad-cross) * Math.sin(angle);
							dy -= (rad-cross) * Math.cos(angle);
						} else {
							dx += (rad-cross) * Math.sin(angle);
							dy += (rad-cross) * Math.cos(angle);
							vy = 0;
						}
					}
				}
			}
			break;
		case TRIANGLE:

			break;
		}
		x = (int) dx;
		y = (int) dy;
	}

}
