package com.dbm.client.action.menu;

import java.awt.event.ActionEvent;

import org.apache.commons.lang3.math.NumberUtils;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.help.Msg01Dialog;
import com.dbm.client.ui.menu.Lgn01Dialog;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;

public class FavrMenuActionListener extends AbstractActionListener {

	@Override
	protected void doActionPerformed(ActionEvent e) {
		// last db connection is not closed
		DbClient dbClient = DbClientFactory.getDbClient();
		if (dbClient != null && dbClient.isConnected()) {
			Msg01Dialog.showMsgDialog(10004);
			return;
		}

		String menuId = ((javax.swing.JMenuItem) e.getSource()).getName();
		if (menuId == null) {
			logger.info("menu id is null");
			return;
		}
		
		int id = 0;
		if (menuId.startsWith("favr:")) {
			menuId = menuId.substring(5);
			id = NumberUtils.toInt(menuId, -1);
			if (id >= 0) {
				FavrBean favrInfo = PropUtil.getFavrInfo(id);
				Session.setCurrFavrInfo(favrInfo);
			}

		} else if (menuId.startsWith("conn:")) {
			menuId = menuId.substring(5);
			id = NumberUtils.toInt(menuId, -1);
			if (id >= 0) {
				ConnBean connInfo = PropUtil.getDbConnInfo(id);
				Session.setCurrConnInfo(connInfo);
			}

		} else {
			logger.info("menu id is invalid");
			return;
		}

		// set database info
		Lgn01Dialog dbDialog = new Lgn01Dialog();
		dbDialog.setVisible(true);
	}

}

