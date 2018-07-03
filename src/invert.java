//Stuff for Hashmap
//Stuff for sorting hashmaps
import java.util.*;
import java.io.*;

public class invert {

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
	    public invert()
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
	    
	    
	    
	    				

	public static void main(String[] args) throws Exception {
		List<String> stopWords = new ArrayList<String>();
		stopWords = stopWordsCreation();


		
		HashMap<Integer, String> hmap = new HashMap<Integer, String>();	//Used in tittle, all terms are unique, and any dups become " "
	
		
		List<String> uniqueTerms = new ArrayList<String>();
		List<String> allTerms = new ArrayList<String>();
		
		HashMap<Integer, String> hmap2 = new HashMap<Integer, String>();
		HashMap<Integer, String> allValues = new HashMap<Integer, String>();

	
		Map<Integer, List<String>> postingsFileListAllWords = new HashMap<>();		
		Map<Integer, List<String>> postingsFileList = new HashMap<>();
		
		List<Integer> docTermCountList = new ArrayList<Integer>();
		
		BufferedReader br = null;
		FileReader fr = null;
		String sCurrentLine;

		int documentCount = 0;
		int documentFound = 0;
		int articleNew = 0;
		int docTermCount = 0;
		
		int count = 1;
		
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
						System.out.println(documentFound);
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
						
						while ( !(sCurrentLine = br.readLine()).matches(".B|.A|.N|.X") )
						{				
							/* In this section, there are 2 lists being made. One list
							 * is for all the unique words in every title in the document (hmap).
							 * Hmap contains all unique words, and anytime a duplicate word is 
							 * found, it is replaced with an empty space in the map.
							 * All Values 
							 * 
							 */
							
							String[] tittle = sCurrentLine.split(" ");
							if (tittle[0].equals(".W") )
							{		
								abstractReached = true;
								break;								
							}
							
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
									uniqueTerms.add(tittleWords);
									documentCount++;
								}							
							}
							
							
						}
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
						
						while ( !(sCurrentLine = br.readLine()).matches(".T|.I|.A|.N|.X")  )
						{	
							String[] abstaract = sCurrentLine.split(" ");
							if (abstaract[0].equals(".B") )
							{			
								abstractReached = false;
								break;								
							}
							
							
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
									uniqueTerms.add(miscWords);								
									documentCount++;
								}
							}						
						}
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
			
			
			System.out.println(documentFound);
			// END OF LOOKING AT ALL DOCS
			
			
			//System.out.println("All terms" +allTerms);
			String[] sortedArray = allTerms.toArray(new String[0]);
			//System.out.println(Arrays.toString(sortedArray));
			
			
			//Sortings 
			Set set3 = hmap2.entrySet();
			Iterator iterator3 = set3.iterator();

			// Sorting the map
			Map<Integer, String> map = sortByValues(hmap2);
			// //"After Sorting:");
			Set set2 = map.entrySet();
			Iterator iterator2 = set2.iterator();
			int docCount = 1;
			while (iterator2.hasNext()) {
				Map.Entry me2 = (Map.Entry) iterator2.next();
				// (me2.getKey() + ": ");
				// //me2.getValue());
			}
			
			System.out.println("Please wait for dictionary to complete...");
		
			
			//"THIS IS START OF DICTIONARTY" 
			BufferedWriter bw = null;
			FileWriter fw = null;
			
			try {
				fw = new FileWriter(DICTIONARY);
				bw = new BufferedWriter(fw);
				// Dictionary File
				//String[] array = map.values().toArray(new String[hmap2.size()]);
				Arrays.sort(sortedArray);
			
				for (int i = 0; i < sortedArray.length; i++) {

					String temp = sortedArray[i];

					if (temp.compareTo(" ") == 0 || (stopWordsSetting && stopWords.contains(temp))) {
						// //"EITHER BLANK OR STOPWORD");
						continue;
					}

					// (temp+" ");
					bw.write(temp + " ");
					for (int j = i + 1; j < sortedArray.length; j++) {
						String temp2 = sortedArray[j];
						if (temp.compareTo(temp2) == 0) {
							// (temp2+" ");
							i++;
							docCount++;
						}
					}
					// //": " +docCount);
					String docCountString = Integer.toString(docCount);
					bw.write(docCountString);
					bw.append(System.lineSeparator());
					docCount = 1;
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
			
			
			
			System.out.println("Dictionary complete !!!");
			// 	POSTING FILE STARTS 
			
			
			
			System.out.println("Please wait for posting to complete ...");
			try {
				// Posting File
				//System.out.println("postingsFileListAllWords: "+postingsFileListAllWords);
				//System.out.println("postingsFileList:         "+postingsFileList);
				//System.out.println("postingsFileListAllWords.size(): " +postingsFileListAllWords.size());
				// //postingsFileList);

				fw = new FileWriter(POSTING);
				bw = new BufferedWriter(fw);
				String temp = " ";
				double termFreq = 0.0;
				// //postingsFileListAllWords);
				List<String> finalTermList = new ArrayList<String>();
				for (int i = 0; i < postingsFileListAllWords.size(); ++i) // go thru each doc
				{
					// //postingsFileList.get(i).size());
					for (int j = 0; j < postingsFileListAllWords.get(i).size(); ++j) // go thru each word
					{
						temp = postingsFileListAllWords.get(i).get(j);

						// //"CURRENT HEAD TERM: " +temp );
						if ((stopWordsSetting && stopWords.contains(temp))) {
							// //"SKIIIPPED BECAUSE ITSA STOP WORD");
							continue;
						}

						if (!(finalTermList.contains(temp))) {
							bw.write(temp); // FIRST TERM WRITE

							bw.append(System.lineSeparator());
							for (int k = 0; k < postingsFileListAllWords.size(); k++) // go thru each doc
							{

								if (!(finalTermList.contains(temp))) // duplicate ??
								{
									// //finalTermList);
									// //"Looking in Doc" +k);postingsFileListAllWords
									// //temp+ " contains: "+ Collections.frequency(postingsFileListAllWords.get(k),
									// temp));
									termFreq = 1 + (Math
											.log10(Collections.frequency(postingsFileListAllWords.get(k), temp)));
									// //"TERM FREQ FOR DOC " +(k+1)+ " is " +termFreq);

									if (postingsFileList.get(k).contains(temp)) {
										// (temp+ " is found in : ");
										// //" " +(k+1)+ " ");

										String docCountString = Integer.toString(k + 1);
										bw.write("found in doc: " + docCountString); // SECOND TERM WRITE
										bw.append(System.lineSeparator());

										// //postingsFileListAllWords.get(i).size());
										Map<String, List<Integer>> indexes = new HashMap<>();
										// //postingsFileListAllWords.get(k));
										for (int c = 0; c < postingsFileListAllWords.get(k).size(); c++) { // size
																											// of
																											// current
																											// doc
											// //"Compare " + postingsFileListAllWords.get(i).get(j)+ " with " +
											// postingsFileListAllWords.get(k).get(c));
											if ((indexes.get(postingsFileListAllWords.get(k).get(c)) != null)) {

												// //postingsFileListAllWords.get(k).get(c));
												List<Integer> indexList = indexes
														.get(postingsFileListAllWords.get(k).get(c));
												indexList.add(c);
												String term = postingsFileListAllWords.get(k).get(c).toString();
												indexes.put(temp, indexList);
												// //"first indexlist" +indexList);
												// //"first" +indexes);
											}

											else if (postingsFileListAllWords.get(i).get(j)
													.equals(postingsFileListAllWords.get(k).get(c))) {
												List<Integer> indexList = new ArrayList<>();
												// //"second 1indexlist" +indexList);
												indexList.add(c);
												// //"second 2indexlist" +indexList);
												String term = postingsFileListAllWords.get(k).get(c).toString();
												indexes.put(temp, indexList);

												// //"second" +indexes);
											}

										}
										// //"END" +indexes);
										bw.write("Positions in doc: " + indexes);
										bw.append(System.lineSeparator());
										bw.write("Word Freq: " + termFreq);
										bw.append(System.lineSeparator());

									}
								}
								// //"Word Freq for doc " +(k+1)+" is " +docCounts);
								// //"DONE LOOKING AT ONE DOC!!!");
								// //);
								// docCounts = 0;
							}

							finalTermList.add(temp);
							// //);
						}
						bw.write("****");
						bw.append(System.lineSeparator());
						bw.append(System.lineSeparator());
					}

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
			System.out.println("Posting complete !!!");

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
						
			
		
			
			
			    
			    			// STEMMING IN MAIN STARTS
			    if (allowStemming)
			    {
		        char[] w = new char[501];
		        Stemmer s = new Stemmer();
		        BufferedWriter bw3 = null;
		        FileWriter fw3 = null;

		        try
		        {
		            FileInputStream in = new FileInputStream("C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\listOfWords.txt");
		            fw3 = new FileWriter(STEMMING);
		            bw3 = new BufferedWriter(fw3);   
		            
		            
		            
		            try
		            { while(true)

		                {  int ch = in.read();
		                    if (Character.isLetter((char) ch))
		                    {
		                        int j = 0;
		                        while(true)
		                        {  ch = Character.toLowerCase((char) ch);
		                            w[j] = (char) ch;
		                            if (j < 500) j++;
		                            ch = in.read();
		                            if (!Character.isLetter((char) ch))
		                            {
		                                /* to test add(char ch) */
		                                for (int c = 0; c < j; c++) s.add(w[c]);

		                                /* or, to test add(char[] w, int j) */
		                                /* s.add(w, j); */

		                                s.stem();
		                                {  String u;

		                                    /* and now, to test toString() : */
		                                    u = s.toString();

		                                    /* to test getResultBuffer(), getResultLength() : */
		                                    /* u = new String(s.getResultBuffer(), 0, s.getResultLength()); */
		                                    
		                                    
		                                    bw3.write(u + " ");
		                                    
		                                    //System.out.print(" "+u+" ");
		                                }
		                                break;
		                            }
		                        }
		                    }
		                    if (ch < 0) break;
		                   
		                    bw3.write((char)ch);
		                    
		                }
		            }
		            catch (IOException e)
		            {  System.out.println("error reading ");

		            }

		        }
		        catch (IOException e) {

		            e.printStackTrace();
		        }

		        
		        finally {
		            try {
		                if (bw3 != null)
		                    bw3.close();

		                if (fw3 != null)
		                    fw3.close();

		            } catch (IOException ex) {

		                ex.printStackTrace();

		            }

		        }
		        
		        
		        //ummm enter stemming files?
		        try 
		        {
					fr = new FileReader(STEMMING);
					br = new BufferedReader(fr);
					
					List<String> uniqueTerms2 = new ArrayList<String>();
					// Continues to get 1 line from document until it reaches the end of EVERY doc
					while ((sCurrentLine = br.readLine()) != null) 
					{
						
						String[] arr2 = sCurrentLine.split(" ");


						//Go through the entire array
						for (String ss : arr2) 
						{
							uniqueTerms2.add(ss);
						}
					}
					

			    		// STEMMING IN MAIN ENDS 
			    
		        
			    
					int itemCount2 = uniqueTerms2.size();
					//System.out.println(uniqueTerms2);
					System.out.println("Total Terms after STEMING: " +itemCount2);
		        }
		    	catch (IOException e) {
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
			    }
			    
			
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
