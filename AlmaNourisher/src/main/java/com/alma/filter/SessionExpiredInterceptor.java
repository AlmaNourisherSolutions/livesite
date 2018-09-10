package com.alma.filter;


import com.alma.dao.UserRoleAssociationDAO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SessionExpiredInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	private UserRoleAssociationDAO userRoleAssociatinoDAO;

	private static final Logger logger = Logger.getLogger(SessionExpiredInterceptor.class);

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		HttpSession session = request.getSession(false);
		try {
			Integer userId = (Integer) session.getAttribute("userId");
			if (userId != null) {
				List roles = this.userRoleAssociatinoDAO.getRolesByUserID(userId.intValue());

				if ((roles != null) && (this.userRoleAssociatinoDAO.isAlmaAdmin(roles))) {
					session.setMaxInactiveInterval(86400);
				}
			}
		} catch (NullPointerException npe) {
			npe.getMessage();
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView view)
			throws Exception {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0L);
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
