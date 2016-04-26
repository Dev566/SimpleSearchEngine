package dev.search.com;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MySearch
 */
public class MySearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String message;
	String Domain;
	Boolean ReadyToProcess;
	TextProcessor theTextProcessor;

	/**
	 * @throws IOException
	 * @see HttpServlet#HttpServlet()
	 */
	public MySearch() throws IOException {
		super();
		// TODO Auto-generated constructor stub

	}

	public void ProcessQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Boolean okTOgo = false;
		theTextProcessor = new TextProcessor();
		if (!Domain.isEmpty() && theTextProcessor.OnlineCrawler == false)
			theTextProcessor.SetOnlineCrawler(Domain);
		ReadyToProcess = theTextProcessor.StartTextProcessing();
		if (ReadyToProcess)
			okTOgo = true;
		else
			System.out.println("Fail");
		if (okTOgo) {
			// response.getWriter().append(" <H1>Frequency :" +
			// Integer.toString(theTextProcessor.theListManager.GetDocumentFrequency(message))+"</H1>");
			theTextProcessor.tfIdf();
			theTextProcessor.theListManager.CreateMatric(theTextProcessor.NumberOfDoc);
			theTextProcessor.findSimilarity(message);
			theTextProcessor.ProcessQuery(message);
			response.getWriter().append(" <BR>");
			if (theTextProcessor.ResultSimilarity != null) {
				Set<String> ResultKey = theTextProcessor.FinalResult.keySet();
				ArrayList<String> tempArray1;
				for (String tempKey : ResultKey) {

					System.out.println("Similar : " + tempKey);
					tempArray1 = (ArrayList<String>) theTextProcessor.FinalResult.get(tempKey);
					for (String str : tempArray1) {
						response.getWriter().append(" <BR><h4>Doc ID : " + str + " : " + tempKey + "</BR></h4>");
						if(theTextProcessor.spider!=null)
						{
							String SiteLink = theTextProcessor.spider.DocumentLinkMap.get(str);
							response.getWriter().append("  <a href="+ SiteLink+"> "+SiteLink+" </a> ");
					}}
					
				}
			}

		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter myPrint = response.getWriter();
		response.getWriter().append("<marquee><H1><center>Welcome </H1></marquee>").append(request.getContextPath());
		myPrint.println("\n" + "Response from Server....");
		message = request.getParameter("Query");
		Domain = request.getParameter("DomainName");
		ProcessQuery(request, response);
		myPrint.println("<BR><center>" + "Original Query <H2>" + message + "</BR></H2>");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		message = request.getParameter("Query");

	}

}
