package it.csttech.metadb.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

abstract class BaseService {

	protected String toCamelCase(String init) {
		if (init == null)
			return null;
		StringBuilder ret = new StringBuilder(init.length());
		for (String word : init.split("_")) {
			if (!word.isEmpty()) {
				ret.append(Character.toUpperCase(word.charAt(0)));
				ret.append(word.substring(1).toLowerCase());
			}
		}
		return Character.toLowerCase(ret.charAt(0)) + ret.substring(1);
	}

	protected String toCamelCaseFirstUp(String init) {
		String ret = toCamelCase(init);
		return Character.toUpperCase(ret.charAt(0)) + ret.substring(1);
	}

	protected String toJavaType(String type) {
		switch (type) {
		case "int":
			return "Integer";
		case "smallint":
			return "Integer";
		case "varchar":
			return "String";
		case "nvarchar":
			return "String";
		case "nchar":
			return "String";
		case "datetime":
			return "Date";
		case "bit":
			return "Boolean";
		case "int identity":
			return "Integer";
		case "float":
			return "Float";
		case "image":
			return "InputStream";
		case "ntext":
			return "String";
		//
		// system tables
		case "sysname":
			return "String";
		case "varbinary":
			return "String";

		default:
			return type;
		}
	}

	protected void write(FileOutputStream fos, String data) throws IOException {
		fos.write(data.getBytes());
		fos.write('\n');
	}

	protected boolean containsDate(List<Map<String, Object>> cols) {
		Iterator<Map<String, Object>> i = cols.iterator();
		while (i.hasNext()) {
			Map<String, Object> col = i.next();
			if (toJavaType((String) col.get("TYPE_NAME")).equals("Date")) {
				return true;
			}
		}
		return false;
	}

}
