package it.csttech.metadb.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DaoService extends BaseService {
	private String tableName;
	private List<Map<String, Object>> cols;
	private String primaryKey;
	private String basePath;

	public DaoService(String tableName, List<Map<String, Object>> cols, String primaryKey, String basePath) {
		this.tableName = tableName;
		this.cols = cols;
		this.primaryKey = primaryKey;
		this.basePath = basePath;
		this.basePath = this.basePath + File.separator + "daos";
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
			fos = new FileOutputStream(new File(basePath + File.separator + className + "Dao.java"));
			createHeaderDaoInterface(fos, className);
			createDetailDaoInterface(fos);
			createFooterDaoInterface(fos);
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
			String implementationPath = basePath + File.separator + "hibernate";
			File directory = new File(implementationPath);
			if (!directory.exists()) {
				directory.mkdir();
			}
			fos = new FileOutputStream(new File(implementationPath + File.separator + className + "DaoImpl.java"));
			createHeaderDaoImplementation(fos, className);
			createDetailDaoImplementation(fos);
			createFooterDaoImplementation(fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fos = null;
		}
	}

	private void createHeaderDaoInterface(FileOutputStream fos, String className) throws IOException {
		write(fos, "package it.csttech.netaui.data.daos;");
		write(fos, "");
		write(fos, "import org.hibernate.SessionFactory;");
		write(fos, "import it.phoenix.core.data.dao.BaseDao;");
		write(fos, "import it.csttech.netaui.data.entities." + className + ";");
		write(fos, "");
		write(fos, "public interface " + className + "Dao extends BaseDao<" + className + ">");
		write(fos, "{");
	}

	private void createDetailDaoInterface(FileOutputStream fos) throws IOException {
		String s3 = "   ";
		write(fos, s3 + "public SessionFactory getDaoSessionFactory();");
	}

	private void createFooterDaoInterface(FileOutputStream fos) throws IOException {
		write(fos, "}");
	}

	private void createHeaderDaoImplementation(FileOutputStream fos, String className) throws IOException {
		write(fos, "package it.csttech.netaui.data.daos.hibernate;");
		write(fos, "");
		write(fos, "import org.hibernate.SessionFactory;");
		write(fos, "import it.phoenix.core.data.dao.hibernate.BaseDaoImpl;");
		write(fos, "import it.csttech.netaui.data.daos." + className + "Dao;");
		write(fos, "import it.csttech.netaui.data.entities." + className + ";");
		write(fos, "");
		write(fos, "public class " + className + "DaoImpl extends BaseDaoImpl<" + className + "> implements "
				+ className + "Dao");
		write(fos, "{");
	}

	private void createDetailDaoImplementation(FileOutputStream fos) throws IOException {
		String s3 = "   ";
		String s6 = s3 + s3;
		write(fos, s3 + "@Override");
		write(fos, s3 + "public SessionFactory getDaoSessionFactory()");
		write(fos, s3 + "{");
		write(fos, s6 + "return super.getDaoSessionFactory();");
		write(fos, s3 + "}");

	}

	private void createFooterDaoImplementation(FileOutputStream fos) throws IOException {
		write(fos, "}");
	}

}
