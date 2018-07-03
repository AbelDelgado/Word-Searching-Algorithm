//Stuff for Hashmap
//Stuff for sorting hashmaps
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.lang.Double;
public class search {

	private static final String STOPWORDS = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\stopwords.txt";
	private static final String FILENAME = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\test.txt";
	private static final String DICTIONARY = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\dictionary.txt";
	//cacm.all

	private static final String POSTING = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\posting.txt";
	
	
	
	public static boolean stopWordsSetting = true;
	public static boolean allowStemming = false;
	
	public static List<String> stopWordsCreation() throws Exception 
	{
		BufferedReader br = null;
		FileReader fr = null;

		List<String> stopWordsFunction = new ArrayList<String>();
		String sCurrentLine;
		try {
			fr = new FileReader(STOPWORDS);
			br = new BufferedReader(fr);

			while ((sCurrentLine = br.readLine()) != null) {
				stopWordsFunction.add(sCurrentLine);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

		return stopWordsFunction;
	}

	
	// 				STEMMER STARTS
	
	 private static final String STEMMING = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\stemmed.txt";
	    private char[] b;
	    private int i,     /* offset into b */
	    i_end, /* offset to end of stemmed word */
	    j, k;
	    private static final int INC = 50;
	    /* unit of size whereby b is increased */
	    public search()
	    {  b = new char[INC];
	        i = 0;
	        i_end = 0;
	    }

	    /**
	     * Add a character to the word being stemmed.  When you are finished
	     * adding characters, you can call stem(void) to stem the word.
	     */

	    public void add(char ch)
	    {  if (i == b.length)
	        {  char[] new_b = new char[i+INC];
	            for (int c = 0; c < i; c++) new_b[c] = b[c];
	            b = new_b;
	        }
	        b[i++] = ch;
	    }

	    /** Adds wLen characters to the word being stemmed contained in a portion
	     * of a char[] array. This is like repeated calls of add(char ch), but
	     * faster.
	     */

	    public void add(char[] w, int wLen)
	    {  if (i+wLen >= b.length)
	        {  char[] new_b = new char[i+wLen+INC];
	            for (int c = 0; c < i; c++) new_b[c] = b[c];
	            b = new_b;
	        }
	        for (int c = 0; c < wLen; c++) b[i++] = w[c];
	    }

	    /**
	     * After a word has been stemmed, it can be retrieved by toString(),
	     * or a reference to the internal buffer can be retrieved by getResultBuffer
	     * and getResultLength (which is generally more efficient.)
	     */
	    public String toString() { return new String(b,0,i_end); }

	    /**
	     * Returns the length of the word resulting from the stemming process.
	     */
	    public int getResultLength() { return i_end; }

	    /**
	     * Returns a reference to a character buffer containing the results of
	     * the stemming process.  You also need to consult getResultLength()
	     * to determine the length of the result.
	     */
	    public char[] getResultBuffer() { return b; }

	    /* cons(i) is true <=> b[i] is a consonant. */

	    private final boolean cons(int i)
	    {  switch (b[i])
	        {  case 'a': case 'e': case 'i': case 'o': case 'u': return false;
	            case 'y': return (i==0) ? true : !cons(i-1);
	            default: return true;
	        }
	    }

	    /* m() measures the number of consonant sequences between 0 and j. if c is
	    a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
	    presence,

	    <c><v>       gives 0
	    <c>vc<v>     gives 1
	    <c>vcvc<v>   gives 2
	    <c>vcvcvc<v> gives 3
	    ....
	     */

	    private final int m()
	    {  int n = 0;
	        int i = 0;
	        while(true)
	        {  if (i > j) return n;
	            if (! cons(i)) break; i++;
	        }
	        i++;
	        while(true)
	        {  while(true)
	            {  if (i > j) return n;
	                if (cons(i)) break;
	                i++;
	            }
	            i++;
	            n++;
	            while(true)
	            {  if (i > j) return n;
	                if (! cons(i)) break;
	                i++;
	            }
	            i++;
	        }
	    }

	    /* vowelinstem() is true <=> 0,...j contains a vowel */

	    private final boolean vowelinstem()
	    {  int i; for (i = 0; i <= j; i++) if (! cons(i)) return true;
	        return false;
	    }

	    /* doublec(j) is true <=> j,(j-1) contain a double consonant. */

	    private final boolean doublec(int j)
	    {  if (j < 1) return false;
	        if (b[j] != b[j-1]) return false;
	        return cons(j);
	    }

	    /* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
	    and also if the second c is not w,x or y. this is used when trying to
	    restore an e at the end of a short word. e.g.

	    cav(e), lov(e), hop(e), crim(e), but
	    snow, box, tray.

	     */

	    private final boolean cvc(int i)
	    {  if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2)) return false;
	        {  int ch = b[i];
	            if (ch == 'w' || ch == 'x' || ch == 'y') return false;
	        }
	        return true;
	    }

