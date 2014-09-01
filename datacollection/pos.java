import java.io.*;
import java.io.File;
import java.io.StringReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

import opennlp.tools.sentdetect.*;
import opennlp.tools.util.*;
import opennlp.tools.postag.*;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class pos{
    static POSModel model;
    static POSTaggerME tagger;

    public static boolean containsCD(String line) throws IOException{

        String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
        String[] tags = tagger.tag(whitespaceTokenizerLine);

        for(int i=0;i<tags.length;i++){
            // System.out.println(tags[i] +  " : " + whitespaceTokenizerLine[i]);
            if(tags[i].equals("CD"))
                return true;
        }
        return false;
    }
    public static void modelInit(){
        model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
        tagger = new POSTaggerME(model);
    }
    public static void main(String[] args) throws IOException{
        modelInit();
        BufferedReader in = new BufferedReader(new FileReader(args[0]));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line + " : " + containsCD(line));
        }
    }
}
