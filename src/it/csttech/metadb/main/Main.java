package it.csttech.metadb.main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.csttech.metadb.services.ContextService;
import it.csttech.metadb.services.DaoService;
import it.csttech.metadb.services.EntityService;
import it.csttech.metadb.services.ManagerService;

// TODO properties in file di configurazione
// TODO parametrizzazione package
public class Main {
	private final String USER = "user";
	private final String PASSWORD = "password";
	private String driver = "net.sourceforge.jtds.jdbc.Driver";
	private String url = "jdbc:jtds:sqlserver://localhost:1433/NetAUI_EAB";
	private String userName = "SA";
	private String password = "aE!_6Z7bUUrC!2zVe.88WT4bmydrdfgZ";
	private String basePath = "/tmp";

	public void execute() {
		Connection con = null;
		Properties connectionProps = new Properties();
		connectionProps.put(USER, this.userName);
		connectionProps.put(PASSWORD, this.password);
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, connectionProps);
			doCreate(con);
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doCreate(Connection connection) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] { "TABLE" });
		ContextService contextService = new ContextService(basePath);
		while (resultSet.next()) {
			String tableName = resultSet.getString("TABLE_NAME");
			if (tableName.equalsIgnoreCase("dtproperties") || tableName.equalsIgnoreCase("trace_xe_action_map")
					|| tableName.equalsIgnoreCase("trace_xe_event_map")) {
				continue;
			}

			System.out.println("processing [" + tableName + "] ...");
			contextService.addTableName(tableName);
			ResultSet columns = databaseMetaData.getColumns(connection.getCatalog(), null, tableName, "%");
			List<Map<String, Object>> cols = new ArrayList<Map<String, Object>>();
			while (columns.next()) {
				Map<String, Object> col = new HashMap<String, Object>();
				col.put("COLUMN_NAME", new String(columns.getString("COLUMN_NAME")));
				col.put("TYPE_NAME", new String(columns.getString("TYPE_NAME")));
				col.put("COLUMN_SIZE", new Integer(columns.getInt("COLUMN_SIZE")));
				col.put("IS_NULLABLE", new Boolean(columns.getBoolean("IS_NULLABLE")));
				col.put("IS_AUTOINCREMENT", new Boolean(columns.getBoolean("IS_AUTOINCREMENT")));
				cols.add(col);
			}
			String primaryKey = null;
			ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(connection.getCatalog(), null, tableName);
			while (primaryKeys.next()) {
				primaryKey = primaryKeys.getString("COLUMN_NAME");
				break;
			}

			EntityService entityService = new EntityService(tableName, cols, primaryKey, basePath);
			entityService.create();

			DaoService daoService = new DaoService(tableName, cols, primaryKey, basePath);
			daoService.create();

			ManagerService managerService = new ManagerService(tableName, cols, primaryKey, basePath);
			managerService.create();
		}
		contextService.create();
		System.out.println("*** FINE ***");
	}

	public static void main(String args[]) {
		Main main = new Main();
		main.execute();
	}
}