	    private final boolean ends(String s)
	    {  int l = s.length();
	        int o = k-l+1;
	        if (o < 0) return false;
	        for (int i = 0; i < l; i++) if (b[o+i] != s.charAt(i)) return false;
	        j = k-l;
	        return true;
	    }

	    /* setto(s) sets (j+1),...k to the characters in the string s, readjusting
	    k. */

	    private final void setto(String s)
	    {  int l = s.length();
	        int o = j+1;
	        for (int i = 0; i < l; i++) b[o+i] = s.charAt(i);
	        k = j+l;
	    }

	    /* r(s) is used further down. */

	    private final void r(String s) { if (m() > 0) setto(s); }

	    /* step1() gets rid of plurals and -ed or -ing. e.g.

	    caresses  ->  caress
	    ponies    ->  poni
	    ties      ->  ti
	    caress    ->  caress
	    cats      ->  cat

	    feed      ->  feed
	    agreed    ->  agree
	    disabled  ->  disable

	    matting   ->  mat
	    mating    ->  mate
	    meeting   ->  meet
	    milling   ->  mill
	    messing   ->  mess

	    meetings  ->  meet

	     */

	    private final void step1()
	    {  if (b[k] == 's')
	        {  if (ends("sses")) k -= 2; else
	            if (ends("ies")) setto("i"); else
	            if (b[k-1] != 's') k--;
	        }
	        if (ends("eed")) { if (m() > 0) k--; } else
	        if ((ends("ed") || ends("ing")) && vowelinstem())
	        {  k = j;
	            if (ends("at")) setto("ate"); else
	            if (ends("bl")) setto("ble"); else
	            if (ends("iz")) setto("ize"); else
	            if (doublec(k))
	            {  k--;
	                {  int ch = b[k];
	                    if (ch == 'l' || ch == 's' || ch == 'z') k++;
	                }
	            }
	            else if (m() == 1 && cvc(k)) setto("e");
	        }
	    }

	    /* step2() turns terminal y to i when there is another vowel in the stem. */

	    private final void step2() { if (ends("y") && vowelinstem()) b[k] = 'i'; }

	    /* step3() maps double suffices to single ones. so -ization ( = -ize plus
	    -ation) maps to -ize etc. note that the string before the suffix must give
	    m() > 0. */

	    private final void step3() { if (k == 0) return; /* For Bug 1 */ switch (b[k-1])
	        {
	            case 'a': if (ends("ational")) { r("ate"); break; }
	            if (ends("tional")) { r("tion"); break; }
	            break;
	            case 'c': if (ends("enci")) { r("ence"); break; }
	            if (ends("anci")) { r("ance"); break; }
	            break;
	            case 'e': if (ends("izer")) { r("ize"); break; }
	            break;
	            case 'l': if (ends("bli")) { r("ble"); break; }
	            if (ends("alli")) { r("al"); break; }
	            if (ends("entli")) { r("ent"); break; }
	            if (ends("eli")) { r("e"); break; }
	            if (ends("ousli")) { r("ous"); break; }
	            break;
	            case 'o': if (ends("ization")) { r("ize"); break; }
	            if (ends("ation")) { r("ate"); break; }
	            if (ends("ator")) { r("ate"); break; }
	            break;
	            case 's': if (ends("alism")) { r("al"); break; }
	            if (ends("iveness")) { r("ive"); break; }
	            if (ends("fulness")) { r("ful"); break; }
	            if (ends("ousness")) { r("ous"); break; }
	            break;
	            case 't': if (ends("aliti")) { r("al"); break; }
	            if (ends("iviti")) { r("ive"); break; }
	            if (ends("biliti")) { r("ble"); break; }
	            break;
	            case 'g': if (ends("logi")) { r("log"); break; }
	        } }

	    /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */

	    private final void step4() { switch (b[k])
	        {
	            case 'e': if (ends("icate")) { r("ic"); break; }
	            if (ends("ative")) { r(""); break; }
	            if (ends("alize")) { r("al"); break; }
	            break;
	            case 'i': if (ends("iciti")) { r("ic"); break; }
	            break;
	            case 'l': if (ends("ical")) { r("ic"); break; }
	            if (ends("ful")) { r(""); break; }
	            break;
	            case 's': if (ends("ness")) { r(""); break; }
	            break;
	        } }

	    /* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */

