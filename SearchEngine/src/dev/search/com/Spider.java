package dev.search.com;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Spider {

	 	private static final int MAX_PAGES_TO_SEARCH = 10;
	    private Set<String> pagesVisited = new HashSet<String>();
	    private List<String> pagesToVisit = new LinkedList<String>();
	    static int filecount=0;
	    static TreeMap<String, String> DocumentLinkMap = new TreeMap<String, String>();
	    
	    public void visit(String url)
	    {
	        while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH)
	        {
	            String currentUrl;
	            SpiderLeg leg = new SpiderLeg();
	            if(this.pagesToVisit.isEmpty())
	            {
	                currentUrl = url;
	                this.pagesVisited.add(url);
	                System.out.println(currentUrl);
	            }
	            else
	            {
	                currentUrl = this.nextUrl();
	            }
	            leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
	                                   // SpiderLeg
	            /* boolean success = leg.searchForWord(searchWord);
	            if(success)
	            {
	                System.out.println(String.format("**Success** Word %s found at %s", searchWord, currentUrl));
	                break;
	            } */
	            
	            DocumentLinkMap.put(Integer.toString(filecount), currentUrl);
	            filecount++;
	            this.pagesToVisit.addAll(leg.getLinks());
	        }
	        System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
	        //System.out.println(pagesVisited);
	        //System.out.println(pagesToVisit);
	    }
	    private String nextUrl()
	    {
	        String nextUrl;
	        do
	        {
	            nextUrl = this.pagesToVisit.remove(0);
	        } while(this.pagesVisited.contains(nextUrl));
	        this.pagesVisited.add(nextUrl);
	        return nextUrl;
	    }
//	    public static void main(String[] args)
//	    {
//	        Spider spider = new Spider();
//	        spider.visit("http://ku.edu/");
//	    }
}