/*
 * Created on 2007/03/13
 */
package com.dbm.client.db;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.dbm.common.db.DbClient;

/**
 * SQLCipher for Android
 *
 * @author JiangJusheng
 */
public class DbClient4TcpWrapperImpl extends DbClient {

	private ServerSocket server = null;

	public DbClient4TcpWrapperImpl() {
		Runnable newThread = new Runnable() {
			public void run() {
				try {
					server = new ServerSocket(8090);
					while (true) {
						socket = server.accept();
						Thread receiveThread = new serverThread();
						receiveThread.start();
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		};
		new Thread(newThread).start();
	}

	@Override
	public String getTableDataAt(int rowNum, int colNum) {
		return null;
	}

	@Override
	public boolean start(String[] args) {
		_dbArgs = args;
		return true;
	}

	@Override
	public void close() {
		_isConnected = false;
		try {
			// 出力ストリームを閉じる
			if (dos != null) {
				dos.close();
			}
			// 入力ストリームを閉じる
			if (dis != null) {
				dis.close();
			}
			if (socket != null && !socket.isClosed()) {
				socket.shutdownOutput();
				socket.shutdownInput();
				socket.close();
			}
			socket = null;

			if (server != null) {
				server.close();
			}
		} catch (IOException e1) {
			logger.error(e1);
		}
	}

	// ソケット
	private Socket socket = null;
	// 読込データ
	private DataInputStream dis = null;
	// 送信データ
	private DataOutputStream dos = null;

	private ArrayList<ArrayList<Object>> dataObjs = null;

//	@SuppressWarnings("unchecked")
//	@Override
//	public Object execute(int sqlType, String action) {
//		if (socket == null) {
//			// TODO--
//			return null;
//		}
//
//		if (sqlType == 0) {
//			String[] addr = StringUtil.str2Array(_dbArgs[1], ":");
//			if (addr == null || addr.length == 0 || addr.length == 1) {
//				// input error, have not db info
//			}
//			action = addr[1] + "@"+ _dbArgs[3];
//
//			try {
//				Class.forName(_dbArgs[0]);
//				_dbConn = DriverManager.getConnection(_dbArgs[1]);
//				isConnected = true;
//			} catch (Exception exp) {
//				throw new BaseExceptionWrapper(exp);
//			}
//		}
//		if (sqlType == 9) {
//			action = "select rowid,* from " + action;
//		}
//
//		try {
//			dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//			dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//
//			// スマホ端末へ送信
//			StringBuilder sendStr = new StringBuilder();
//			sendStr.append(sqlType);
//			sendStr.append(StringUtil.addPreZero(action.length(), 6));
//			sendStr.append(action);
//
//			dos.write(sendStr.toString().getBytes());
//			dos.flush();
//
//			// ************************************************************************************
//			// スマホ端末からリクエスト
//			byte[] output = null;
//
//			if (sqlType == 0 || sqlType == 1 || sqlType == 8 || sqlType == 9) {
//				// connect to db / query sql
//				try {
//					ObjectInputStream ois = new ObjectInputStream(dis);
//					Object rslt = ois.readObject();
//					
//					if (sqlType == 1 || sqlType == 9) {
//						dataObjs = (ArrayList<ArrayList<Object>>) rslt;
//						TableUtil.setTableData(dataObjs, false);
//					}
//					return rslt;
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//
//			} else if (sqlType == 2 || sqlType == 3) {
//				// update sql
//
//				output = new byte[1];
//				dis.read(output);
//				return output;
//			}
//
//		} catch (Exception e) {
//			logger.error(e);
//		}
//		return null;
//	}

	@Override
	public int getExecScriptType(String action) {
		int sqlType = -1;

		return sqlType;
	}


	@Override
	public int directExec(String action) {
		return 0;
	}

	public int getCurrPageNum() {
		return 0;
	}

	public int getPageCount() {
		return 0;
	}

//	@Override
//	public boolean executeUpdate(String tblName, HashMap<Integer, HashMap<Integer, String>> params) {
//		if (dataObjs == null) {
//			return false;
//		}
//		StringBuilder sqlStrsAll = new StringBuilder();
//
//		Map<Integer, String> colNameMap = dataObjs.get(dataObjs.size() - 1);
//		String rawId = null;
//		boolean isRowEnd = false;
//		boolean isColEnd = false;
//		for (Iterator<Entry<Integer, HashMap<Integer, String>>> iter = params.entrySet().iterator(); iter.hasNext(); ) {
//			if (isRowEnd) {
//				sqlStrsAll.append("\n");
//			}
//			isRowEnd = true;
//
//			sqlStrsAll.append("update ");
//			sqlStrsAll.append(tblName);
//			sqlStrsAll.append(" set ");
//
//			Entry<Integer, HashMap<Integer, String>> entry = iter.next();
//			rawId = colNameMap.get("0");
//			HashMap<Integer, String> rowMap = entry.getValue();
//
//			isColEnd = false;
//			for (Iterator<Entry<Integer, String>> iter2 = rowMap.entrySet().iterator(); iter2.hasNext(); ) {
//				if (isColEnd) {
//					sqlStrsAll.append(", ");
//				}
//				isColEnd = true;
//
//				Entry<Integer, String> entry2 = iter2.next();
//				sqlStrsAll.append(entry2.getKey());
//				sqlStrsAll.append(" = '");
//				sqlStrsAll.append(entry2.getValue());
//				sqlStrsAll.append("'");
//			}
//
//			sqlStrsAll.append(" where rowid = ");
//			sqlStrsAll.append(rawId);
//		}
//
//		execute(2, sqlStrsAll.toString());
//		return true;
//	}

	class serverThread extends Thread {

		public void run() {
			try {
				dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

				if (_isConnected) {
					// already connected

				} else {
					byte[] headBuf = new byte[7];
					dis.read(headBuf);

					// SQL type
					String telId = "";//StringUtil.byte2CharStr(headBuf);
					if ("0000000".equals(telId)) {
						// connected success
						_isConnected = true;
						// get smart phone's ip address
						String smtIpAddr = socket.getInetAddress().getHostAddress();
						//AppUIAdapter.setGuiDbUrl(smtIpAddr);
					}
				}
			} catch (IOException e) {
				logger.error(e);
			} catch (Exception ex) {
				logger.error(ex);
			}
		}
	}

	@Override
	public void setTableName(String tblName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String procCellData(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet directQuery(String sqlStr, int pageNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet defaultQuery(int pageNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void defaultUpdate(HashMap<Integer, HashMap<Integer, String>> params,
			ArrayList<HashMap<Integer, String>> addParams, ArrayList<Integer> delParams) {
		// TODO Auto-generated method stub
		
	}

}