	    private final void step5()
	    {   if (k == 0) return; /* for Bug 1 */ switch (b[k-1])
	        {  case 'a': if (ends("al")) break; return;
	            case 'c': if (ends("ance")) break;
	            if (ends("ence")) break; return;
	            case 'e': if (ends("er")) break; return;
	            case 'i': if (ends("ic")) break; return;
	            case 'l': if (ends("able")) break;
	            if (ends("ible")) break; return;
	            case 'n': if (ends("ant")) break;
	            if (ends("ement")) break;
	            if (ends("ment")) break;
	            /* element etc. not stripped before the m */
	            if (ends("ent")) break; return;
	            case 'o': if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
	            /* j >= 0 fixes Bug 2 */
	            if (ends("ou")) break; return;
	            /* takes care of -ous */
	            case 's': if (ends("ism")) break; return;
	            case 't': if (ends("ate")) break;
	            if (ends("iti")) break; return;
	            case 'u': if (ends("ous")) break; return;
	            case 'v': if (ends("ive")) break; return;
	            case 'z': if (ends("ize")) break; return;
	            default: return;
	        }
	        if (m() > 1) k = j;
	    }

	    /* step6() removes a final -e if m() > 1. */

	    private final void step6()
	    {  j = k;
	        if (b[k] == 'e')
	        {  int a = m();
	            if (a > 1 || a == 1 && !cvc(k-1)) k--;
	        }
	        if (b[k] == 'l' && doublec(k) && m() > 1) k--;
	    }

	    /** Stem the word placed into the Stemmer buffer through calls to add().
	     * Returns true if the stemming process resulted in a word different
	     * from the input.  You can retrieve the result with
	     * getResultLength()/getResultBuffer() or toString().
	     */
	    public void stem()
	    {  k = i - 1;
	        if (k > 1) { step1(); step2(); step3(); step4(); step5(); step6(); }
	        i_end = k+1; i = 0;
	    }

	    /** Test program for demonstrating the Stemmer.  It reads text from a
	     * a list of files, stems each word, and writes the result to standard
	     * output. Note that the word stemmed is expected to be in lower case:
	     * forcing lower case must be done outside the Stemmer class.
	     * Usage: Stemmer file-name file-name ...
	     */
	    
	    
	    
	    		// STEMMER ENDS
	    
	    
	    
	    				

