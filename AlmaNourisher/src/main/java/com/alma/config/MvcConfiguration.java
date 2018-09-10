package com.alma.config;


import com.alma.dao.BranchDAO;
import com.alma.dao.BranchDAOImpl;
import com.alma.dao.DocumentsDAO;
import com.alma.dao.DocumentsDAOImpl;
import com.alma.dao.ManageSchoolDAO;
import com.alma.dao.ManageSchoolDAOImpl;
import com.alma.dao.ManageUserDAO;
import com.alma.dao.ManageUserDAOImpl;
import com.alma.dao.ReportDAO;
import com.alma.dao.ReportDAOImpl;
import com.alma.dao.StudentDAO;
import com.alma.dao.StudentDAOImpl;
import com.alma.dao.StudentDetailDAO;
import com.alma.dao.StudentDetailDAOImpl;
import com.alma.dao.StudentHealthHistoryDAO;
import com.alma.dao.StudentHealthHistoryDAOImpl;
import com.alma.dao.UserRoleAssociationDAO;
import com.alma.dao.UserRoleAssociationDAOImpl;
import com.alma.dao.UsersDAO;
import com.alma.dao.UsersDAOImpl;
import com.alma.dao.WellnessLookupAssociationDAO;
import com.alma.dao.WellnessLookupAssociationImpl;
import com.alma.filter.SessionExpiredInterceptor;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@PropertySources({ @org.springframework.context.annotation.PropertySource({ "classpath:dbconfig.properties" }) })
@ComponentScan(basePackages = { "com.alma" })
@EnableWebMvc
public class MvcConfiguration {
	static Logger log = Logger.getLogger(MvcConfiguration.class);
	private CommonsMultipartResolver multipartResolver;

	@Autowired
	private Environment env;
	
	@PostConstruct
	public void init() {
		System.setProperty("db.connection.url", this.env.getProperty("db.connection.url"));

		System.setProperty("db.connection.username", this.env.getProperty("db.connection.username"));

		System.setProperty("db.connection.password", this.env.getProperty("db.connection.password"));
	}
	
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(new String[] { "/resources/**" })
				.addResourceLocations(new String[] { "/resources/" }).setCachePeriod(Integer.valueOf(31556926));
	}

	@Bean
	public SessionExpiredInterceptor SessionExpiredInterceptor() {
		return new SessionExpiredInterceptor();
	}

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(SessionExpiredInterceptor());
	}
	
	@Bean
	public DataSource getDataSource() {
		log.debug("db connection url >>>>>>>>>>>> " + this.env.getProperty("db.connection.url"));
		log.debug("db connection userName >>>>>>>>>>>> " + this.env.getProperty("db.connection.username"));
		log.debug("db connection userpassword >>>>>>>>>>>> " + this.env.getProperty("db.connection.password"));

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(this.env.getProperty("db.connection.url"));
		dataSource.setUsername(this.env.getProperty("db.connection.username"));
		dataSource.setPassword(this.env.getProperty("db.connection.password"));

		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("useUnicode", "true");
		connectionProperties.setProperty("characterEncoding", "UTF-8");
		dataSource.setConnectionProperties(connectionProperties);

		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
		dataSourceTransactionManager.setDataSource(dataSource);

		return dataSourceTransactionManager;
	}
	
	@Bean
	public UsersDAO getUsersDAO() {
		return new UsersDAOImpl(getDataSource());
	}

	@Bean
	public ManageUserDAO getManageUserDAO() {
		return new ManageUserDAOImpl(getDataSource());
	}
	
	@Bean
	public UserRoleAssociationDAO getUserRoleAssociationDAO() {
		return new UserRoleAssociationDAOImpl(getDataSource());
	}
	
	@Bean
	public ManageSchoolDAO getSchoolDetailsDAO() {
		return new ManageSchoolDAOImpl(getDataSource());
	}
	
	@Bean
	public BranchDAO getBranchDAO() {
		return new BranchDAOImpl(getDataSource());
	}
	
	@Bean
	public StudentDetailDAO getStudentDetailDAO() {
		return new StudentDetailDAOImpl(getDataSource());
	}

	@Bean
	public WellnessLookupAssociationDAO getWellnessLookupAssociationDAO() {
		return new WellnessLookupAssociationImpl(getDataSource());
	}
	
	@Bean
	public StudentDAO getStudentDAO() {
		return new StudentDAOImpl(getDataSource());
	}
	
	@Bean
	public StudentHealthHistoryDAO getStudentHealthHistoryDAO() {
		return new StudentHealthHistoryDAOImpl(getDataSource());
	}
	
	@Bean
	public ReportDAO getReportDAO() {
		return new ReportDAOImpl(getDataSource());
	}
	
	@Bean
	public DocumentsDAO getDocumentsDAO() {
		return new DocumentsDAOImpl(getDataSource());
	}
}
