package model;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.StringTokenizer;

public class Node {
	private static final int SIZE = 10;
	private int x;
	
	private int y;

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Node(String data) {
		StringTokenizer st = new StringTokenizer(data, ",");
		this.x = Integer.parseInt(st.nextToken());
		this.y = Integer.parseInt(st.nextToken());
	}

	/**
	 * 頂点を描画するメソッド
	 * @param g
	 * @param color 色
	 */
	public void draw(Graphics2D g, Color color) {
		g.setColor(color);
		int centerX = this.x - SIZE / 2;
		int centerY = this.y - SIZE / 2;
		g.fillOval(centerX, centerY, SIZE, SIZE);
		g.setColor(Color.BLACK);
		g.drawOval(centerX, centerY, SIZE, SIZE);
	}

	/**
	 * 頂点を描画するメソッド
	 * @param g 
	 * @param index 頂点番号
	 */
	public void draw(Graphics2D g, int index) {
		g.drawString(Integer.toString(index), this.x + SIZE / 2, this.y - SIZE / 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			return this.x == node.x && this.y == node.y;
		}
		return false;
	}
	
	/**
	 * 頂点間の直線距離を計算します。
	 * @param node 距離を計算する頂点
	 * @return 頂点間の直線距離
	 */
	public double getDistance(Node node) {
		int dx = this.x - node.x;
		int dy = this.y - node.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public int getX() {
		return this.x;
	}
	
	
	public int getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return this.x + "," + this.y;
	}
	@Override
	public int hashCode() {
		return this.x + this.y;
	}
}
