package com.dbm.client.ui.tbllist;

/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.dbm.client.util.LoggerWrapper;

public class ObjectsTreeModel extends DefaultTreeModel {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * instances of the log class
	 */
	private static LoggerWrapper logger = new LoggerWrapper(ObjectsTreeModel.class); 

	/**
	 * 缺省构造函数
	 */
	public ObjectsTreeModel() {
		super(new DefaultMutableTreeNode("Database"));
	}

	/**
	 * 加载DB对象，如：表、视图等等
	 */
	public void loadList(List<String> mdInfoList) {
		try {
			final DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();

			for (String meta : mdInfoList) {
				root.add(new TableTypesGroupNode(this, meta, meta, null, null));
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * 清空一览
	 */
	public void clearList() {
		final DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
		root.removeAllChildren();
		reload();
	}
}
