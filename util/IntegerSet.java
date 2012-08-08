package util;

/**
 * Integer型のセット
 * @author ma38su
 */
public class IntegerSet {
	private int[] bitset;
	private int[] list;
	private int size;
	/**
	 * 要素の数を返すメソッド
	 * @return 要素の数
	 */
	public int size() {
		return this.size;
	}
	
	private int[] index;
	/**
	 * @param initialCapacity
	 */
	public IntegerSet(int initialCapacity) {
		this.bitset = new int[initialCapacity];
		this.list = new int[initialCapacity];
		this.size = 0;
		this.index = new int[initialCapacity];
		for (int i = 0; i < this.index.length; i++) {
			this.index[i] = i;
		}
	}

	/**
	 * 要素を含むかどうか確認するメソッド
	 * @param value 要素
	 * @return 要素を含んでいればtrueを返す、falseを返す。
	 */
	public boolean contains(int value) {
		return this.bitset[value] > 0;
	}

	/**
	 * 要素を加えるメソッド
	 * @param value 加える要素
	 * @return 要素が含まれていなければtrue、含まれていなければfalseを返す。
	 */
	public boolean add(int value) {
		if (this.bitset[value] == 0) {
			this.list[this.size++] = value;
			this.bitset[value] = this.size;
			return true;
		}
		return false;
	}
	
	public int get(int index) {
		if (index >= this.size) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return this.list[index];
	}
	
	/**
	 * 要素を削除するメソッド
	 * @param value 削除する要素
	 * @return 要素が含まれていればtrue、含まれていなければfalseを返す。
	 */
	public boolean remove(int value) {
		if (this.bitset[value] > 0) {
			int index = this.bitset[value] - 1;
			swap(index, --this.size);
			this.bitset[this.list[index]] = this.bitset[value];
			this.bitset[value] = 0;
			return true;
		}
		return false;
	}

	/**
	 * 要素を交換するメソッド
	 * @param n1 交換する要素のインデックス
	 * @param n2 交換する要素のインデックス
	 */
	private void swap(int n1, int n2) {
		int tmp = this.list[n1];
		this.list[n1] = this.list[n2];
		this.list[n2] = tmp;
		
	}
}
