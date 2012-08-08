package model;

import java.awt.Graphics2D;

/**
 * 無向グラフにおけるルート
 * @author Masayasu Fujiwara
 */
public class Edge {
	private Node start;
	private Node end;

	public Edge(Node start, Node end) {
		this.start = start;
		this.end = end;
	}
	
	public Node getStart() {
		return this.start;
	}
	
	public Node getEnd() {
		return this.end;
	}
	
	/**
	 * 頂点間に直線を描画します。
	 * @param g Graphics2D
	 * @param num 数字
	 */
	public void draw(Graphics2D g) {
		g.drawLine(this.start.getX(), this.start.getY(), this.end.getX(), this.end.getY());
	}

	public void draw(Graphics2D g, int num) {
		g.drawLine(this.start.getX(), this.start.getY(), this.end.getX(), this.end.getY());
		g.drawString(Integer.toString(num), (this.start.getX() + this.end.getX()) / 2, (this.start.getY() + this.end.getY()) / 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge entry = (Edge) obj;
			return (this.start.equals(entry.start) && this.end.equals(entry.end)) || (this.start.equals(entry.end) && this.end.equals(entry.start));
//			return this.start.equals(entry.start) && this.terminal.equals(entry.terminal);
		}
		return false;
	}

	/**
	 * 2頂点間の距離を計算します。
	 * 値は保持せず、呼び出す度に再計算します。
	 * @return 2頂点間のユークリッド距離
	 */
	public double getDistance() {
		int dx = this.start.getX() - this.end.getX();
		int dy = this.start.getY() - this.end.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public int hashCode() {
		return this.start.hashCode() + this.end.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Edge[");
		sb.append(this.start);
		sb.append("][");
		sb.append(this.end);
		sb.append("]");
		return sb.toString();
	}
}
