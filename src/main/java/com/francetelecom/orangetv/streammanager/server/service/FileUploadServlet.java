package com.francetelecom.orangetv.streammanager.server.service;

import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.francetelecom.orangetv.streammanager.server.manager.AdminManager.MulticatServerInfo;
import com.francetelecom.orangetv.streammanager.server.manager.ServletManager;

public class FileUploadServlet extends HttpServlet implements IMyServices {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(FileUploadServlet.class.getName());

	private long fileSizeLimit; // param du context

	private MulticatServerInfo multicatServerInfo;

	// ------------------------------ overriding HttpServlet

	@Override
	public void init(ServletConfig config) throws ServletException {
		log.info("init()");
		super.init(config);
		ServletContext servletContext = config.getServletContext();

		this.multicatServerInfo = ServletManager.get().buildMulticatServerInfo(servletContext);
		this.fileSizeLimit = this.multicatServerInfo.getMaxUploadSizeMo() * 1024 * 1024;

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.config("doGet()");

		resp.setHeader("Access-Control-Allow-Origin", "*");

		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<h1>FileUploadServlet HTTP GET</h1>");

		long sizeMo = this.fileSizeLimit / (1024 * 1024);
		out.println("<h2>Max upload size: " + sizeMo + " Mo </h2>");
		out.println("</body>");
		out.println("</html>");

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.config("doPost()");
		try {
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
			fileUpload.setSizeMax(fileSizeLimit);

			List<FileItem> items = fileUpload.parseRequest(req);
			String response = this.executeAction(req, resp, items);

			PrintWriter out = resp.getWriter();
			out.println("<html>");
			out.println("<body>");
			out.println("<h1>FileUploadServlet HTTP POST</h1>");
			out.println("<h2>" + response + "</h2>");
			out.println("</body>");
			out.println("</html>");

		} catch (Exception e) {
			log.severe("Throwing servlet exception for unhandled exception: " + e.getMessage());
			throw new ServletException(e.getMessage());
		}
	}

	// ------------------------------------------------- private methods
	private String executeAction(HttpServletRequest request, HttpServletResponse resp, List<FileItem> sessionFiles)
			throws UploadActionException {
		log.info("executeAction()");

		String response = "";
		if (sessionFiles == null || sessionFiles.size() > 1) {
			response = "wrong session files...";
		} else {

			// only one file uploaded
			FileItem item = sessionFiles.get(0);
			if (false == item.isFormField()) {
				try {
					// / Create a new file based on the remote file name in the
					// client
					String saveName = item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+", "_");

					// sauvegarde du fichier sur le server
					log.config("path: " + this.multicatServerInfo.getUploadTomcatPath());
					File savedFile = new File(this.multicatServerInfo.getUploadTomcatPath(), saveName);
					item.write(savedFile);

					// Sauvegarde du path dans la session
					String uploadedPath = savedFile.getAbsolutePath();
					request.getSession(true).setAttribute(KEY_SESSION_GWTUPLOADED_FILE, uploadedPath);

					// / Send a customized message to the client.
					response += "File saved as " + uploadedPath;

				} catch (Exception e) {
					throw new UploadActionException(e);
				}
			}

		}

		// / Send your customized message to the client.
		return response;

	}

}
