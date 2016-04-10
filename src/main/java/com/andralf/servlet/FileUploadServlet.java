package com.andralf.servlet;

import com.andralf.util.NetworkUtil;
import com.google.common.io.Files;
import com.andralf.props.FileProviderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class FileUploadServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(FileUploadServlet.class);

	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response) throws ServletException, IOException {
		try {
			LOG.debug("HTTP request received from remote IP address: {0}",NetworkUtil.getRemoteAddress(request));
			String savePath = FileProviderProperties.getInstance().getIncomingDirectory();

			File fileSaveDir = new File(savePath);
			if (!fileSaveDir.exists()) fileSaveDir.mkdir();

			for (Part part : request.getParts()) {
				if (part != null && part.getInputStream() != null) {
					String fileName = extractFileName(part);
					byte[] buffer = new byte[part.getInputStream().available()];
					part.getInputStream().read(buffer);

					File targetFile = new File(fileSaveDir, fileName);
					Files.write(buffer, targetFile);
					LOG.info(MessageFormat.format("Stored file: {0}, Remote IP address: {1}",
							targetFile.getAbsolutePath(),
							NetworkUtil.getRemoteAddress(request)));
				}

			}
		} catch (RuntimeException re) {
			re.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Extracts file name from HTTP header content-disposition
	 */
	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}
}
