package it.csttech.metadb.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ManagerService extends BaseService {
	private String tableName;
	private List<Map<String, Object>> cols;
	private String primaryKey;
	private String basePath;

	public ManagerService(String tableName, List<Map<String, Object>> cols, String primaryKey, String basePath) {
		this.tableName = tableName;
		this.cols = cols;
		this.primaryKey = primaryKey;
		this.basePath = basePath;
		this.basePath = this.basePath + File.separator + "managers";
	}

	public void create() {
		createInterface();
		createImplementation();
	}

	private void createInterface() {
		String className = toCamelCaseFirstUp(tableName);
		FileOutputStream fos = null;
		try {
			File directory = new File(basePath);
			if (!directory.exists()) {
				directory.mkdir();
			}
			fos = new FileOutputStream(new File(basePath + File.separator + className + "Manager.java"));
			createHeaderManagerInterface(fos, className);
			createDetailManagerInterface(fos, className);
			createFooterManagerInterface(fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fos = null;
		}
	}

	private void createImplementation() {
		String className = toCamelCaseFirstUp(tableName);
		FileOutputStream fos = null;
		try {
			String implementationPath = basePath + File.separator + "spring";
			File directory = new File(implementationPath);
			if (!directory.exists()) {
				directory.mkdir();
			}
			fos = new FileOutputStream(new File(implementationPath + File.separator + className + "ManagerImpl.java"));
			createHeaderManagerImplementation(fos, className);
			createDetailManagerImplementation(fos, className);
			createFooterManagerImplementation(fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fos = null;
		}
	}

	private void createHeaderManagerInterface(FileOutputStream fos, String className) throws IOException {
		write(fos, "package it.csttech.netaui.data.managers;");
		write(fos, "");
		if (containsDate(cols)) {
			write(fos, "import java.util.Date;");
			write(fos, "");
		}
		write(fos, "import it.csttech.netaui.data.entities." + className + ";");
		write(fos, "import it.phoenix.core.data.managers.BaseManager;");
		write(fos, "");
		write(fos, "public interface " + className + "Manager extends BaseManager<" + className + ">");
		write(fos, "{");
	}

	private void createDetailManagerInterface(FileOutputStream fos, String className) throws IOException {
		String s3 = "   ";

		StringBuffer sb = new StringBuffer();
		Iterator<Map<String, Object>> i = cols.iterator();
		while (i.hasNext()) {
			Map<String, Object> col = i.next();
			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (primaryKey != null && !primaryKey.trim().equalsIgnoreCase((String) col.get("COLUMN_NAME"))) {
				sb.append(toJavaType((String) col.get("TYPE_NAME")));
				sb.append(" ");
				sb.append(toCamelCase((String) col.get("COLUMN_NAME")));
			}
		}

		write(fos, s3 + "public void create(" + sb.toString() + ");");
		write(fos, s3 + "public " + className + " read(Integer id);");
		write(fos, s3 + "public void update();");
		write(fos, s3 + "public void delete(Integer id);");
	}

	private void createFooterManagerInterface(FileOutputStream fos) throws IOException {
		write(fos, "}");
	}

	private void createHeaderManagerImplementation(FileOutputStream fos, String className) throws IOException {
		String s3 = "   ";
		write(fos, "package it.csttech.netaui.data.managers.spring;");
		write(fos, "");
		if (containsDate(cols)) {
			write(fos, "import java.util.Date;");
			write(fos, "");
		}
		write(fos, "import org.springframework.beans.factory.annotation.Autowired;");
		write(fos, "import org.springframework.transaction.annotation.Transactional;");
		write(fos, "");
		write(fos, "import it.phoenix.core.data.managers.spring.BaseManagerImpl;");
		write(fos, "import it.csttech.netaui.data.entities." + className + ";");
		write(fos, "import it.csttech.netaui.data.daos." + className + "Dao;");
		write(fos, "import it.csttech.netaui.data.managers." + className + "Manager;");
		write(fos, "");
		write(fos, "public class " + className + "ManagerImpl extends BaseManagerImpl<" + className + "> implements "
				+ className + "Manager");
		write(fos, "{");
		write(fos, s3 + "@Autowired");
		write(fos, s3 + "private " + className + "Dao " + toCamelCase(className) + "Dao;");
		write(fos, "");
	}

	private void createDetailManagerImplementation(FileOutputStream fos, String className) throws IOException {
		String s3 = "   ";
		String s6 = s3 + s3;

		StringBuffer sb = new StringBuffer();
		Iterator<Map<String, Object>> i = cols.iterator();
		while (i.hasNext()) {
			Map<String, Object> col = i.next();
			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (primaryKey != null && !primaryKey.trim().equalsIgnoreCase((String) col.get("COLUMN_NAME"))) {
				sb.append(toJavaType((String) col.get("TYPE_NAME")));
				sb.append(" ");
				sb.append(toCamelCase((String) col.get("COLUMN_NAME")));
			}
		}

		write(fos, s3 + "@Transactional");
		write(fos, s3 + "public void create(" + sb.toString() + ")");
		write(fos, s3 + "{");
		write(fos, s6 + className + " " + toCamelCase(className) + "Entity = new " + className + "();");

		Iterator<Map<String, Object>> i2 = cols.iterator();
		while (i2.hasNext()) {
			Map<String, Object> col = i2.next();
			if (primaryKey != null && !primaryKey.trim().equalsIgnoreCase((String) col.get("COLUMN_NAME"))) {
				write(fos, s6 + toCamelCase(className) + "Entity.set" + toCamelCaseFirstUp((String) col.get("COLUMN_NAME"))
						+ "(" + toCamelCase((String) col.get("COLUMN_NAME")) + ");");
			}
		}

		write(fos, s6 + toCamelCase(className) + "Dao.insert(" + toCamelCase(className) + "Entity);");
		write(fos, s3 + "}");
		write(fos, s3);
		write(fos, s3 + "@Transactional");
		write(fos, s3 + "public " + className + " read(0)");
		write(fos, s3 + "{");
		write(fos, s6 + "return " + toCamelCase(className) + "Dao.findById(0);");
		write(fos, s3 + "}");
		write(fos, s3);
		write(fos, s3 + "@Transactional");
		write(fos, s3 + "public void update()");
		write(fos, s3 + "{");
		write(fos, s6 + className + " " + toCamelCase(className) + " = new " + className + "();");
		write(fos, s6 + toCamelCase(className) + "Dao.update(" + toCamelCase(className) + ");");
		write(fos, s3 + "}");
		write(fos, s3);
		write(fos, s3 + "@Transactional");
		write(fos, s3 + "public void delete(Integer id)");
		write(fos, s3 + "{");
		write(fos, s6 + toCamelCase(className) + "Dao.deleteById(0);");
		write(fos, s3 + "}");
	}

	private void createFooterManagerImplementation(FileOutputStream fos) throws IOException {
		write(fos, "}");
	}

}
