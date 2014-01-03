package com.dbm.client.action;

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
import java.awt.Component;
import java.awt.Cursor;

public class CursorChanger {

	private Component _comp;
	private final static Cursor _newCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	private Cursor _origCursor;

	public CursorChanger(Component comp) {
		_comp = comp;
	}

	public void show() {
		if (_comp == null) {
			return;
		}
		if (_origCursor == null) {
			_origCursor = _comp.getCursor();
		}
		_comp.setCursor(_newCursor);
	}

	public void restore() {
		if (_comp != null && _origCursor != null) {
			_comp.setCursor(_origCursor);
		}
		_comp = null;
		_origCursor = null;
	}

}