	public static void main(String[] args) throws Exception 
	{
		List<String> stopWords = new ArrayList<String>();
		stopWords = stopWordsCreation();


		
		HashMap<Integer, String> hmap = new HashMap<Integer, String>();	//Used in tittle, all terms are unique, and any dups become " "
	
		
		List<String> uniqueTerms = new ArrayList<String>();
		List<String> allTerms = new ArrayList<String>();
				
		
		HashMap<Integer, String> hmap2 = new HashMap<Integer, String>();
		HashMap<Integer, String> allValues = new HashMap<Integer, String>();
		HashMap<Integer, Double> docNorms = new HashMap<Integer, Double>();
		
		
		
		
		Map<Integer, List<String>> postingsFileListAllWords = new HashMap<>();		
		Map<Integer, List<String>> postingsFileList = new HashMap<>();
		
		Map<Integer, List<StringBuilder>> docAndTitles = new HashMap<>();
		Map<Integer, List<StringBuilder>> docAndAbstract = new HashMap<>();
		Map<Integer, List<StringBuilder>> docAndAuthors = new HashMap<>();
		
		
		Map<Integer, List<Double>> termWeights = new HashMap<>();
		
		List<Integer> docTermCountList = new ArrayList<Integer>();
		
		BufferedReader br = null;
		FileReader fr = null;
		String sCurrentLine;

		int documentCount = 0;
		int documentFound = 0;
		int articleNew = 0;
		int docTermCount = 0;
		
		
		boolean abstractReached = false;

		try {
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			// Continues to get 1 line from document until it reaches the end of EVERY doc
			while ((sCurrentLine = br.readLine()) != null) 
			{
				// sCurrentLine now contains the 1 line from the document

				// Take line and split each word and place them into array
				String[] arr = sCurrentLine.split(" ");


				//Go through the entire array
				for (String ss : arr) 
				{
					
					/*
					 * This section takes the array and checks to see if it has reached a new
					 * document or not. If the current line begins with an .I, then it knows that a
					 * document has just started. If it incounters another .I, then it knows that a
					 * new document has started.
					 */
					//System.out.println("Before anything: "+sCurrentLine);
					if (arr[0].equals(".I")) 
					{						
						if (articleNew == 0) 
						{
							articleNew = 1;
							documentFound = Integer.parseInt(arr[1]);
						} 
						else if (articleNew == 1) 
						{
							articleNew = 0;
							documentFound = Integer.parseInt(arr[1]);
						}			
						//System.out.println(documentFound);
						//count++;
						break;
					}
					
					
					/* This section detects that after a document has entered,
					 * it has to gather all the words contained in the title 
					 * section.
					 */
					if (arr[0].equals(".T") ) 
					{
							
						// Go to line UNDER .T since that is where tittle is located
						//sCurrentLine = br.readLine();
						docAndTitles.put(documentFound, new ArrayList<StringBuilder>());
						//System.out.println("docAndTitles.get(documentCount+1): " +docAndTitles.get(documentFound));
						//System.out.println("Docs and titles: "+docAndTitles);
						StringBuilder title = new StringBuilder();
						
						while ( !(sCurrentLine = br.readLine()).matches(".B|.A|.N|.X|.K|.C") )
						{				
							/* In this section, there are 2 lists being made. One list
							 * is for all the unique words in every title in the document (hmap).
							 * Hmap contains all unique words, and anytime a duplicate word is 
							 * found, it is replaced with an empty space in the map.
							 * All Values 
							 * 
							 */
							
							//postingsFileList.put(documentFound - 1, new ArrayList<String>());
							//postingsFileList.get(documentFound - 1).add(term2);
							
							//System.out.println("current line: "+sCurrentLine);
							
							String[] tittle = sCurrentLine.split(" ");
							if (tittle[0].equals(".W") )
							{		
								abstractReached = true;
								break;								
							}
							
							title.append(sCurrentLine);
							
							for (String tittleWords : tittle)
							{	
								
								tittleWords = tittleWords.toLowerCase();
								tittleWords = tittleWords.replaceAll("[-&^%'{}*|$+\\/\\?!<>=.,;_:()\\[\\]\"\\d]", "");
								tittleWords = tittleWords.replaceAll("[-&^%'*{}|$+\\/\\?!<>=.,;_:()\\[\\]\"\\d]", "");
								tittleWords = tittleWords.replaceAll("[-&^%'{}*|$+\\/\\?!<>=.,;_:()\\[\\]\"\\d]", "");
								//System.out.println(tittleWords);
								if (hmap.containsValue(tittleWords)) 
								{																																			
									hmap.put(documentCount, " ");
									allValues.put(documentCount, tittleWords);
									allTerms.add(tittleWords);
									documentCount++;
								}
								else 
								{
									allTerms.add(tittleWords);
									allValues.put(documentCount, tittleWords);
									hmap.put(documentCount, tittleWords);
									if (!(uniqueTerms.contains(tittleWords)))
									{
										if ((stopWordsSetting && !(stopWords.contains(tittleWords))))
											uniqueTerms.add(tittleWords);
									}
									
									documentCount++;
								}							
							}
							//docAndTitles.get(documentCount).add(" ");
							title.append("\n");
						}
						//System.out.println("Title: "+title);
						//System.out.println("docAndTitles.get(documentCount+1): " +docAndTitles.get(documentFound));
						docAndTitles.get(documentFound).add(title);
						//System.out.println("Done!: "+ docAndTitles);
					}
					
					
					if (arr[0].equals(".A") ) 
					{
							
						// Go to line UNDER .T since that is where tittle is located
						//sCurrentLine = br.readLine();
						docAndAuthors.put(documentFound, new ArrayList<StringBuilder>());
						//System.out.println("docAndTitles.get(documentCount+1): " +docAndTitles.get(documentFound));
						//System.out.println("Docs and titles: "+docAndTitles);
						StringBuilder author = new StringBuilder();
						
						
						
						while ( !(sCurrentLine = br.readLine()).matches(".N|.X|.K|.C") )
						{											
							/* In this section, there are 2 lists being made. One list
							 * is for all the unique words in every title in the document (hmap).
							 * Hmap contains all unique words, and anytime a duplicate word is 
							 * found, it is replaced with an empty space in the map.
							 * All Values 
							 * 
							 */
							
							//postingsFileList.put(documentFound - 1, new ArrayList<String>());
							//postingsFileList.get(documentFound - 1).add(term2);
							
							//System.out.println("current line: "+sCurrentLine);
							
							String[] tittle = sCurrentLine.split(" ");
							if (tittle[0].equals(".W") )
							{		
								abstractReached = true;
								break;								
							}
							
							author.append(sCurrentLine);
							
							
							//docAndTitles.get(documentCount).add(" ");
							author.append("\n");
						}
						//System.out.println("Title: "+title);
						//System.out.println("docAndTitles.get(documentCount+1): " +docAndTitles.get(documentFound));
						docAndAuthors.get(documentFound).add(author);
						//System.out.println("Done!: "+ docAndTitles);
					}
					
					
					/* Since there may or may not be an asbtract after
					 * the title, we need to check what the next section
					 * is. We know that every doc has a publication date,
					 * so it can end there, but if there is no abstract,
					 * then it will keep scanning until it reaches the publication
					 * date. If abstract is empty (in tests), it will also finish instantly
					 * since it's blank and goes straight to .B (the publishing date).
					 * Works EXACTLY like Title 		 
					 */
					
					
					if (abstractReached) 
					{	
						//System.out.println("\n");
						//System.out.println("REACHED ABSTRACT and current line is: " +sCurrentLine);
						docAndAbstract.put(documentFound, new ArrayList<StringBuilder>());
						StringBuilder totalAbstract = new StringBuilder();
						
						while ( !(sCurrentLine = br.readLine()).matches(".T|.I|.A|.N|.X|.K|.C")  )
						{	
							String[] abstaract = sCurrentLine.split(" ");
							if (abstaract[0].equals(".B") )
							{			
								abstractReached = false;
								break;								
							}
							totalAbstract.append(sCurrentLine);
							
							String[] misc = sCurrentLine.split(" ");
							for (String miscWords : misc) 
							{					
								
								miscWords = miscWords.toLowerCase();  
								miscWords = miscWords.replaceAll("[-&^%'*$+|{}?!\\/<\\>=.,;_:()\\[\\]\"\\d]", "");
								miscWords = miscWords.replaceAll("[-&^%'*$+|?{}!\\/<\\>=.,;_:()\\[\\]\"\\d]", "");		
								miscWords = miscWords.replaceAll("[-&^%'*$+|{}?!\\/<\\>=.,;_:()\\[\\]\"\\d]", "");		
								//System.out.println(miscWords);
								if (hmap.containsValue(miscWords)) 
								{
									hmap.put(documentCount, " ");
									allValues.put(documentCount, miscWords);
									allTerms.add(miscWords);
									documentCount++;
	
								} 
								else 
								{
									allTerms.add(miscWords);
									hmap.put(documentCount, miscWords);
									allValues.put(documentCount, miscWords);
									if (!(uniqueTerms.contains(miscWords)))
									{
										if ((stopWordsSetting && !(stopWords.contains(miscWords))))
											uniqueTerms.add(miscWords);
									}
																
									documentCount++;
								}
							}	
							totalAbstract.append("\n");
						}
						docAndAbstract.get(documentFound).add(totalAbstract);
					}
					
			
					
					
				}	
				
				//Once article is found, we enter all of of it's title and abstract terms 
				if (articleNew == 0) 
				{
					
					documentFound = documentFound - 1;
					//System.out.println("Words found in Doc: " + documentFound);
					//System.out.println("Map is" +allValues);
					Set set = hmap.entrySet();
					Iterator iterator = set.iterator();

					Set set2 = allValues.entrySet();
					Iterator iterator2 = set2.iterator();

					
					
					postingsFileList.put(documentFound - 1, new ArrayList<String>());
					postingsFileListAllWords.put(documentFound - 1, new ArrayList<String>());
					while (iterator.hasNext()) {
						Map.Entry mentry = (Map.Entry) iterator.next();
						// ("key is: "+ mentry.getKey() + " & Value is: " + mentry.getValue());
						// ("Value is: " + mentry.getValue());
						// );
						String term = mentry.getValue().toString();
						// //"This is going to be put in doc3: "+ (documentFound-1));
						postingsFileList.get(documentFound - 1).add(term);
						// if ( !((mentry.getValue()).equals(" ")) )
						docTermCount++;
					}
					// "BEFORE its put in, this is what it looks like" + hmap);
					hmap2.putAll(hmap);
					hmap.clear();
					articleNew = 1;

					docTermCountList.add(docTermCount);
					docTermCount = 0;

					while (iterator2.hasNext()) {
						Map.Entry mentry2 = (Map.Entry) iterator2.next();
						// ("key is: "+ mentry.getKey() + " & Value is: " + mentry.getValue());
						// ("Value2 is: " + mentry2.getValue());
						// );
						String term = mentry2.getValue().toString();
						// //"This is going to be put in doc3: "+ (documentFound-1));
						postingsFileListAllWords.get(documentFound - 1).add(term);
						// if ( !((mentry.getValue()).equals(" ")) )
						// docTermCount++;
					}

					allValues.clear();					
					documentFound = Integer.parseInt(arr[1]);

					// "MEANWHILE THESE ARE ALL VALUES" + postingsFileListAllWords);

				}							
			}
			
			
			
			//System.out.println("Looking at final doc!");
			//Final loop for last sets
			Set set = hmap.entrySet();
			Iterator iterator = set.iterator();

			Set setA = allValues.entrySet();
			Iterator iteratorA = setA.iterator();
			postingsFileList.put(documentFound - 1, new ArrayList<String>());
			postingsFileListAllWords.put(documentFound - 1, new ArrayList<String>());
			while (iterator.hasNext()) {
				Map.Entry mentry = (Map.Entry) iterator.next();
				// ("key is: "+ mentry.getKey() + " & Value is: " + mentry.getValue());
				// ("Value is: " + mentry.getValue());
				// //);
				// if ( !((mentry.getValue()).equals(" ")) )
				String term2 = mentry.getValue().toString();
				postingsFileList.get(documentFound - 1).add(term2);

				docTermCount++;
			}
			//System.out.println("Done looking at final doc!");
			
			
			//System.out.println("Sorting time!");
			while (iteratorA.hasNext()) {
				Map.Entry mentry2 = (Map.Entry) iteratorA.next();
				// ("key is: "+ mentry.getKey() + " & Value is: " + mentry.getValue());
				// ("Value2 is: " + mentry2.getValue());
				// //);
				String term = mentry2.getValue().toString();
				// //"This is going to be put in doc3: "+ (documentFound-1));
				postingsFileListAllWords.get(documentFound - 1).add(term);
				// if ( !((mentry.getValue()).equals(" ")) )
				// docTermCount++;
			}

			hmap2.putAll(hmap);
			hmap.clear();
			docTermCountList.add(docTermCount);
			docTermCount = 0;


			
		
			// END OF LOOKING AT ALL DOCS
			
			//System.out.println("Docs and titles: "+docAndTitles);
			
			//System.out.println("All terms" +allTerms);
			String[] sortedArray = allTerms.toArray(new String[0]);
			String[] sortedArrayUnique = uniqueTerms.toArray(new String[0]);
			//System.out.println(Arrays.toString(sortedArray));
			
			Arrays.sort(sortedArray);
			Arrays.sort(sortedArrayUnique);
			
			
			
			//Sortings 
			Set set3 = hmap2.entrySet();
			Iterator iterator3 = set3.iterator();

			// Sorting the map
			//System.out.println("Before sorting " +hmap2);			
			Map<Integer, String> map = sortByValues(hmap2);
			//System.out.println("after sorting " +map);
			// //"After Sorting:");
			Set set2 = map.entrySet();
			Iterator iterator2 = set2.iterator();
			int docCount = 1;
			while (iterator2.hasNext()) {
				Map.Entry me2 = (Map.Entry) iterator2.next();
				// (me2.getKey() + ": ");
				// //me2.getValue());
			}
			
			//System.out.println("Done sorting!");
		
			
			
			
			//System.out.println("Posting starts ");
			//"THIS IS START OF DICTIONARTY" 
			BufferedWriter bw = null;
			FileWriter fw = null;
			
			
			//System.out.println("Start making an array thats big as every doc total ");
			for (int z = 1; z < documentFound+1; z++)
			{
				termWeights.put(z, new ArrayList<Double>());
			}
			//System.out.println("Done making that large array Doc ");
			
			//System.out.println("Current Weights: "+termWeights);
			
			//System.out.println("All terms" +allTerms)
			//System.out.println(Arrays.toString(sortedArray));
			//System.out.println(Arrays.toString(sortedArrayUnique));
			//System.out.println(uniqueTerms);
			
			
			
			//	System.out.println("Posting starts ");
			// 	POSTING FILE STARTS 
			try {
				// Posting File
				//System.out.println("postingsFileListAllWords: "+postingsFileListAllWords); //Contains every word including Dups, seperated per doc
				//System.out.println("postingsFileList:         "+postingsFileList); 		   //Contains unique words, dups are " ",  seperated per doc
				//System.out.println("postingsFileListAllWords.size(): " +postingsFileListAllWords.size()); //Total # of docs 
				//System.out.println("Array size: "+sortedArrayUnique.length);

				fw = new FileWriter(POSTING);
				bw = new BufferedWriter(fw);
				String temp = " ";
				Double termFreq = 0.0;
				// //postingsFileListAllWords);
				List<String> finalTermList = new ArrayList<String>();
				
				
			
					// //postingsFileList.get(i).size());
					for (int j = 0; j < sortedArrayUnique.length; ++j)					 // go thru each word, CURRENT TERM
					{
						//System.out.println("Term is: " + sortedArrayUnique[j]);
						temp = sortedArrayUnique[j];					
						if ((stopWordsSetting && stopWords.contains(temp))) 
						{						
							continue;
						}

						if (!(finalTermList.contains(temp))) 
						{							
							
							
							
							//PART TO FIND DOCUMENT FREQ							
							int docCountIDF = 0;
							//Start here for dictionary 
							for (int totalWords = 0; totalWords < sortedArray.length; totalWords++) 		
							{										
								if (temp.compareTo(" ") == 0 || (stopWordsSetting && stopWords.contains(temp))) 
								{
									//EITHER BLANK OR STOPWORD 
									//System.out.println("fOUND STOP WORD");
									continue;
								}																			
								
									String temp2 = sortedArray[totalWords];
									//System.out.println("Compare: "+temp+ " with " +temp2);
									if (temp.compareTo(temp2) == 0) {
										// (temp2+" ");												
										docCountIDF++;
									}
							}
							//System.out.println("Total Number: " +docCountIDF);
							//System.out.println("documentFound: " +documentFound);
							//System.out.println("So its " + documentFound + " dividied by " +docCountIDF);
							//docCountIDF = 1;														
							double idf = (Math.log10(((double)documentFound/(double)docCountIDF)));
							//System.out.println("Calculated IDF: "+idf);
							if (idf < 0.0)
							{
								idf = 0.0;
							}
							//System.out.println("IDF is: " +idf);
							
							//System.out.println("Size of doc words: " + postingsFileListAllWords.size());
							for (int k = 0; k < postingsFileListAllWords.size(); k++) 		//Go thru each doc. Since only looking at 1 term, it does it once per doc
							{
								//System.out.println("Current Doc: " +(k+1));																						
									termFreq = 1 + (Math.log10(Collections.frequency(postingsFileListAllWords.get(k), temp)));			
									//System.out.println("Freq is: " +Collections.frequency(postingsFileListAllWords.get(k), temp));
									//System.out.println(termFreq + ": " + termFreq.isInfinite());
									if (termFreq.isInfinite() || termFreq <= 0)
									{
										termFreq = 0.0;
									}				
									//System.out.println("termFreq :" +termFreq); 
									//System.out.println("idf: " +idf);
									termWeights.get(k+1).add( (idf*termFreq) );																	
							}
							
							//System.out.println("");
							finalTermList.add(temp);							
						}	
						//System.out.println("Current Weights: "+termWeights);
						
						//FINALCOUNTER
						//System.out.println("Done looking at word: " +j);
						//System.out.println("");
					}
					
					//System.out.println("Current Weights: "+termWeights);
					
					
					
					while (true)
				    {
					
						
						
					System.out.println("Enter a query: ");
					
	            	Scanner scanner = new Scanner(System.in);
	            	String enterQuery = scanner.nextLine();
	            	
	            	
					List<Double> queryWeights = new ArrayList<Double>();
					
					// Query turn														
					enterQuery = enterQuery.toLowerCase();		
					enterQuery = enterQuery.replaceAll("[-&^%'*$+|{}?!\\/<\\>=.,;_:()\\[\\]\"]", "");
					//System.out.println("Query is: " + enterQuery);
					
					if (enterQuery.equals("exit"))
					{
						break;
					}
					String[] queryArray = enterQuery.split(" ");
					Arrays.sort(queryArray);
					
					//Find the query weights for each term in vocab
					for (int j = 0; j < sortedArrayUnique.length; ++j)					 // go thru each word, CURRENT TERM
					{
						//System.out.println("Term is: " + sortedArrayUnique[j]);
						temp = sortedArrayUnique[j];					
						if ((stopWordsSetting && stopWords.contains(temp))) 
						{						
							continue;
						}
					
						int docCountDF = 0;
						//Start here for dictionary 
						for (int totalWords = 0; totalWords < queryArray.length; totalWords++) 		
						{										
							if (temp.compareTo(" ") == 0 || (stopWordsSetting && stopWords.contains(temp))) 
							{
								//EITHER BLANK OR STOPWORD
								continue;
							}																			
							
								String temp2 = queryArray[totalWords];
								//System.out.println("Compare: "+temp+ " with " +temp2);
								if (temp.compareTo(temp2) == 0) {
									// (temp2+" ");												
									docCountDF++;
								}
						}
						Double queryWeight = 1 + (Math.log10(docCountDF));
						if (queryWeight.isInfinite())
						{
							queryWeight = 0.0;
						}		
						
						queryWeights.add(queryWeight);
					}										
					//System.out.println("Query WEights is: "+queryWeights);
					
					
					
					
					// Finding the norms for DOCS					
					for (int norms = 1; norms < documentFound+1; norms++)
					{
						double currentTotal = 0.0;
						
						for (int weightsPerDoc = 0; weightsPerDoc < termWeights.get(norms).size(); weightsPerDoc++)
						{						
							double square = Math.pow(termWeights.get(norms).get(weightsPerDoc), 2);
							
							//System.out.println("Current square: " + termWeights.get(norms).get(weightsPerDoc));
							currentTotal = currentTotal + square;
							//System.out.println("Current total: " + currentTotal);
						}
						//System.out.println("About to square root this: " +currentTotal);
						double root = Math.sqrt(currentTotal);
						docNorms.put(norms, root);
					}
					//System.out.println("All of the docs norms: "+docNorms);
						
					
					//Finding the norm for the query
					double currentTotal = 0.0;					
					for (int weightsPerDoc = 0; weightsPerDoc < queryWeights.size(); weightsPerDoc++)
					{														
						double square = Math.pow(queryWeights.get(weightsPerDoc), 2);
						currentTotal = currentTotal + square;													
					}
					double root = Math.sqrt(currentTotal);
					double queryNorm = root; 						
					//System.out.println("Query norm " + queryNorm);
					
					//Finding the cosine sim
					//System.out.println("Term Weights " + termWeights);
					HashMap<Integer, Double> cosineScore = new HashMap<Integer, Double>();
					for (int cosineSim = 1; cosineSim < documentFound+1; cosineSim++)
					{
						double total = 0.0;
						for (int docTerms = 0; docTerms < termWeights.get(cosineSim).size(); docTerms++)
						{
							
							
							double docTermWeight = termWeights.get(cosineSim).get(docTerms);
							double queryTermWeight = queryWeights.get(docTerms);
							//System.out.println("queryTermWeight " + queryTermWeight);
							//System.out.println("docTermWeight " + docTermWeight);
							
							total = total + (docTermWeight*queryTermWeight);
							
						}
						double cosineSimScore = 0.0;
						if (!(total == 0.0 || (docNorms.get(cosineSim) * queryNorm) == 0))
						{
							cosineSimScore = total / (docNorms.get(cosineSim) * queryNorm);
						}
						else
						{
							cosineSimScore = 0.0;
						}
						
						cosineScore.put(cosineSim, cosineSimScore);
					}
					cosineScore = sortByValues2(cosineScore);					
					//System.out.println("This is the cosineScores: " +cosineScore);
					
					//System.out.println("docAndTitles: "+ docAndTitles);
					int topK = 0;
					int noValue = 0;
					for (Integer name: cosineScore.keySet())
					{
						if (topK < 50)
						{	
							
				            String key =name.toString();
				            //String value = cosineScore.get(name).toString();  
				            if (!(cosineScore.get(name) <= 0))
				            {
				            	System.out.println("Doc: "+key);				            					            					            	
				            	
				            	StringBuilder builder = new StringBuilder();
				            	for (StringBuilder value : docAndTitles.get(name)) {
				            	    builder.append(value);
				            	}
				            	String text = builder.toString();				            	
				            	//System.out.println("Title:\n" +docAndTitles.get(name));
				            	System.out.println("Title: " +text);
				            	
				            	
				            	
				            	
				            	
				            	//System.out.println("Authors:\n" +docAndAuthors.get(name));
				            	
				            	if (docAndAuthors.get(name) == null || docAndAuthors.get(name).toString().equals(""))
				            	{
				            		System.out.println("Authors: N\\A\n");
				            	}
				            	else 
				            	{				            						            	
					            	StringBuilder builder2 = new StringBuilder();
					            	for (StringBuilder value : docAndAuthors.get(name)) {
					            	    builder2.append(value);
					            	}
					            	String text2 = builder2.toString();				            	
					            	
					            	System.out.println("Authors found: " +text2);
				            	}	
				            	
				            	
				            	/* ABSTRACT 
				            	if (docAndAbstract.get(name) == null)
				            	{
				            		System.out.println("Abstract: N\\A\n");
				            	}
				            	else 
				            	{				            						            	
					            	StringBuilder builder2 = new StringBuilder();
					            	for (StringBuilder value : docAndAbstract.get(name)) {
					            	    builder2.append(value);
					            	}
					            	String text2 = builder2.toString();				            	
					            	
					            	System.out.println("Abstract: " +text2);
				            	}	
				            	*/
				            	
				            	System.out.println("");
				            }
				            else {
				            	noValue++;
				            }
				            topK++;
				            
						}			
						
						
						if (noValue == documentFound)
						{
							System.out.println("No documents contain query!");
						}
					} 
					topK=0;
					noValue = 0;
				    }
				
			} finally {
				try {
					if (bw != null)
						bw.close();

					if (fw != null)
						fw.close();

				} catch (IOException ex) {

					ex.printStackTrace();

				}

			}
			

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
			
			
			
			
			

			
									
			int itemCount = uniqueTerms.size();
			//System.out.println(allValues);
			System.out.println("Total Terms BEFORE STEMING: " +itemCount);
			System.out.println("Total Documents " + documentFound);
						
		    
			  
			    
			//END OF MAIN
		}
		        

	private static HashMap sortByValues2(HashMap map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

		

	

	private static HashMap sortByValues(HashMap map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
}
