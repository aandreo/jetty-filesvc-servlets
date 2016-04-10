package com.andralf;

import com.andralf.props.FileProviderProperties;
import com.andralf.servlet.FileDownloadServlet;
import com.andralf.servlet.FileUploadServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Main {

	public static final String FILE_UPLOAD_SERVLET_PATH = "/FileUpload";
	public static final String FILE_DOWNLOAD_SERVLET_PATH = "/FileDownload";
	private static int DEFAULT_PORT = 8080;

	private Server server;

	public static void main(String[] args) throws Exception{

		int port = DEFAULT_PORT;

		if (args != null){
			if (args.length >= 1){
				try{
					port = Integer.valueOf(args[0]);
				}catch(NumberFormatException nfe){
					throw new RuntimeException("Given port number is not numeric");
				}
			}
		}

		Main main = new Main();
		main.start(port, true);
	}

	public void start(int port, boolean joinAndWaitUntilStopped) throws Exception{
		// Create a basic jetty server object that will listen on port 8080.
		// Note that if you set this to port 0 then a randomly available port
		// will be assigned that you can either look in the logs for the port,
		// or programmatically obtain it for use in test cases.

		server = new Server(port);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		DefaultHandler defaultHandler = new DefaultHandler();
		defaultHandler.setServeIcon(false);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{context, defaultHandler});
		server.setHandler(handlers);

		ServletHolder fileUploadServletHolder = new ServletHolder(FileUploadServlet.class);
		fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("",
				1024*1024* FileProviderProperties.getInstance().getMaxFileSize(),
				1024*1024* FileProviderProperties.getInstance().getMaxFileSize(),
				1024*1024* FileProviderProperties.getInstance().getMaxFileSize()));

		context.addServlet(fileUploadServletHolder, FILE_UPLOAD_SERVLET_PATH);
		context.addServlet(FileDownloadServlet.class, FILE_DOWNLOAD_SERVLET_PATH);

		context.setErrorHandler(new ErrorHandler());

		// Start things up!
		server.start();

		// The use of server.join() the will make the current thread join and
		// wait until the server is done executing.
		// See
		// http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
		if (joinAndWaitUntilStopped) server.join();
	}

	public void stop() throws Exception{
		server.stop();
	}

	private static class ErrorHandler extends ErrorPageErrorHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
			response.getWriter()
					.append("ERROR: HTTP ")
					.append(String.valueOf(response.getStatus()));
		}
	}

}
