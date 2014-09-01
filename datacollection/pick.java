import java.io.*;
//import java.Math.*;
public class pick {
	public static void main(String args[]) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader("corpus.txt"));
        String s;
		int i=0,j=0,k=0;
        while ((s = in.readLine()) != null) {
            i = (int)(Math.random()*100);
            k++;
            if(s.charAt(0) <'A' || s.charAt(0)>'Z') continue;
            if(i!=23) continue;
            System.out.println(s);
            j++;
            if(j==110) break;
        }
        System.out.println(k);
	}
}
