package dev.search.com;

import java.io.IOException;

public class MainClass {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
        System.out.println( "Hello World!" );
        TextProcessor theTextProcessor = new TextProcessor();
        if(theTextProcessor.StartTextProcessing())
        	System.out.println("Success");
        else
        	System.out.println("Fail");
        
        
        //theTextProcessor.theListManager.GetDocumentFrequency("view");
        System.out.println(theTextProcessor.theListManager.GetDocumentFrequency("view"));
        
        System.out.println("Key Words");
        //theTextProcessor.theListManager.DisplayKey();
	}

}
