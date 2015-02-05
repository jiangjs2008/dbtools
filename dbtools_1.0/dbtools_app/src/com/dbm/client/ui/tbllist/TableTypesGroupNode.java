package com.dbm.client.ui.tbllist;

import java.awt.Component;
import java.util.List;

import com.dbm.client.action.CursorChanger;
import com.dbm.client.ui.AppUIAdapter;

public class TableTypesGroupNode extends BaseNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private interface i18n {
		String NO_CATALOG = "No Catalog"; // i18n or Replace with
	}

	private String _catalogName;
	private String _schemaName;
	private String _catalogIdentifier;
	private String _schemaIdentifier;

	public TableTypesGroupNode(ObjectsTreeModel treeModel, String catalogName, String catalogIdentifier, String schemaName,
			String schemaIdentifier) {
		super(treeModel, generateName(catalogName, schemaName));
		_catalogIdentifier = catalogIdentifier;
		_schemaIdentifier = schemaIdentifier;
		_catalogName = catalogName;
		_schemaName = schemaName;
	}

	/**
	 * 节点展开，显示所属对象一览，如表、视图一览
	 */
	@Override
	public void expand(List<String[]> tables) {
		if (tables == null || tables.size() == 0) {
			return;
		}
		if (getChildCount() == 0) {
			CursorChanger cc = new CursorChanger((Component) AppUIAdapter.getUIObj(AppUIAdapter.AppMainGUI));
			cc.show();
			try {
				ObjectsTreeModel model = getTreeModel();
				for (String[] objName : tables) {
					BaseNode node = new BaseNode(model, objName[0]);
					model.insertNodeInto(node, this, this.getChildCount());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				cc.restore();
			}
		}
	}

	String getCatalogName() {
		return _catalogName;
	}

	public String getCatalogIdentifier() {
		return _catalogIdentifier;
	}

	String getSchemaName() {
		return _schemaName;
	}

	String getSchemaIdentifier() {
		return _schemaIdentifier;
	}

	private static String generateName(String catalogName, String schemaName) {
		StringBuffer buf = new StringBuffer();
		if (catalogName != null) {
			buf.append(catalogName);
			if (schemaName != null) {
				buf.append(".");
			}
		}
		if (schemaName != null) {
			buf.append(schemaName);
		}
		if (buf.length() == 0) {
			buf.append(i18n.NO_CATALOG);
		}
		return buf.toString();
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
