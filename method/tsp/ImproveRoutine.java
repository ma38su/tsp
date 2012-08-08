package method.tsp;

import java.util.List;
import model.Node;


/**
 * 改善法を順序を指定して適用するためのクラス
 * 改善法が適用できなくなれば、改めて最初の改善法から適用していきます。
 * @author ma38su
 */
public class ImproveRoutine implements TspImprovement {
	TspImprovement[] algorithm;
	
	public ImproveRoutine(TspImprovement... algorithm) {
		this.algorithm = algorithm;
	}

	public boolean method(List<Node> route) {
		for (int i = 0; i < this.algorithm.length; i++) {
			if (this.algorithm[i].method(route)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (TspImprovement tsp : this.algorithm) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(" -> ");
			}
			sb.append(tsp);
		}
		return sb.toString();
	}

	public boolean method(int[] route, double[][] table) {
		throw new IllegalAccessError();
	}
}
