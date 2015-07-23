/**
 * 
 */
package com.ribas.andrei.vaadin;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

/**
 * @author goncand
 * 
 */
@WebServlet(urlPatterns = { "/*" })
public class VaadinServlet extends AbstractApplicationServlet {

	private static final long serialVersionUID = -1050716413166316126L;

	@Inject
	private OpenWindowApplication applicationClass;

	@Override
	protected Application getNewApplication(HttpServletRequest request) throws ServletException {
		return applicationClass;
	}

	@Override
	protected Class<? extends Application> getApplicationClass() throws ClassNotFoundException {
		return OpenWindowApplication.class;
	}

}
