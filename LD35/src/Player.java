import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

public class Player {

	public static final int CIRCLE = 0,
			SQUARE = 1,
			TRIANGLE = 2;
	static final double G = .1;
	static final double TERMINAL = 5;
	public int type = 1;
	public int prevType;

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
	
	public boolean win;
	public boolean dead;

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

	public Player(Level level) {
		this((level.start != null) ? level.start.x : 400, (level.start != null) ? level.start.y : 600, level);
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
				double angle = -this.angle;
				for (int i = 0; i < 3; i++) {
					xar[i] = (int) Math.round(x + Math.cos(angle + Math.PI / 2 + 2 * Math.PI * i / 3 + Math.PI / 6) * radius);
					yar[i] = (int) Math.round(y + Math.sin(angle + Math.PI / 2 + 2 * Math.PI * i / 3 + Math.PI / 6) * radius);
				}
				p = new Polygon(xar, yar, 3);
				g.drawLine((int) Math.round(x + Math.cos(angle) * radius * -.4), (int) Math.round(y + Math.sin(angle) * radius * -.4), 
						(int) Math.round(x + Math.cos(angle) * radius), (int) Math.round(y + Math.sin(angle) * radius));
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
						Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 0 / 3 + Math.PI / 6) * radius * (TRANSITION - ticks) / TRANSITION);
				yar[0] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 0 / 2) * radius * ticks / TRANSITION + 
						Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 0 / 3 + Math.PI / 6) * radius * (TRANSITION - ticks) / TRANSITION);

				xar[1] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * 1 / 2) * radius * ticks / TRANSITION + 
						Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 1 / 6 + Math.PI / 6) * radius * Math.tan(Math.PI / 6) * .5 * (TRANSITION - ticks) / TRANSITION);
				yar[1] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 1 / 2) * radius * ticks / TRANSITION + 
						Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 1 / 6 + Math.PI / 6) * radius * Math.tan(Math.PI / 6) * .5 * (TRANSITION - ticks) / TRANSITION);

				xar[2] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * 2 / 2) * radius * ticks / TRANSITION + 
						Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 1 / 3 + Math.PI / 6) * radius * (TRANSITION - ticks) / TRANSITION);
				yar[2] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 2 / 2) * radius * ticks / TRANSITION + 
						Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 1 / 3 + Math.PI / 6) * radius * (TRANSITION - ticks) / TRANSITION);

				xar[3] = (int) Math.round(x + Math.cos(angle + 3 * Math.PI / 4 + Math.PI * 3 / 2) * radius * ticks / TRANSITION + 
						Math.cos(angle + Math.PI / 2 + 2 * Math.PI * 2 / 3  + Math.PI / 6) * radius * (TRANSITION - ticks) / TRANSITION);
				yar[3] = (int) Math.round(y + Math.sin(angle + 3 * Math.PI / 4 + Math.PI * 3 / 2) * radius * ticks / TRANSITION + 
						Math.sin(angle + Math.PI / 2 + 2 * Math.PI * 2 / 3  + Math.PI / 6) * radius * (TRANSITION - ticks) / TRANSITION);
				break;
			}
			p = new Polygon(xar, yar, sides);
			if (transition == 0) {
				type = to;
				if (type == TRIANGLE) {
					if (vx > 0) this.angle = 0;
					else this.angle = Math.PI;
				}
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
	double move;
	static final double DM = .1;
	double anglemoment;
	public void physics() {
		if (LD35.me.left == LD35.me.right) {
			if (move > 0) move = Math.max(0, move - DM);
			else move = Math.min(0, move + DM);
		} else if (LD35.me.right) move = Math.min(1, move + DM);
		else move = Math.max(-1, move - DM);
		
		switch (type) {
		case CIRCLE:
			prevType = CIRCLE;
			dx += vx;
			dy += vy;
			angle += anglemoment;
			anglemoment += (LD35.me.left ? -.005 : (LD35.me.right ? .005 : 0));
			boolean hit = false;
			for (Line2D line : l.lines) {
				double da = Math.hypot(line.getX1()-dx, line.getY1()-dy);
				double db = Math.hypot(line.getX2()-dx, line.getY2()-dy);
				double len = Math.hypot(line.getX1()-line.getX2(), line.getY1()-line.getY2());
				double cross = ((line.getX1()-dx)*(line.getY1()-line.getY2()) - (line.getY1()-dy)*(line.getX1()-line.getX2()))/len;
				cross = Math.abs(cross);
				if (da <= radius || db <= radius || (Math.abs(cross) <= radius && da <= len && db <= len)) {
					double angle = Math.atan2(line.getY2()-line.getY1(), line.getX2()-line.getX1());
					if (Math.abs(angle) > Math.PI/2) angle -= Math.signum(angle) * Math.PI;
					System.out.println(Math.toDegrees(angle));
					hit = true;
					double thy = line.getY1() + (dx-line.getX1()) * (line.getY2()-line.getY1()) / (line.getX2()-line.getX1());
					boolean above = dy < thy;
					if (above) anglemoment += Math.sin(angle) * G / radius;
//					else anglemoment -= Math.sin(angle) * G / radius;
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
						vx = (above?1:-1)*Math.cos(angle) * anglemoment * radius;
						vy = (above?1:-1)*Math.sin(angle) * anglemoment * radius + 0;
						System.out.println(above);
						onGround = true;
						if (above) {
							onGround = true;
							dx += (radius-cross) * Math.sin(angle);
							dy -= (radius-cross) * Math.cos(angle);
						} else {
							dx -= (radius-cross) * Math.sin(angle);
							dy += (radius-cross) * Math.cos(angle);
						}
					}
					if (!above) hit = false;
				}
			}
			if (!hit) {
				System.out.println("NOT HIT");
				vy += G;
			}
			break;
		case SQUARE:
			prevType = SQUARE;
			double rad = radius/Math.sqrt(2);
			if (onGround) {
				onLeft = false;
				onRight = false;
				if (LD35.me.jump) {
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
			if (LD35.me.left) onRight = false;
			if (LD35.me.right) onLeft = false;
			if (vxmoment > 0) {
				vxmoment = Math.max(0, vxmoment - (LD35.me.left? .075 : 0.1));
//				if (LD35.me.right) {
//					move = 1;
//					vxmoment = 0;
//				}
			}
			if (vxmoment < 0) {
				vxmoment = Math.min(0, vxmoment + .075);
//				if (LD35.me.left) {
//					move = -1;
//					vxmoment = 0;
//				}
			}
			vx += 2*move;
			vx += vxmoment;
			vx = Math.max(-2, Math.min(2, vx));
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
					if (Math.abs(line.getX1()-line.getX2()) < .5*Math.abs(line.getY1()-line.getY2())) {
						if (angle < 0) angle += Math.PI;
						dy += (rad-cross) * Math.cos(angle);
						double thx = line.getX1() + (dy-line.getY1()) * (line.getX2()-line.getX1()) / (line.getY2()-line.getY1());
						if (dx > thx) {
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
						double thy = line.getY1() + (dx-line.getX1()) * (line.getY2()-line.getY1()) / (line.getX2()-line.getX1());
						if (dy < thy) {
							onGround = true;
							vy = 0;
							dx += (rad-cross) * Math.sin(angle);
							dy -= (rad-cross) * Math.cos(angle);
						} else {
							dx -= (rad-cross) * Math.sin(angle);
							dy += (rad-cross) * Math.cos(angle);
							vy = 0;
						}
					}
				}
			}
			break;
		case TRIANGLE:
			vy += G/10;
			if (LD35.me.left) angle += .01;
			if (LD35.me.right) angle -= .01;
			double dir = vx * Math.cos(angle) - vy * Math.sin(angle);
			double perp = vx * Math.sin(angle) + vy * Math.cos(angle);
			if (perp > TERMINAL/10) perp = Math.max(TERMINAL/10, perp-G);
			if (perp < -TERMINAL/10) perp = Math.min(TERMINAL/10, perp+G);
			dir = Math.max(-TERMINAL, Math.min(TERMINAL, dir));
			vx = dir * Math.cos(-angle) - perp * Math.sin(-angle);
			vy = dir * Math.sin(-angle) + perp * Math.cos(-angle);
			if (dir < 0) angle += Math.PI;
			dx += vx;
			dy += vy;
			rad = radius/Math.sqrt(2);
			for (Line2D line : l.lines) {
				double da = Math.hypot(line.getX1()-dx, line.getY1()-dy);
				double db = Math.hypot(line.getX2()-dx, line.getY2()-dy);
				double len = Math.hypot(line.getX1()-line.getX2(), line.getY1()-line.getY2());
				double cross = ((line.getX1()-dx)*(line.getY1()-line.getY2()) - (line.getY1()-dy)*(line.getX1()-line.getX2()))/len;
				cross = Math.abs(cross);
				if (da <= rad || db <= rad || (Math.abs(cross) <= rad && da <= len && db <= len)) {
					transition(prevType);
					vx = 0;
					vy = 0;
				}
			}
			break;
		}
		x = (int) dx;
		y = (int) dy;
		Rectangle me = new Rectangle(x-radius, y-radius, 2*radius, 2*radius);
		if (l.goal.contains(me)) win = true;
		if (!l.border.intersects(me)) dead = true;
	}

}
