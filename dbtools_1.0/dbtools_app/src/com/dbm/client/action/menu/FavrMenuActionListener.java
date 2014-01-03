package com.dbm.client.action.menu;

import java.awt.event.ActionEvent;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.Lgn01Dialog;
import com.dbm.client.ui.Msg01Dialog;
import com.dbm.client.ui.Session;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.FavrBean;

public class FavrMenuActionListener extends AbstractActionListener {

	private FavrBean favrInfo = null;
	private ConnBean connInfo = null;

	public FavrMenuActionListener(FavrBean favrInfo, ConnBean connInfo) {
		this.favrInfo = favrInfo;
		this.connInfo = connInfo;
	}

	@Override
	protected void doActionPerformed(ActionEvent e) {
		// last db connection is not closed
		DbClient dbClient = DbClientFactory.getDbClient();
		if (dbClient != null && dbClient.isConnected()) {
			Msg01Dialog.showMsgDialog("A db connection is already used");
			return;
		}
		Session.setCurrFavrInfo(favrInfo);
		Session.setCurrConnInfo(connInfo);

		// set database info
		Lgn01Dialog dbDialog = new Lgn01Dialog();
		dbDialog.setVisible(true);
	}

}

