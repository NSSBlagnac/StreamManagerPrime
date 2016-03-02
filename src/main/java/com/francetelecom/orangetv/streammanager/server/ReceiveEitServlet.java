package com.francetelecom.orangetv.streammanager.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.francetelecom.orangetv.streammanager.server.dao.StreamDao;
import com.francetelecom.orangetv.streammanager.server.util.EitJsonParser;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;

public class ReceiveEitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Reception des eit à injecter dans une requete POST (body json
	 * stringified)
	 * Appel du program C 'eitinjector' pour traiter ces eit
	 */
	private static final Logger log = Logger.getLogger(ReceiveEitServlet.class.getName());

	// private static final EitCallCProgram eitCallCProgram =
	// EitCallCProgram.get();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.fine("doGet()");

		resp.setHeader("Access-Control-Allow-Origin", "*");

		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<h1>ReceiveEitServlet Servlet GET</h1>");
		out.println("</body>");
		out.println("</html>");

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.fine("doPost()");
		this.processRequest(req, resp);

	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String payloadRequest = getBody(req);
			log.info("JSON: " + payloadRequest);
			EitInfoModel eitInfoModel = EitJsonParser.parse(payloadRequest);

			if (eitInfoModel != null) {

				boolean result = StreamDao.get().updateEit(eitInfoModel);
				if (result) {

					PrintWriter out = resp.getWriter();
					out.println("<html>");
					out.println("<body>");
					out.println("<h1>ReceiveEitServlet Servlet POST</h1>");
					out.println("<p>");
					out.println(payloadRequest);
					out.println("</p>");
					out.println("</body>");
					out.println("</html>");
					return;
				}
			}

			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (Throwable ex) {
			log.severe("Error processing request: " + ex.getMessage());
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	public static String getBody(HttpServletRequest request) throws IOException {

		// Read from request
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

}
