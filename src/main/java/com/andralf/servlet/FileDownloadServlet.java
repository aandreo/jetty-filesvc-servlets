package com.andralf.servlet;

import com.andralf.util.NetworkUtil;
import com.google.common.base.Strings;
import com.andralf.props.FileProviderProperties;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

public class FileDownloadServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(FileDownloadServlet.class);

	public static final String FILENAME_PARAM_NAME = "filename";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = request.getParameter(FILENAME_PARAM_NAME);
		if (Strings.isNullOrEmpty(filename) || FilenameUtils.normalizeNoEndSeparator(filename) == null) {
			int returnCode = HttpServletResponse.SC_NOT_FOUND;
			LOG.warn(MessageFormat.format("Filename parameter value is empty or invalid: {0}. HTTP {1} returned. Remote IP address: {2}",
					filename,
					Integer.toString(returnCode),
					NetworkUtil.getRemoteAddress(request)));
			response.setStatus(returnCode);
			return;
		}

		File file = new File(FileProviderProperties.getInstance().getIncomingDirectory(),
				FilenameUtils.normalize(request.getParameter(FILENAME_PARAM_NAME)));
		file = new File(file.toURI().normalize());
		if (!file.exists()) {
			int returnCode = HttpServletResponse.SC_NOT_FOUND;
			LOG.warn(MessageFormat.format(
					"Could not find equivalent file specified in filename parameter value: {0}. HTTP {1} returned. Remote IP address: {2}",
					filename,
					Integer.toString(returnCode),
					NetworkUtil.getRemoteAddress(request)));
			response.setStatus(returnCode);
			return;
		}

		try(
			InputStream fis = new FileInputStream(file);
			ServletOutputStream os = response.getOutputStream();
		){
			ServletContext ctx = getServletContext();
			String mimeType = ctx.getMimeType(file.getAbsolutePath());
			response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
			response.setContentLength((int) file.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			byte[] bufferData = new byte[1024];
			int read;
			while ((read = fis.read(bufferData)) != -1) {
				os.write(bufferData, 0, read);
			}
			os.flush();
			LOG.info(MessageFormat.format("File downloaded: {0}. Remote IP address: {1}", filename,
					NetworkUtil.getRemoteAddress(request)));
		}

	}


}
