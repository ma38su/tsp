package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.JComponent;
import model.Edge;
import model.Node;

/**
 * グラフのデモを表示するパネルです。
 * @author ma38su 
 */
public class DemoPanel extends JComponent {

	/**
	 * 辺の接続制約
	 */
	private boolean[][] connect;

	/**
	 * 辺の非接続制約
	 */
	private boolean[][] disconnect;
	
	public void switchNodeIndexView() {
		this.isNodeIndexViwe = !this.isNodeIndexViwe;
	}
	/**
	 * 辺の接続状況
	 */
	private boolean[][] edges;

	/**
	 * アニメーションの間隔
	 */
	private int interval = 0;

	/**
	 * 辺の接続制約の表示フラグ
	 */
	private boolean isConnectView = false;

	/**
	 * 辺の非接続制約の表示フラグ
	 */
	private boolean isDisconnectView = false;
	
	/**
	 * 辺の表示フラグ
	 */
	private boolean isEdgeView = true;

	/**
	 * 頂点番号の表示フラグ
	 */
	private boolean isNodeIndexViwe = true;
	
	/**
	 * フレームとパネルのマージン
	 */
	private final int MARGIN = 5;

	/**
	 * 頂点のリスト
	 */
	private List<Node> nodes;

	/**
	 * 頂点数と巡回路のコストの変更を通知するオブジェクト
	 */
	private Observable observable;

	/**
	 * オフスクリーンイメージ
	 */
	private Image offs;

	/**
	 * 巡回路
	 */
	private List<Node> route;

	private Rectangle screen;

	/**
	 * コンストラクタ
	 * @param observable 巡回路コストと、頂点数の変更を通知するオブジェクト
	 */
	public DemoPanel(Observable observable) {
		this.observable = observable;
		this.nodes = new ArrayList<Node>();
		this.route = new ArrayList<Node>();
		this.edges = null;
	}
	
	/**
	 * 頂点を追加するメソッド
	 * @param x X座標
	 * @param y Y座標
	 */
	public void add(int x, int y) {
		if (this.screen.contains(x, y)) {
			this.nodes.add(new Node(x, y));
			this.observable.notifyObservers(this.nodes.size());
			this.repaint();
		}
	}

	/**
	 * パネルを初期化するメソッド
	 */
	public void clear() {
		this.route.clear();
		this.disconnect = null;
		this.connect = null;
		this.edges = null;
		this.observable.notifyObservers(this.route);
		this.nodes.clear();
		this.observable.notifyObservers(this.nodes.size());
		this.repaint();
	}

	/**
	 * 頂点を返すメソッド
	 * @return 頂点のリスト
	 */
	public List<Node> getNodes() {
		return this.nodes;
	}
	
	public String getFormulation() {
		StringBuilder sb = new StringBuilder();
		if (!this.nodes.isEmpty()) {
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = i + 1; j < nodes.size(); j++) {
					sb.append("var x");
					sb.append(i);
					sb.append('x');
					sb.append(j);
					sb.append(" >= 0, <= 1, integer;\n");
				}
			}
			sb.append('\n');
			
			double[][] table = new double[nodes.size()][nodes.size()];

			for (int i = 0; i < nodes.size(); i++) {
				Node n1 = nodes.get(i);
				for (int j = i + 1; j < nodes.size(); j++) {
					Node n2 = nodes.get(j);
					double dist = n1.getDistance(n2);
					table[i][j] = dist;
					table[j][i] = dist;
				}
			}
			
