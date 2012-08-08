package util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 経路探索のためのヒープです。
 * キーに対して値を持たせ、値の比較によって、ヒープ（優先度付キュー）を構築します。
 * Comparatorをコンストラクタに与えなければ、要素は最小の値を根にしてヒープを構成します。
 * 
 * キーに対する値を更新する場合には、以前の値よりも根に近い（小さい）と評価される場合のみ
 * 更新されます。
 *
 * @author ma38su
 * @param <E> 
 *
 */
public class Heap<E> {

	/**
	 * 標準の初期容量
	 */
	private static final int DEFAULT_CAPACITY = 10;

	/**
	 * ソートされるオブジェクト
	 */
	private E[] entries;

	/**
	 * ヒープのサイズ
	 */
	private int size;

	/**
	 * キーの管理のためのMap
	 */
	private final Map<E, Integer> table;
	
	/**
	 * 順序付け
	 */
	private final Comparator<E> comparator;

	/**
	 * コンストラクタ
	 *
	 */
	public Heap() {
		this(null);
	}

	/**
	 * コンストラクタ
	 * @param initialCapacity 初期容量
	 */
	public Heap(int initialCapacity) {
		this(initialCapacity, null);
	}
	/**
	 * コンストラクタ
	 * @param comparator
	 */
	public Heap(Comparator<E> comparator) {
		this(Heap.DEFAULT_CAPACITY, comparator);
	}

	/**
	 * コンストラクタ
	 * @param initialCapacity 初期容量
	 * @param comparator
	 */
	@SuppressWarnings("unchecked")
	public Heap(int initialCapacity, Comparator<E> comparator) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException();
		}
		this.size = 0;
		this.entries = (E[]) new Object[initialCapacity + 1];
		this.table = new HashMap<E, Integer>();
		this.comparator = comparator;
	}

	/**
	 * keyが存在していればvalue更新、keyが存在してなければ場合は挿入する
	 * @param key 挿入する key
	 * @param value 挿入する value
	 * @return 更新または挿入がおこなえればtrue
	 */
	@SuppressWarnings("unchecked")
	public boolean add(E key) {
		E entry = key;
		Integer pointer = this.table.get(key);
		if (pointer != null) {
			int index = pointer.intValue();
			if (this.comparator == null) {
				if(((Comparable<E>)this.entries[index]).compareTo(entry) > 0) {
					this.entries[index] = entry;
					this.fixUp(index);
				} else {
					return false;
				}
			} else {
				if (this.comparator.compare(this.entries[index], entry) > 0) {
					this.entries[index] = entry;
					this.fixUp(index);
				} else {
					return false;
				}
			}
		} else {
			this.grow(++this.size);
			this.table.put(key, this.size);
			this.entries[this.size] = entry;
			this.fixUp(this.size);
		}
		return true;
	}

	/**
	 * 入れ替える
	 * @param index1
	 * @param index2
	 */
	private void swap(int index1, int index2) {
		final E tmp = this.entries[index1];
		this.entries[index1] = this.entries[index2];
		this.entries[index2] = tmp;
		this.table.put(this.entries[index1], index1);
		this.table.put(this.entries[index2], index2);
	}

	/**
	 * ヒープの先頭（根）の要素を削除して取り出す
	 * @return ヒープの先頭の要素
	 */
	public E poll() {
		if (this.size == 0) {
			return null;
		}

		final E entry = this.entries[1];
		this.table.remove(entry);
		if (this.size > 1) {
			this.entries[1] = this.entries[this.size];
			this.table.put(this.entries[1], 1);
		}
		this.entries[this.size] = null;
		if (--this.size > 1) {
			this.fixDown(1);
		}
		return entry;
	}

	/**
	 * 削除せずにヒープの先頭（根）の要素を取り出す
	 * @return ヒープの先頭の要素
	 */
	public E peek() {
		return this.entries[1];
	}

	/**
	 * @param key 確認する key
	 * @return keyが含まれていれば true
	 */
	public boolean containsKey(Object key) {
		return this.table.containsKey(key);
	}

	public void clear() {
		this.table.clear();
		for (int i = 0; i <= this.size; i++) {
			this.entries[i] = null;
		}
		this.size = 0;
	}

	/**
	 * 子との状態の比較
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void fixDown(int index) {
		int son;
		if (this.comparator == null) {
			while ((son = index << 1) <= this.size) {
				if (son < this.size && ((Comparable<E>) this.entries[son]).compareTo(this.entries[son+1]) > 0) {
					son++;
				}
				if (((Comparable<E>) this.entries[index]).compareTo(this.entries[son]) <= 0) {
					break;
				}
				this.swap(index, son);
				index = son;
			}
		} else {
			while ((son = index << 1) <= this.size) {
				if (son < this.size && this.comparator.compare(this.entries[son], this.entries[son+1]) > 0) {
					son++;
				}
				if (this.comparator.compare(this.entries[index], this.entries[son]) <= 0) {
					break;
				}
				this.swap(index, son);
				index = son;
			}
		}
	}

	/**
	 * 親との状態を確認
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void fixUp(int index) {
		int parent;
		if (this.comparator == null) {
			while ((parent = index >> 1) > 0) {
				if (((Comparable<E>) this.entries[index]).compareTo(this.entries[parent]) >= 0) {
					break;
				}
				this.swap(index, parent);
				index = parent;
			}
		} else {
			while ((parent = index >> 1) > 0) {
				if (this.comparator.compare(this.entries[index], this.entries[parent]) >= 0) {
					break;
				}
				this.swap(index, parent);
				index = parent;
			}
		}
	}

	/**
	 * ヒープが空でないか確かめる。
	 * @return ヒープに要素がなければtrue
	 */
	public boolean isEmpty() {
		return this.size == 0;
	}

	/**
	 * 配列のサイズを拡張する
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void grow(int index) {
		int newLength = this.entries.length;
		if (index < newLength) {
			return;
		}
		if (index == Integer.MAX_VALUE) {
			throw new OutOfMemoryError();
		}
		while (newLength <= index) {
			if (newLength >= Integer.MAX_VALUE / 2) {
				newLength = Integer.MAX_VALUE;
			} else {
				newLength <<= 2;
			}
		}
		final E[] newEntrys = (E[]) new Object[newLength];
		System.arraycopy(this.entries, 0, newEntrys, 0, this.entries.length);

		this.entries = newEntrys;
	}

	@Override
	public String toString() {
		if (this.size == 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder(this.entries[1].toString());
		for(int i = 2; i <= this.size; i++) {
			sb.append("," + this.entries[i].toString());
		}
		return sb.toString();
	}

	/**
	 * ヒープのサイズを返します。
	 * @return ヒープのサイズ
	 */
	public int size() {
		return this.size;
	}
}
