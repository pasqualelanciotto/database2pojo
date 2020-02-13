package it.csttech.metadb.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EntityService extends BaseService {
	private String tableName;
	private List<Map<String, Object>> cols;
	private String primaryKey;
	private String basePath;

	public EntityService(String tableName, List<Map<String, Object>> cols, String primaryKey, String basePath) {
		this.tableName = tableName;
		this.cols = cols;
		this.primaryKey = primaryKey;
		this.basePath = basePath;
		this.basePath = this.basePath + File.separator + "entities";
	}

	public void create() {
		String className = toCamelCaseFirstUp(tableName);
		FileOutputStream fos = null;
		try {
			File directory = new File(basePath);
			if (!directory.exists()) {
				directory.mkdir();
			}
			fos = new FileOutputStream(new File(basePath + File.separator + className + ".java"));
			createHeaderEntity(fos, tableName);
			Iterator<Map<String, Object>> i = cols.iterator();
			while (i.hasNext()) {
				Map<String, Object> col = i.next();
				createDetailEntity(fos, (String) col.get("COLUMN_NAME"), (String) col.get("TYPE_NAME"),
						(Integer) col.get("COLUMN_SIZE"), (Boolean) col.get("IS_NULLABLE"),
						(Boolean) col.get("IS_AUTOINCREMENT"), primaryKey);
			}
			i = cols.iterator();
			while (i.hasNext()) {
				Map<String, Object> col = i.next();
				createGetterSetterMethod(fos, (String) col.get("COLUMN_NAME"), (String) col.get("TYPE_NAME"),
						(Integer) col.get("COLUMN_SIZE"), (Boolean) col.get("IS_NULLABLE"),
						(Boolean) col.get("IS_AUTOINCREMENT"), primaryKey);
			}
			createFooterEntity(fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fos = null;
		}
	}

	private void createHeaderEntity(FileOutputStream fos, String tableName) throws IOException {
		String s3 = "   ";
		write(fos, "package it.csttech.netaui.data.entities;");
		write(fos, "");
		write(fos, "import java.io.Serializable;");
		write(fos, "import java.util.Date;");
		write(fos, "");
		write(fos, "import javax.persistence.Column;");
		write(fos, "import javax.persistence.Entity;");
		write(fos, "import javax.persistence.Id;");
		write(fos, "import javax.persistence.Table;");
		write(fos, "");
		write(fos, "@Entity");
		write(fos, "@Table(name = \"" + tableName + "\")");
		write(fos, "public class " + toCamelCaseFirstUp(tableName) + " implements Serializable");
		write(fos, "{");
		write(fos, s3 + "private static final long serialVersionUID = " + new Random().nextLong() + "L;");
		write(fos, s3);

	}

	private void createDetailEntity(FileOutputStream fos, String columnName, String columnTypeName, Integer columnSize,
			Boolean isNullable, Boolean isAutoincrement, String primaryKey) throws IOException {
		String s3 = "   ";
		if (columnName.equals(primaryKey)) {
			write(fos, s3 + "@Id");
		}
		write(fos, s3 + "@Column(name = \"" + columnName + "\", nullable = " + isNullable + ", length = " + columnSize
				+ ")");
		write(fos, s3 + "private " + toJavaType(columnTypeName) + " " + toCamelCase(columnName) + ";");
		write(fos, s3);
	}

	private void createGetterSetterMethod(FileOutputStream fos, String columnName, String columnTypeName,
			Integer columnSize, Boolean isNullable, Boolean isAutoincrement, String primaryKey) throws IOException {
		String s3 = "   ";
		String s6 = s3 + s3;

		write(fos, s3 + "public " + toJavaType(columnTypeName) + " get" + toCamelCaseFirstUp(columnName) + "()");
		write(fos, s3 + "{");
		write(fos, s6 + "return " + toCamelCase(columnName) + ";");
		write(fos, s3 + "}");
		write(fos, s3 + "");

		write(fos, s3 + "public void set" + toCamelCaseFirstUp(columnName) + "(" + toJavaType(columnTypeName) + " "
				+ toCamelCase(columnName) + ")");
		write(fos, s3 + "{");
		write(fos, s6 + "this." + toCamelCase(columnName) + " = " + toCamelCase(columnName) + ";");
		write(fos, s3 + "}");
		write(fos, s3);
	}

	private void createFooterEntity(FileOutputStream fos) throws IOException {
		write(fos, "}");
	}
}
