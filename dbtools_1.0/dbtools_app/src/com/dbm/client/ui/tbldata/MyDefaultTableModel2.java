package com.dbm.client.ui.tbldata;

import javax.swing.table.DefaultTableModel;

public class MyDefaultTableModel2 extends DefaultTableModel {

	private static final long serialVersionUID = -3621110831803263176L;

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}