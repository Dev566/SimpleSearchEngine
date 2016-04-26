package dev.search.com;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg {
	
	// We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    


    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url)
    {
        try
        {
        	String DirLoc = "F:/IR/CrawledPages/";
//        	File DirectoryTOClear = new File(DirLoc);
//        	FileUtils.cleanDirectory(DirectoryTOClear); 
        	Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            
            String abc = url.replace(':', '_');
            abc = abc.replace('/', '-');
            //System.out.println(abc);
            String file = DirLoc.concat(abc).concat(".txt");
            //System.out.println(file);
            PrintWriter write = new PrintWriter(file);
            //String fil = abcd.txt;
            //String filename = fil;
            //System.out.println(filename);
            //PrintWriter out = new PrintWriter("filename.txt");
            //System.out.println(fil);
            //String baseName = FilenameUtils.getBaseName(url);
            //String extension = FilenameUtils.getExtension(url);

            //System.out.println("Basename : " + baseName);
            //System.out.println("extension : " + extension);
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            String bodyText = this.htmlDocument.body().text();
            //System.out.println(bodyText);
            write.println(bodyText);
            for(Element link : linksOnPage)
            {
                this.links.add(link.absUrl("href"));
            }
            write.close();
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }

    public List<String> getLinks()
    {
        return this.links;
    }


}