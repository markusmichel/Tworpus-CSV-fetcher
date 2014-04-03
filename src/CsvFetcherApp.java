import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


public class CsvFetcherApp {
	
	@Option(name="-num-tweets", usage="Number of tweets to fetch", required=true)
	private int numTweets;
	
	@Option(name="-min-chars", usage="Minimum characters per tweet", required=false)
	private int numMinChars = 0;
	
	@Option(name="-min-words", usage="Minimum words per tweet", required=false)
	private int numMinWords = 0;
	
	@Option(name="-start-date", usage="Minimum date to look for as formatted date (yyyy/MM/dd)", required=true)
	private String minDateString;
	
	@Option(name="-end-date", usage="Maximum date to look for as formatted date (yyyy/MM/dd)", required=true)
	private String maxDateString;
	
	@Option(name="-language", usage="Language of tweets to fetch (ISO-639-1 encoded)", required=false)
	private String language = "en";	
	
	@Option(name="-output-file", usage="Location + name of the output file. Default is current dir/tweets.csv.", required=false)
	private String outputFileLocation = "tweets.csv";	
	
	
	public static void main(String[] args) {
		new CsvFetcherApp().start(args);
	}
	
	private void start(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		parser.setUsageWidth(80);
		
		try {
			parser.parseArgument(args);
			
		} catch(CmdLineException ex) {
			System.err.println(ex.getMessage());
			System.err.println("java SampleMain [options...] arguments...");
			
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();
            
            return;
		}
		
		minDateString.replace('-', '/');
		maxDateString.replace('-', '/');
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = simpleDate.parse(minDateString);
			endDate = simpleDate.parse(minDateString);
		} catch (ParseException e) {
			System.err.println("Invalid date format");
			return;
		}
		
		String urlString = "http://localhost:3000/api/v1/tweets/find?format=csv&limit=" 
				+ numTweets + "&languages=" + language
				+ "&wordcount=" + numMinWords + "&charcount=" + numMinChars
				+ "&startDate=" + startDate.getTime() + "&endDate=" + endDate.getTime();
		
		try {
			URL url = new URL(urlString);
			InputStream is = url.openStream();
			String content = IOUtils.toString(is, "UTF-8");
			
			File csvFile = new File(outputFileLocation);
			if(!csvFile.exists()) {
				
				try {
					csvFile.createNewFile();
				} catch(IOException ex) {
					System.err.println("Directory does not exist or file could not be created");
					return;
				}
			}
			
			PrintWriter out = new PrintWriter(csvFile);
			out.write(content);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
