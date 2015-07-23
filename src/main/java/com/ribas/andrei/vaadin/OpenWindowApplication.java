/**
 * 
 */
package com.ribas.andrei.vaadin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.enterprise.context.SessionScoped;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * @author goncand
 * 
 */
@SuppressWarnings("serial")
@SessionScoped
public class OpenWindowApplication extends Application {

	private Window mainWindow;

	private Window subWindow;

	@Override
	public void init() {

		mainWindow = new Window("Open Window Vaadin Application");

		Button windowButton = new Button("Click here to open a new window showing HTML content.", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				final StreamResource streamSource = OpenWindowApplication.this.getStreamSource();

				ApplicationContext context = OpenWindowApplication.this.getContext();

				int width = 1024;

				int height = 768;

				if (context instanceof WebApplicationContext) {

					WebApplicationContext webApplicationContext = (com.vaadin.terminal.gwt.server.WebApplicationContext) context;

					width = webApplicationContext.getBrowser().getScreenWidth();

					height = webApplicationContext.getBrowser().getScreenHeight();

				}

				mainWindow.open(streamSource, "_new", width, height, Window.BORDER_NONE);

			}

		});

		mainWindow.addComponent(windowButton);

		Button subwindowButton = new Button("Click here to open a subwindow showing HTML content.", new Button.ClickListener() {
			// inline click-listener
			public void buttonClick(ClickEvent event) {
				createSubWindow("SubWindow with HTML content", false);
			}
		});

		mainWindow.addComponent(subwindowButton);

		Button modalSubwindowButton = new Button("Click here to open a modal subwindow showing HTML content.", new Button.ClickListener() {
			// inline click-listener
			public void buttonClick(ClickEvent event) {
				createSubWindow("Modal subWindow with HTML content", true);
			}
		});

		mainWindow.addComponent(modalSubwindowButton);

		setMainWindow(mainWindow);

	}

	private void createSubWindow(String subWindowMessage, boolean modal) {

		if (subWindow == null) {

			Embedded iframe = new Embedded(null, OpenWindowApplication.this.getStreamSource());
			iframe.setType(Embedded.TYPE_BROWSER);
			iframe.setWidth("100%");
			iframe.setHeight("100%");

			createSubWindow(subWindowMessage, modal, iframe);

		} else if (subWindow.getParent() != null) {
			mainWindow.showNotification("SubWindow is already open");
		}

	}

	private void createSubWindow(String subWindowMessage, boolean modal, Embedded iframe) {

		final Window tempSubWindow = new Window(subWindowMessage);
		tempSubWindow.setWidth("80%");
		tempSubWindow.setHeight("80%");
		tempSubWindow.center();
		tempSubWindow.setModal(modal);

		tempSubWindow.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent e) {
				closeWindow();
			}
		});

		tempSubWindow.addComponent(iframe);

		OpenWindowApplication.this.subWindow = tempSubWindow;

		OpenWindowApplication.this.mainWindow.addWindow(OpenWindowApplication.this.subWindow);

	}

	private void closeWindow() {

		if (OpenWindowApplication.this.subWindow != null) {
			OpenWindowApplication.this.mainWindow.removeWindow(OpenWindowApplication.this.subWindow);
			OpenWindowApplication.this.subWindow = null;
			mainWindow.showNotification("Window closed by user");
		}

	}

	private StreamResource getStreamSource() {

		final String filename = "report-" + System.currentTimeMillis() + ".html";

		final StreamResource streamResource = new StreamResource(new StreamResource.StreamSource() {
			public InputStream getStream() {
				return new ByteArrayInputStream(("<html><body><h1>" + filename + "</h1></body></html>").getBytes());
			}
		}, filename, OpenWindowApplication.this);

		streamResource.setMIMEType("text/html");

		return streamResource;

	}

}