/* 
The MIT License (MIT)
Copyright (c) 2015 Andrei Gonçalves Ribas <andrei.g.ribas@gmail.com>
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
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
 * @author Andrei Gonçalves Ribas <andrei.g.ribas@gmail.com>
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

		Button windowButton = new Button(
				"Click here to open a new window showing HTML content.",
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {

						final StreamResource streamSource = OpenWindowApplication.this
								.getStreamSource();

						ApplicationContext context = OpenWindowApplication.this
								.getContext();

						int width = 1024;

						int height = 768;

						if (context instanceof WebApplicationContext) {

							WebApplicationContext webApplicationContext = (com.vaadin.terminal.gwt.server.WebApplicationContext) context;

							width = webApplicationContext.getBrowser()
									.getScreenWidth();

							height = webApplicationContext.getBrowser()
									.getScreenHeight();

						}

						mainWindow.open(streamSource, "_new", width, height,
								Window.BORDER_NONE);

					}

				});

		mainWindow.addComponent(windowButton);

		Button subwindowButton = new Button(
				"Click here to open a subwindow showing HTML content.",
				new Button.ClickListener() {
					// inline click-listener
					public void buttonClick(ClickEvent event) {
						createSubWindow("SubWindow with HTML content", false);
					}
				});

		mainWindow.addComponent(subwindowButton);

		Button modalSubwindowButton = new Button(
				"Click here to open a modal subwindow showing HTML content.",
				new Button.ClickListener() {
					// inline click-listener
					public void buttonClick(ClickEvent event) {
						createSubWindow("Modal subWindow with HTML content",
								true);
					}
				});

		mainWindow.addComponent(modalSubwindowButton);

		setMainWindow(mainWindow);

	}

	private void createSubWindow(String subWindowMessage, boolean modal) {

		if (subWindow == null) {

			Embedded iframe = new Embedded(null,
					OpenWindowApplication.this.getStreamSource());
			iframe.setType(Embedded.TYPE_BROWSER);
			iframe.setWidth("100%");
			iframe.setHeight("100%");

			createSubWindow(subWindowMessage, modal, iframe);

		} else if (subWindow.getParent() != null) {
			mainWindow.showNotification("SubWindow is already open");
		}

	}

	private void createSubWindow(String subWindowMessage, boolean modal,
			Embedded iframe) {

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

		OpenWindowApplication.this.mainWindow
				.addWindow(OpenWindowApplication.this.subWindow);

	}

	private void closeWindow() {

		if (OpenWindowApplication.this.subWindow != null) {
			OpenWindowApplication.this.mainWindow
					.removeWindow(OpenWindowApplication.this.subWindow);
			OpenWindowApplication.this.subWindow = null;
			mainWindow.showNotification("Window closed by user");
		}

	}

	private StreamResource getStreamSource() {

		final String filename = "report-" + System.currentTimeMillis()
				+ ".html";

		final StreamResource streamResource = new StreamResource(
				new StreamResource.StreamSource() {
					public InputStream getStream() {
						return new ByteArrayInputStream(("<html><body><h1>"
								+ filename + "</h1></body></html>").getBytes());
					}
				}, filename, OpenWindowApplication.this);

		streamResource.setMIMEType("text/html");

		return streamResource;

	}

}