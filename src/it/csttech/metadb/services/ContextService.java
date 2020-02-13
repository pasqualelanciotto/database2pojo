package it.csttech.metadb.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContextService extends BaseService {

	private List<String> tablesName;
	private String basePath;
	private final String s3 = "   ";
	private final String s6 = s3 + s3;

	public ContextService(String basePath) {
		this.basePath = basePath;
		tablesName = new ArrayList<String>();
	}

	public void addTableName(String tableName) {
		tablesName.add(tableName);
	}

	public void create() {
		FileOutputStream fos = null;
		try {
			File directory = new File(basePath);
			if (!directory.exists()) {
				directory.mkdir();
			}
			fos = new FileOutputStream(new File(basePath + File.separator + "netaui-data-context.xml"));
			createContext(fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fos = null;
		}
	}

	private void createContext(FileOutputStream fos) throws IOException {
		List<String> header = getheader();
		for (String head : header) {
			write(fos, head);
		}

		final char quotes = '"';
		for (String tableName : tablesName) {
			StringBuilder sb = new StringBuilder("<bean id=");
			sb.append(quotes);
			sb.append(toCamelCase(tableName));
			sb.append(quotes);
			sb.append(" class=");
			sb.append(quotes);
			sb.append("it.csttech.netaui.data.daos.hibernate.");
			sb.append(toCamelCaseFirstUp(tableName));
			sb.append("DaoImpl");
			sb.append(quotes);
			sb.append(">");
			write(fos, s3 + sb.toString());

			sb = new StringBuilder("<property name=");
			sb.append(quotes);
			sb.append("daoSessionFactory");
			sb.append(quotes);
			sb.append(" ref=");
			sb.append(quotes);
			sb.append("phoenixSessionFactory");
			sb.append(quotes);
			sb.append(" />");

			write(fos, s6 + sb.toString());
			write(fos, s3 + "</bean>");
			write(fos, "");
		}

		write(fos, s3 + "<!-- MANAGERS -->");
		for (String tableName : tablesName) {
			StringBuilder sb = new StringBuilder("<bean id=");
			sb.append(quotes);
			sb.append(toCamelCase(tableName) + "Manager");
			sb.append(quotes);
			sb.append(" class=");
			sb.append(quotes);
			sb.append("it.csttech.netaui.data.managers.spring.");
			sb.append(toCamelCaseFirstUp(tableName) + "ManagerImpl");
			sb.append(quotes);
			sb.append(" />");
			write(fos, s3 + sb.toString());
		}

		write(fos, "</beans>");
	}

	private List<String> getheader() {
		List<String> header = new ArrayList<String>();
		header.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		header.add("<beans xmlns=\"http://www.springframework.org/schema/beans\"");
		header.add(s6 + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		header.add(s6 + "xmlns:aop=\"http://www.springframework.org/schema/aop\"");
		header.add(s6 + "xmlns:context=\"http://www.springframework.org/schema/context\"");
		header.add(s6 + "xmlns:tx=\"http://www.springframework.org/schema/tx\"");
		header.add(s6 + "xmlns:task=\"http://www.springframework.org/schema/task\"");
		header.add(s6 + "xsi:schemaLocation=\"http://www.springframework.org/schema/beans");
		header.add(s6 + "http://www.springframework.org/schema/beans/spring-beans-4.3.xsd");
		header.add(s6 + "http://www.springframework.org/schema/context");
		header.add(s6 + "http://www.springframework.org/schema/context/spring-context-4.3.xsd");
		header.add(s6 + "http://www.springframework.org/schema/util");
		header.add(s6 + "http://www.springframework.org/schema/util/spring-util-4.3.xsd");
		header.add(s6 + "http://www.springframework.org/schema/tx");
		header.add(s6 + "http://www.springframework.org/schema/tx/spring-tx-4.3.xsd");
		header.add(s6 + "http://www.springframework.org/schema/task");
		header.add(s6 + "http://www.springframework.org/schema/task/spring-task-4.3.xsd");
		header.add(s6 + "http://www.springframework.org/schema/aop");
		header.add(s6 + "http://www.springframework.org/schema/aop/spring-aop-4.3.xsd");
		header.add(">");
		header.add("");

		header.add(s3 + "<!-- DAOS -->");
		header.add(s3 + "<bean class=\"org.springframework.jdbc.core.JdbcTemplate\">");
		header.add(s6 + "<constructor-arg ref=\"phoenixDataSource\" />");
		header.add(s3 + "</bean>");
		header.add("");
		return header;
	}

}