			sb.append("minimize route: ");
			boolean flag = false;
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = i + 1; j < nodes.size(); j++) {
					if (flag) {
						sb.append(" + ");
					} else {
						flag = true;
					}
					sb.append(table[i][j]);
					sb.append("*x");
					sb.append(i);
					sb.append("x");
					sb.append(j);
				}
			}
			sb.append(";\n");
			sb.append("\n");
			
			for (int i = 0; i < nodes.size() - 1; i++) {
				flag = false;
				for (int j = 0; j < nodes.size(); j++) {
					if (i != j) {
						if (flag) {
							sb.append(" + ");
						} else {
							sb.append("s.t. x");
							sb.append(i);
							sb.append(": ");
							flag = true;
						}
						sb.append('x');
						if (i < j) {
							sb.append(i);
							sb.append('x');
							sb.append(j);
						} else if (i > j) {
							sb.append(j);
							sb.append('x');
							sb.append(i);
						}
					}
				}
				sb.append(" = 2;\n");
			}
		}
		return sb.toString();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (this.offs == null || this.getWidth() != this.offs.getWidth(null) || this.getHeight() != this.offs.getHeight(null)) {
			this.offs = this.createImage(this.getWidth(), this.getHeight());
			this.screen = new Rectangle(this.MARGIN, this.MARGIN, this.getWidth() - this.MARGIN * 2, this.getHeight() - this.MARGIN * 2);
		}
		Graphics2D g2 = (Graphics2D) this.offs.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fill(this.screen);
		g2.setClip(this.screen);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.BLACK);
		synchronized (this.route) {
			if (!this.route.isEmpty()) {
				Node n0 = this.route.get(this.route.size() - 1);
				for (Node node : this.route) {
					Edge entry = new Edge(n0, node);
					entry.draw(g2);
					n0 = node;
				}
			}
		}

		try {
			Stroke stroke = g2.getStroke();
			if (this.isDisconnectView) {
				if (this.disconnect != null) {
					g2.setColor(Color.RED);
					g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{5, 5}, 0));
					for (int i = 0; i < this.disconnect.length; i++) {
						// this.nodes.size()だと頂点が増えたときに例外が起こる可能性がある。
						for (int j = i + 1; j < this.disconnect[i].length; j++) {
							if (this.disconnect[i][j]) {
								Edge edge = new Edge(this.nodes.get(i), this.nodes.get(j));
								edge.draw(g2);
							}
						}
					}
					g2.setStroke(stroke);
				}
			}
			if (this.edges != null && this.isEdgeView) {
				g2.setColor(Color.DARK_GRAY);
				for (int i = 0; i < this.edges.length; i++) {
					// this.nodes.size()だと頂点が増えたときに例外が起こる可能性がある。
					for (int j = i + 1; j < this.edges[i].length; j++) {
						if (this.edges[i][j]) {
							Edge edge = new Edge(this.nodes.get(i), this.nodes.get(j));
							edge.draw(g2);
						}
					}
				}
			}
			
			if (this.isConnectView) {
				if (this.connect != null) {
					g2.setColor(Color.BLUE);
					g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{5, 5}, 0));
					for (int i = 0; i < this.connect.length; i++) {
						// this.nodes.size()だと頂点が増えたときに例外が起こる可能性がある。
						for (int j = i + 1; j < this.connect[i].length; j++) {
							if (this.connect[i][j]) {
								Edge edge = new Edge(this.nodes.get(i), this.nodes.get(j));
								edge.draw(g2);
							}
						}
					}
					g2.setStroke(stroke);
				}
			}

			for (int i = 0; i < this.nodes.size(); i++) {
				int connection = 0;
				Node node = this.nodes.get(i);
				if (this.edges != null) {
					for (int j = 0; j < this.edges.length; j++) {
						if (i < this.edges.length && (this.edges[i][j] || this.edges[j][i])) {
							connection++;
						}
					}
				}
				if (connection == 0) {
					node.draw(g2, Color.GRAY);
				} else if (connection % 2 == 0) {
					node.draw(g2, Color.YELLOW);
				} else {
					node.draw(g2, Color.RED);
				}
			}
			
			if (this.isNodeIndexViwe) {
				for (int i = 0; i < this.nodes.size(); i++) {
					this.nodes.get(i).draw(g2, i + 1);
				}
			}
			g2.setClip(0, 0, this.getWidth(), this.getHeight());
			g2.setColor(Color.BLACK);
			g2.draw(this.screen);
			g.drawImage(this.offs, 0, 0, null);
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			this.repaint();
		}
	}

	/**
	 * パネルに表示する辺を設定するメソッド
	 * @param edges 辺配列
	 */
	public void set(boolean[][] edges) {
		synchronized (this.route) {
			this.route.clear();
		}
		this.observable.notifyObservers(this.route);
		this.edges = edges;
		this.repaint();
		synchronized (this) {
			if (this.interval > 0) {
				try {
					this.wait(this.interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * パネルに表示する接続、非接続を設定するためのメソッド
	 * @param connect 接続制約
	 * @param disconnect 非接続制約
	 */
	public void set(boolean[][] connect, boolean[][] disconnect) {
		this.connect = connect;
		this.disconnect = disconnect;
	}

	/**
	 * 巡回賂を設定します。
	 * @param route 巡回賂を示す頂点のリスト
	 */
	public void set(List<Node> route) {
		this.edges = null;
		synchronized (this.route) {
			this.route.clear();
			synchronized (route) {
				this.route.addAll(route);
			}
		}
		this.repaint();
		synchronized (this) {
			if (this.interval > 0) {
				try {
					this.wait(this.interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 巡回路のコストを設定するメソッド
	 * @param cost 巡回路のコスト
	 */
	public void setCost(Double cost) {
		this.observable.notifyObservers(cost);
	}

	/**
	 * アニメーションの間隔を設定します。
	 * @param ms アニメーションの間隔（ms）
	 */
	public void setInterval(int ms) {
		synchronized (this) {
			this.interval = ms;
		}
	}

	/**
	 * パネルに表示する頂点を設定するメソッド
	 * @param nodes
	 */
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
		this.observable.notifyObservers(this.nodes.size());
		this.repaint();
	}

	/**
	 * 接続制約の表示を切替えるメソッド
	 */
	public void switchConnectViwe() {
		this.isConnectView = !this.isConnectView;
	}

	/**
	 * 非接続制約の表示を切替えるメソッド
	 */
	public void switchDisconnectView() {
		this.isDisconnectView = !this.isDisconnectView;
	}
	
	/**
	 * 辺の表示を切替えるメソッド
	 */
	public void switchEdgeView() {
		this.isEdgeView = !this.isEdgeView;
	}
}
