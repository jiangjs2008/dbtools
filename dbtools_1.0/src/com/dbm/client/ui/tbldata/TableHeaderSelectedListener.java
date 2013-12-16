package com.dbm.client.ui.tbldata;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableHeaderSelectedListener extends MouseAdapter {


	public void mouseClicked(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK && e.getClickCount() == 1) {
//			JTableHeader tblHeader = (JTableHeader) e.getSource();
//			//if (!e.isShiftDown()) {
//				jTable1.clearSelection();
//		//	}
//			int pick = tblHeader.columnAtPoint(e.getPoint());
//			boolean colSel = jTable1.getColumnSelectionAllowed();
//			boolean rowSel = jTable1.getRowSelectionAllowed();
////			jTable1.setColumnSelectionAllowed(true);
////			jTable1.setRowSelectionAllowed(false);
////			jTable1.addColumnSelectionInterval(pick, pick);
//			jTable1.getSelectionModel().addSelectionInterval(0, jTable1.getColumnCount());
////			jTable1.setColumnSelectionAllowed(colSel);
////			jTable1.setRowSelectionAllowed(rowSel);
		}
	}
}
