package com.android.developer.contacts;

public class SortToken {
	/** 简拼 */
	public StringBuilder simpleSpell = new StringBuilder();
	/** 全拼 */
	public StringBuilder wholeSpell = new StringBuilder();
	/** 中文全名 */
	public StringBuilder chName = new StringBuilder();
	@Override
	public String toString() {
		return "[simpleSpell=" + simpleSpell + ", wholeSpell=" + wholeSpell + ", chName=" + chName + "]";
	}
}
