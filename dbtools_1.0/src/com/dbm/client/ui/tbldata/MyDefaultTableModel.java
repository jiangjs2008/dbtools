package com.dbm.client.ui.tbldata;

import javax.swing.table.DefaultTableModel;

public class MyDefaultTableModel extends DefaultTableModel {


	private static final long serialVersionUID = -3621110831803263176L;

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			return false;
		}
		return true;
	}

//	@Override
//	public boolean equals(Object anObject) {
//
//
//		return true;
//	}
//
//	@Override
//	public String toString() {
//		return getClass().getName() + "@" + Integer.toHexString(hashCode());
//	}
}