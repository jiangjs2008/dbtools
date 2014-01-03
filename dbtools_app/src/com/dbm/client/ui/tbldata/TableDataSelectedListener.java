package com.dbm.client.ui.tbldata;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class TableDataSelectedListener extends MouseAdapter {

	public void mouseClicked(MouseEvent e) {
		if ((e.getModifiers() == MouseEvent.BUTTON1_MASK || e.getModifiers() == 18) && e.getClickCount() == 1) {
			JTable jTable = (JTable) e.getSource();

			int selCol = jTable.getSelectedColumn();
			int selRow = jTable.getSelectedRow();

			if (selCol == 0) {
				jTable.setRowSelectionAllowed(true);
				jTable.setColumnSelectionAllowed(false);
				jTable.getSelectionModel().addSelectionInterval(selRow, selRow);
			} else {
				jTable.setRowSelectionAllowed(false);
				jTable.setColumnSelectionAllowed(false);
				//jTable.getSelectionModel().clearSelection();
			}
		}
	}


	public void mousePressed(MouseEvent e) {
		if ((e.getModifiers() == MouseEvent.BUTTON1_MASK || e.getModifiers() == 18) && e.getClickCount() == 1) {
			JTable jTable = (JTable) e.getSource();

			int selCol = jTable.getSelectedColumn();
			int selRow = jTable.getSelectedRow();

			if (selCol == 0) {
				jTable.setRowSelectionAllowed(true);
				jTable.setColumnSelectionAllowed(false);
				jTable.getSelectionModel().addSelectionInterval(selRow, selRow);
			} else {
				jTable.setRowSelectionAllowed(false);
				jTable.setColumnSelectionAllowed(false);
				//jTable.getSelectionModel().clearSelection();
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if ((e.getModifiers() == MouseEvent.BUTTON1_MASK || e.getModifiers() == 18) && e.getClickCount() == 1) {
			JTable jTable = (JTable) e.getSource();

			int selCol = jTable.getSelectedColumn();
			int selRow = jTable.getSelectedRow();

			if (selCol == 0) {
				jTable.setRowSelectionAllowed(true);
				jTable.setColumnSelectionAllowed(false);
				jTable.getSelectionModel().addSelectionInterval(selRow, selRow);
			} else {
				jTable.setRowSelectionAllowed(false);
				jTable.setColumnSelectionAllowed(false);
				//jTable.getSelectionModel().clearSelection();
			}
		}
	}
}
