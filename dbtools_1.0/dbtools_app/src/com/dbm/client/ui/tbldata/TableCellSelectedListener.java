package com.dbm.client.ui.tbldata;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class TableCellSelectedListener extends MouseAdapter {

	public void mouseClicked(MouseEvent e) {
		if ((e.getModifiers() == MouseEvent.BUTTON1_MASK || e.getModifiers() == 18) && e.getClickCount() == 1) {
			// 鼠标左键
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
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			// 鼠标右键
			JTable jTable = (JTable) e.getSource();
			
            //获取鼠标右键选中的行  
            int row = jTable.rowAtPoint(e.getPoint());  
            if (row == -1) {  
                return ;  
            }  
            //获取已选中的行  
            int[] rows = jTable.getSelectedRows();  
            boolean inSelected = false ;  
            //判断当前右键所在行是否已选中  
            for(int r : rows){  
                if(row == r){  
                    inSelected = true ;  
                    break ;  
                }  
            }  
            //当前鼠标右键点击所在行不被选中则高亮显示选中行  
            if(!inSelected){  
            	jTable.setRowSelectionInterval(row, row);  
            }  

			int selCol = jTable.getSelectedColumn();
			if (selCol == 0) {
	            // 生成右键菜单
	            JPopupMenu popMenu = new JPopupMenu();  
				JMenuItem jMenuItem2 = new JMenuItem("Table Info");
				popMenu.add(jMenuItem2);
	
				JMenuItem jMenuItem3 = new JMenuItem("Index Info");
				popMenu.add(jMenuItem3);
	
	            popMenu.show(e.getComponent(), e.getX(), e.getY());  
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
