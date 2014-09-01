//package net.marquard;

import java.io.*;
import java.io.File;
import java.io.StringReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import org.xml.sax.SAXException;

import opennlp.tools.sentdetect.*;
import opennlp.tools.util.*;
public class Wikipedia2Txt {

	
	/**
	 * @param args
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws InvalidFormatException, IOException, SAXException {
				
		String dumpfile = "../enwiki-latest-pages-articles.xml";

		IArticleFilter handler = new ArticleFilter();
        WikiXMLParser wxp = new WikiXMLParser(dumpfile, handler);

		wxp.parse();
		
		FileOutputStream fop = null;
		File file;
		String content = "Done!";
		try {
			file = new File("done.txt");
			fop = new FileOutputStream(file);
 			if (!file.exists()) {
				file.createNewFile();
			}
 			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close(); 
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}
	
    /**
     * Print title an content of all the wiki pages in the dump.
     * 
     */
	static class ArticleFilter implements IArticleFilter {

		final static Pattern regex = Pattern.compile("[A-Z][\\p{L}\\w\\p{Blank},\\\"\\';\\[\\]\\(\\)-]+[\\.!]", 
				Pattern.CANON_EQ);
		
		// Convert to plain text
		WikiModel wikiModel = new WikiModel("${image}", "${title}");

		public void process(WikiArticle page, Siteinfo siteinfo) throws SAXException {

			if (page != null && page.getText() != null && !page.getText().startsWith("#REDIRECT ")){

				PrintStream out = null;

				try {
					out = new PrintStream(System.out, true, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Zap headings ==some text== or ===some text===

				// <ref>{{Cite web|url=http://tmh.floonet.net/articles/falseprinciple.html |title="The False Principle of our Education" by Max Stirner |publisher=Tmh.floonet.net |date= |accessdate=2010-09-20}}</ref>
				// <ref>Christopher Gray, ''Leaving the Twentieth Century'', p. 88.</ref>
				// <ref>Sochen, June. 1972. ''The New Woman: Feminism in Greenwich Village 1910Ð1920.'' New York: Quadrangle.</ref>

				// String refexp = "[A-Za-z0-9+\\s\\{\\}:_=''|\\.\\w#\"\\(\\)\\[\\]/,?&%Ð-]+";

				String wikiText = page.getText().
									replaceAll("[=]+[A-Za-z+\\s-]+[=]+", " ").
									replaceAll("\\{\\{[A-Za-z0-9+\\s-]+\\}\\}"," ").
									replaceAll("(?m)<ref>.+</ref>"," ").
									replaceAll("(?m)<ref name=\"[A-Za-z0-9\\s-]+\">.+</ref>"," ").
									replaceAll("<ref>"," <ref>");

				// Remove text inside {{ }}
				String plainStr = wikiModel.render(new PlainTextConverter(), wikiText).
					replaceAll("\\{\\{[A-Za-z+\\s-]+\\}\\}"," ");

				//Matcher regexMatcher = regex.matcher(plainStr);

				/*while (regexMatcher.find())
				{
					// Get sentences with 6 or more words
					String sentence = regexMatcher.group();

					if (matchSpaces(sentence, 5)) {
						out.println(sentence);
					}
				}*/
				String sentences[] = new String[1];
				try{ 
					sentences = SentenceDetect(plainStr);
				}
				catch(IOException e){
					e.printStackTrace();
				}
				//catch(InvalidFormatException e){throw e;}
				for(int i = 0 ; i < sentences.length; i++){
					if(checklist(sentences[i]) && matchSpaces(sentences[i],5)){
						 System.out.println(sentences[i]);
					}
				}
			}
		}
		private String[] SentenceDetect(String corpus) throws InvalidFormatException,IOException {
				InputStream is = new FileInputStream("en-sent.bin");
				SentenceModel model = new SentenceModel(is);
				SentenceDetectorME sdetector = new SentenceDetectorME(model);
				is.close();
				return sdetector.sentDetect(corpus);
		}
		private boolean checklist(String sentence) {
			if(sentence.length()==0) return false;
			for (int i=0; i< sentence.length(); i++) {
				char c = sentence.charAt(i);
				if(c >='a' && c<='z') continue;
				if(c>='A' && c<='Z') continue;
				if(c>='0' && c<='9') continue;
				if(c==' ' || c=='.' || c=='!' || c=='(' || c==')' || c==':' || c==';' || c==',' || c=='-' || c=='\'' || c=='$' || c=='&' || c=='"' || c=='?') continue;
				return false;
			}
			for (int i=0; i< sentence.length(); i++) {
				if (sentence.charAt(i) == ',') return true;
				if(i>1 && sentence.charAt(i) == 'r' && sentence.charAt(i-1) == 'o' && sentence.charAt(i-2) == ' ') return true;
				if (i>1 && i<sentence.length()-1){
					if(sentence.charAt(i) == 'n' && sentence.charAt(i-1) == 'a' && sentence.charAt(i+1) == 'd' && sentence.charAt(i-2) == ' ') return true;
					if(sentence.charAt(i) == 't' && sentence.charAt(i-1) == 'e' && sentence.charAt(i+1) == 'c' && sentence.charAt(i-2) == ' ') return true;
				}
			}
			return false;
		}

		private boolean matchSpaces(String sentence, int matches) {
			int c =0;
			for (int i=0; i< sentence.length(); i++) {
				if (sentence.charAt(i) == ' ') c++;
				if (c == matches) return true;
			}
			return false;
		}
	}

}

