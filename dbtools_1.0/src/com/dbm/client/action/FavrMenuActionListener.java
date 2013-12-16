package com.dbm.client.action;

import java.awt.event.ActionEvent;

import com.dbm.client.db.DbClient;
import com.dbm.client.property.ConnBean;
import com.dbm.client.property.FavrBean;
import com.dbm.client.ui.Lgn01Dialog;
import com.dbm.client.ui.Msg01Dialog;
import com.dbm.client.ui.Session;

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
		if (DbClient.isConnected) {
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

