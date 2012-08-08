
package method.tsp;

import gui.DemoPanel;
import method.GraphDemonstration;
import model.Node;
import util.Heap;

/**
 * Held and Karpの手法による巡回セールスマン問題の厳密解法
 * ラグランジュ緩和、ラグランジュ乗数は適当
 * @author ma38su
 */
public class HeldKarp implements GraphDemonstration {
	private final int limit;
	public HeldKarp(int limit) {
		this.limit = limit;
	}
	/**
	 * 一時的に辺を表現するためのクラス
	 * @author ma38su
	 */
	class Edge {
		int s;
		int t;
		double cost;
		public Edge(int s, int t, double cost) {
			this.s = s;
			this.t = t;
			this.cost = cost;
		}

		@Override
		public boolean equals(Object obj) {
			return this.hashCode() == obj.hashCode();
		}

		public int compareTo(Edge e) {
			double diff = this.cost - e.cost;
			if (diff < 0) {
				return -1;
			} else if (diff > 0){
				return 1;
			}
	        long thisBits = Double.doubleToLongBits(this.cost);
	        long anotherBits = Double.doubleToLongBits(e.cost);
	        return (thisBits == anotherBits ? 0 : 
	                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
	                 1));                          // (0.0, -0.0) or (NaN, !NaN)
		}
		@Override
		public int hashCode() {
			return this.t;
		}
	}

	/**
	 * 1つの頂点に2つの辺がつながっているかどうか確認するメソッド
	 * 1-treeがこれを満たせば、巡回路といえる。
	 * @param dcost 操作量
	 * @param edges 辺の接続
	 * @param adjustment ラグランジュ緩和による辺の補正
	 * @return 巡回路であればtrue、巡回路でなければfalseを返す。
	 */
	private boolean checkCircuit(double dcost, boolean[][] edges, double[] adjustment) {
		boolean ret = true;
		for (int i = 0; i < edges.length; i++) {
			int connection = 0;
			for (int j = 0; j < edges[i].length; j++) {
				if (edges[i][j]) {
					connection++;
				}
			}
			if (connection < 2) {
				adjustment[i] -= dcost;
				ret = false;
			} else if (connection > 2) {
				adjustment[i] += dcost;
				ret = false;
			}
		}
		return ret;
	}

	/**
	 * 巡回路のコストの下界を求めるメソッド
	 * @param table 距離テーブル
	 * @param multiplier ラグランジュ乗数
	 * @param edges 辺の接続関係
	 * @return 巡回路のコストの下界
	 */
	private double getLowerCost(double[][] table, double[] multiplier, boolean[][] edges) {
		double cost = 0;
		for (int i = 0; i < table.length; i++) {
			for (int j = i + 1; j < table[i].length; j++) {
				if (edges[i][j]) {
					cost += table[i][j] + multiplier[i] + multiplier[j];
				}
			}
		}
		double sigma = 0;
		for (double m : multiplier) {
			sigma += m;
		}
		return cost - 2 * sigma;
	}

	/**
	 * 1-treeを求める。
	 * @param panel パネル
	 * @param edges 辺配列
	 * @param nodes 頂点配列
	 * @param table 距離テーブル
	 * @param multipliers ラグランジュ緩和による距離の補正
	 */
	private void getOneTree(DemoPanel panel, final boolean[][] edges, final double[][] table, final double[] multipliers) {
		for (int i = 0; i < edges.length; i++) {
			for (int j = 0; j < edges.length; j++) {
				edges[i][j] = false;
			}
		}
		if (edges.length > 1) {
			Edge e;
			boolean[] close = new boolean[edges.length];
			Heap<Edge> open = new Heap<Edge>();
			int s = (int) (Math.random() * edges.length);
			close[s] = true;
			int index = (s + 1) % edges.length;
			close[index] = true;
			do {
				for (int i = 0; i < edges.length; i++) {
					if (i != index && !close[i]) {
						open.add(new Edge(index, i, table[index][i] + multipliers[index] + multipliers[i]));
					}
				}
				e = open.poll();
				if (e == null) {
					break;
				}
				edges[e.s][e.t] = true;
				edges[e.t][e.s] = true;
				index = e.t;
				close[index] = true;
				panel.set(edges);
			} while (open.size() > 0);
			open.clear();
			for (int i = 0; i < edges.length; i++) {
				if (s != i) {
					open.add(new Edge(s, i, table[index][i] + multipliers[index] + multipliers[i]));
				}
			}
			e = open.poll();
			edges[e.s][e.t] = true;
			edges[e.t][e.s] = true;
			e = open.poll();
			edges[e.s][e.t] = true;
			edges[e.t][e.s] = true;
		}
	}

	/**
	 * 距離テーブルを作成します。
	 * @param nodes 頂点配列
	 * @return 距離テーブル
	 */
	public double[][] createTable(Node[] nodes) {
		final double[][] table = new double[nodes.length][nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				table[i][j] = nodes[i].getDistance(nodes[j]);
			}
		}
		return table;
	}

	public void method(DemoPanel panel) {
		final Node[] nodes = panel.getNodes().toArray(new Node[]{});
		double[][] table = createTable(nodes);
		boolean[][] edges = new boolean[nodes.length][nodes.length];
		double[] multipliers = new double[nodes.length];
		BetterCase betterCase = new BetterCase(nodes.length);
		double lowerBound = 0;
		int count = 0;
		double multiplier = 100;
		do {
			this.getOneTree(panel, edges, table, multipliers);
			panel.set(edges);
			double cost = this.getLowerCost(table, multipliers, edges);
			if (lowerBound < cost) {
				lowerBound = cost;
				betterCase.set(edges, multipliers, lowerBound);
				panel.setCost(lowerBound);
			}
			multiplier *= 0.9;
		} while (count++ < this.limit && !this.checkCircuit(multiplier, edges, multipliers));
		panel.set(betterCase.getEdges());
	}

	@Override
	public String toString() {
		return "Held and Karp";
	}
	class BetterCase {
		boolean[][] edges;
		double[] multipliers;
		double lowerBound;
		public BetterCase(int nodes) {
			this.edges = new boolean[nodes][nodes];
			this.multipliers = new double[nodes];
		}
		public void set(boolean[][] edges, double[] multipliers, double lowerBound) {
			for (int i = 0; i < edges.length; i++) {
				for (int j = 0; j < edges[i].length; j++) {
					this.edges[i][j] = edges[i][j];
				}
			}
			for (int i = 0; i < multipliers.length; i++) {
				this.multipliers[i] = multipliers[i];
			}
			this.lowerBound = lowerBound;
		}
		public boolean[][] getEdges() {
			return this.edges;
		}
		public double[] getMultipliers() {
			return this.multipliers;
		}
		public double getLowerBound() {
			return this.lowerBound;
		}
	}
}