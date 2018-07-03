//Stuff for Hashmap
//Stuff for sorting hashmaps
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.lang.Double;
public class eval {

	private static final String STOPWORDS = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\stopwords.txt";
	private static final String FILENAME = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\cacm.all";
	private static final String DICTIONARY = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\dictionary.txt";
	//cacm.all
	
	private static final String FILENAME2 = "C:\\Users\\abel\\eclipse-workspace\\Lab2 - Web Search\\query.text";
	private static final String POSTING = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\posting.txt";
	
	private static final String RELEVANTUSERQUERRY = "C:\\Users\\abel\\eclipse-workspace\\Lab2 - Web Search\\qrels.text";
	
	
	public static boolean stopWordsSetting = true;
	public static boolean allowStemming = false;
	
	static double totalDocumentsFinal = 3204.0;
	static double dampingFactor = 0.85;
	public static boolean writeDataToFile = true;
	
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

	
	
	    				

	public static void main(String[] args) throws Exception 
	{
		int documentFound = 0;
		BufferedReader br = null;
		FileReader fr = null;
		List<String> stopWords = new ArrayList<String>();
		stopWords = stopWordsCreation();
		
		
		Map<Integer, List<Integer>> pageRankMatrix = new HashMap<>();              
		
		HashMap<Integer, Double> docNorms = new HashMap<Integer, Double>();
		Map<Integer, List<StringBuilder>> docAndTitles = new HashMap<>();
		Map<Integer, List<StringBuilder>> docAndAbstract = new HashMap<>();
		Map<Integer, List<StringBuilder>> docAndAuthors = new HashMap<>();
		Map<Integer, List<Double>> termWeights = new HashMap<>();                                                            
		String[] sortedArray = null ;
		String[] sortedArrayUnique = null;
		try {

			
			

			
			HashMap<Integer, String> hmap = new HashMap<Integer, String>();	//Used in tittle, all terms are unique, and any dups become " "
		
			
			List<String> uniqueTerms = new ArrayList<String>();
			List<String> allTerms = new ArrayList<String>();
					
			
			HashMap<Integer, String> hmap2 = new HashMap<Integer, String>();
			HashMap<Integer, String> allValues = new HashMap<Integer, String>();
			
			
			
			
			
			Map<Integer, List<String>> postingsFileListAllWords = new HashMap<>();		
			Map<Integer, List<String>> postingsFileList = new HashMap<>();
			
			
			
			
			
			
			List<Integer> docTermCountList = new ArrayList<Integer>();
			
			
			String sCurrentLine;

			int documentCount = 0;
			
			int articleNew = 0;
			int docTermCount = 0;
			
			
			boolean abstractReached = false;
			boolean insideX = false;
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			// Continues to get 1 line from document until it reaches the end of EVERY doc
			while ((sCurrentLine = br.readLine()) != null) 
			{
				// sCurrentLine now contains the 1 line from the document

				// Take line and split each word and place them into array
				String[] arr = sCurrentLine.split(" ");

				String[] arr2 = null;
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
					
					
					
				
					if (arr[0].equals(".X") ) 
					{
						
						
						pageRankMatrix.put(documentFound, new ArrayList<Integer>());
						
						boolean iFound = false;		
						
						
						//while ( !((sCurrentLine = br.readLine()).matches( (".B|.A|.N|.X|.K|.C|.I|.T")) ))
						while ( ((sCurrentLine = br.readLine())!= null ))
						{				
							/* In this section, there are 2 lists being made. One list
							 * is for all the unique words in every title in the document (hmap).
							 * Hmap contains all unique words, and anytime a duplicate word is 
							 * found, it is replaced with an empty space in the map.
							 * All Values 
							 * 
							 */
							
							
							
							
							if (sCurrentLine.matches( (".B|.A|.N|.X|.K|.C|.I|.T")))
							{
									break;
							}
							
							
							arr2 = sCurrentLine.split("\\s+");
							
							if (arr2[0].equals(".I")) 
							{						
								
								insideX = true;
								if (articleNew == 0) 
								{
									articleNew = 1;
									documentFound = Integer.parseInt(arr2[1]);
								} 
								else if (articleNew == 1) 
								{
									articleNew = 0;
									documentFound = Integer.parseInt(arr2[1]);
								}			
								//System.out.println(documentFound);
								//count++;
								iFound = true;
								break;
							}
							
							
								
								if (Integer.parseInt(arr2[1]) == 5)
								{
									
									if (!(pageRankMatrix.get(documentFound).contains( Integer.parseInt(arr2[0])) ))
									{
										pageRankMatrix.get(documentFound).add( Integer.parseInt(arr2[0]) );
									}
								
									
									//pageRankMatrix.get(documentFound).add( Integer.parseInt(arr2[0]) );
									
								//	System.out.println("Current line " + sCurrentLine );
									//System.out.println("The array is " + Arrays.toString(arr2) );								
									//System.out.println("The array has " + arr2[0]);
								}	
								
							
						
							//Map<Integer, List<Double>> pageRankMatrix = new HashMap<>();													
							            																				
						}
						
						if (iFound)
						{
							iFound = false;
							break;
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
				
				
				//Map<Integer, List<Double>> pageRankMatrix = new HashMap<>();
				
				
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
					if (insideX)
					{
						documentFound = Integer.parseInt(arr2[1]);
						insideX = false;
					}
					else
					{
						documentFound = Integer.parseInt(arr[1]);
					}
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
			sortedArray = allTerms.toArray(new String[0]);
			sortedArrayUnique = uniqueTerms.toArray(new String[0]);
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
			
			
			
			System.out.println("Please wait for file indexing to complete ...");
			// 	POSTING FILE STARTS 
			try {
				// Posting File
				//System.out.println("postingsFileListAllWords: "+postingsFileListAllWords); //Contains every word including Dups, seperated per doc
				//System.out.println("postingsFileList:         "+postingsFileList); 		   //Contains unique words, dups are " ",  seperated per doc
				//System.out.println("postingsFileListAllWords.size(): " +postingsFileListAllWords.size()); //Total # of docs 
				//System.out.println("Array size: "+sortedArrayUnique.length);

				fw = new FileWriter("C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\weights.txt");
				bw = new BufferedWriter(fw);
				String temp = " ";
				Double termFreq = 0.0;
				// //postingsFileListAllWords);
				List<String> finalTermList = new ArrayList<String>();
				
					
				//writeDataToFile
				
				
				if (writeDataToFile)
				{
					// //postingsFileList.get(i).size());
					for (int j = 0; j < sortedArrayUnique.length; ++j)					 // go thru each word, CURRENT TERM
					{
						//System.out.println("Term is: " + sortedArrayUnique[j]);
						temp = sortedArrayUnique[j];					
						if ((stopWordsSetting && stopWords.contains(temp))) 
						{						
							continue;
						}

						double actualWeightForFile = 0.0;
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
									actualWeightForFile = idf*termFreq;
									termWeights.get(k+1).add( (idf*termFreq) );																	
							}
							
							//System.out.println("");
							finalTermList.add(temp);							
						}
					
						
						//System.out.println("Current Weights: "+termWeights);
						
						//COUNTER
						//System.out.println("Done looking at word: " +j);
						//System.out.println("");
					}
					System.out.println("File indexing complete !! ");
					
				}
					
					        					            					            						            						            
	            
	            	
					
					
					if (writeDataToFile)
					{
						for (int i = 1; i < termWeights.size()+1; i++)
						{
							
							StringBuilder builder = new StringBuilder();							
			            	for (Double value : termWeights.get(i)) {
			            	    builder.append(value);
			            	    builder.append(" ");
			            	}							
			            	String text2 = builder.toString();		
							
			            	
			            	
							//String fine = termWeights.get(i).toString();
							//System.out.println("text2: " +text2);
							bw.write(text2);
							bw.append(System.lineSeparator());			
						}
						
									
					}
					
					
					//System.out.println("Current Weights: "+termWeights);
					
				
					
			
					
						
						
					//System.out.println("Enter a query: ");
					
	            	//Scanner scanner = new Scanner(System.in);
	            	
					
					
					// Finding the norms for DOCS			
					
					if (writeDataToFile)
					{
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

					}
					
			    	

					if (writeDataToFile)
					{
						BufferedWriter bw2 = null;
						FileWriter fw2 = null;
						try 
						{
							fw2 = new FileWriter("C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\norms.txt");
							bw2 = new BufferedWriter(fw2);
							for (int i = 1; i < documentFound+1; i++)
							{
								
								StringBuilder builder = new StringBuilder();										            	
				            	    builder.append(docNorms.get(i));
				            	    builder.append(" ");
				            								
				            	String text2 = builder.toString();		
								
				            	
				            	
								//String fine = termWeights.get(i).toString();
				            	//System.out.println("text2: " +text2);
								bw2.write(text2);
								bw2.append(System.lineSeparator());			
							}

						

						} finally {
							try {
								if (bw2 != null)
									bw2.close();
	
								if (fw2 != null)
									fw2.close();
	
							} catch (IOException ex) {
	
								ex.printStackTrace();
	
							}

						}
						
									
					}
					
					
					
					//System.out.println("docAndTitles: "+ docAndTitles);
				
				    
				
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
			

			
		// HERE IS WHERE EVERYTHING ENDS FOR PART 1, SO IF YOU NEED TO SAVE SOMETHING
		// DO IT HERE
			
			
		System.out.println("Total Documents " + documentFound);
			
		System.out.println("Please wait for final AP and MAP calculations...");
			
			
			
			
			
			
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
			
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if (!writeDataToFile)
		{
			/* Hopefully, this will read in the data that was saved so that I dont have to keep re-making the index!!"
			 * 
			 */
			
			//HashMap<Integer, Double> docNorms2 = new HashMap<Integer, Double>();		    
			//Map <Integer, List<Double>> queryAndRelDocs2 = new HashMap<>();
			
			
	        //String userRelQuerry = "C:\\Users\\abel\\eclipse-workspace\\Lab2 - Web Search\\qrelsTest.txt";
	        //String readInN = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\norms.txt";
	        String readInNorms = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\norms.txt";
	        String readInWeights = "C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\weights.txt";
	        String line = null;
	        
	        
	        FileReader fileReader1 = new FileReader(readInNorms);              
	        BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
	        try {
	            
	           
	            int count =0;
	            int innerCount = 0;
	            
	            while((line = bufferedReader1.readLine()) != null) 
	            {
	            	 
	            	 count++;
	            	 double normValue = Double.parseDouble(line);
	            	 docNorms.put(count, normValue);
	            	 //System.out.println("Line is: "+count);
	            }
	           //System.out.println("docNorms2: " +docNorms);
	        } finally {
				try 
				{
					if (bufferedReader1 != null)
						bufferedReader1.close();
	
					if (fileReader1 != null)
						fileReader1.close();
	
				} catch (IOException ex) {
	
					ex.printStackTrace();
	
				}
	
			}
	        
	        
	        FileReader fileReader2 = new FileReader(readInWeights);              
	        BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
	        try {
	            
	            
	            int count =0;
	            int innerCount = 0;
	            
	            while((line = bufferedReader2.readLine()) != null) 
	            {
	            	 String[] queryRelevantArray = line.split(" ");
	            	 count++;
	            	 termWeights.put(count, new ArrayList<Double>());
	            	 for (int i = 0; i < queryRelevantArray.length; i++)
	            	 {         
	            		 //System.out.println("Cuurent wait: " +queryRelevantArray[i]);
	            		 double weight = Double.parseDouble(queryRelevantArray[i]);
	            		 termWeights.get(count).add(weight);
	                                   
	                	 	
	                	 //System.out.println("Line is: "+weight);
	            	 }
	            	 
	            }
	            //System.out.println("docWeights: " +termWeights);
	        }
	        finally {
				try 
				{
					if (bufferedReader2 != null)
						bufferedReader2.close();
	
					if (fileReader2 != null)
						fileReader2.close();
	
				} catch (IOException ex) {
	
					ex.printStackTrace();
	
				}
	
			}
	        
		
		
		
		}
		
		
		
		
		
		// PART 2
		
		/*
		HashMap<Integer, Double> docNorms = new HashMap<Integer, Double>();
		Map<Integer, List<StringBuilder>> docAndTitles = new HashMap<>();
		Map<Integer, List<StringBuilder>> docAndAbstract = new HashMap<>();
		Map<Integer, List<StringBuilder>> docAndAuthors = new HashMap<>();
		Map<Integer, List<Double>> termWeights = new HashMap<>();
		 */
	
		int documentCountFinal = documentFound; //Carrys the total number of document from part 1!!
		
		/*
		System.out.println("BEFORE ANYTHING CRAZY HAPPENS");
		System.out.println(docNorms);
		System.out.println("");
		System.out.println(docAndTitles);
		System.out.println("");
		System.out.println(docAndAbstract);
		System.out.println("");
		System.out.println(docAndAuthors);
		System.out.println("");
		System.out.println(termWeights);
		System.out.println("");
		System.out.println(Arrays.toString(sortedArray));
		System.out.println("");
		System.out.println(Arrays.toString(sortedArrayUnique));
		System.out.println("");
		System.out.println("Doc count: "+documentCountFinal);
	*/
	
		HashMap<Integer, String> hmap = new HashMap<Integer, String>();	//Used in tittle, all terms are unique, and any dups become " "
	
		
		List<String> uniqueTerms = new ArrayList<String>();
		List<String> allTerms = new ArrayList<String>();
				
		
		HashMap<Integer, String> hmap2 = new HashMap<Integer, String>();
		HashMap<Integer, String> allValues = new HashMap<Integer, String>();
		//HashMap<Integer, Double> docNorms = new HashMap<Integer, Double>();
		
		
		
		
		Map<Integer, List<String>> postingsFileListAllWords = new HashMap<>();		
		Map<Integer, List<String>> postingsFileList = new HashMap<>();
		
		//Map<Integer, List<StringBuilder>> docAndTitles = new HashMap<>();
	
		//Map<Integer, List<StringBuilder>> docAndAuthors = new HashMap<>();
		
		
		//Map<Integer, List<Double>> termWeights = new HashMap<>();
		
		List<Integer> docTermCountList = new ArrayList<Integer>();
		
		

		int documentCount = 0;
		documentFound = 0;		
		int docTermCount = 0;
		
		
		
		
		
		Map<Integer, List<String>> queryIDandQuery = new HashMap<>();
		int articleNew = 0;
		br = null;
		fr = null;
		String sCurrentLine = null;
		
		
		
		
		
		
		
		

        
		
		Map <Integer, List<Integer>> queryAndRelDocs = new HashMap<>();  // querryID,DocsRelated
		
		Map <Integer, List<Integer>> queryAndRelDocsIR = new HashMap<>();
       
        String line = null;

        try {
         
            FileReader fileReader = new FileReader(RELEVANTUSERQUERRY);              
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count =0;
            int innerCount = 0;
            
            while((line = bufferedReader.readLine()) != null) 
            {
            	
               

            	
                    //System.out.println("Dictionary File: " +line);
                    String[] queryRelevantArray = line.split(" ");
                    
                    
                    String temp2 = queryRelevantArray[0];
					//System.out.println("Query number: "+temp2);
					String temp3 = queryRelevantArray[1];
					//System.out.println("Doc: " +temp3);						
					int result = Integer.parseInt(temp2);
					int result2 = Integer.parseInt(temp3);
					count = result;
					if (count != innerCount)					
					{
						queryAndRelDocs.put(result, new ArrayList<Integer>());
						innerCount = count;
					}
					//System.out.println("result: " +result+ " and " +result2);
					queryAndRelDocs.get(result).add(result2);
            }                  
            //System.out.println("All the querrys and related docs: " +queryAndRelDocs);
            
            
            
            
            
            
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                		RELEVANTUSERQUERRY + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + RELEVANTUSERQUERRY + "'");                                  
        }


      
        

        
    
		
		
		
		
		
		
		
		
		
		
		
		
		try {
			fr = new FileReader(FILENAME2);
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
					
					
					
					
					
					if (arr[0].equals(".W") ) 
					{	
						//System.out.println("\n");
						//System.out.println("REACHED ABSTRACT and current line is: " +sCurrentLine);
						queryIDandQuery.put(documentFound, new ArrayList<String>());
						StringBuilder totalAbstract = new StringBuilder();
						
						while ( !(sCurrentLine = br.readLine()).matches(".T|.I|.A|.N|.X|.K|.C")  )
						{	
							String[] abstaract = sCurrentLine.split(" ");
							if (abstaract[0].equals(".B") )
							{										
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
						//docAndAbstract.get(documentFound).add(totalAbstract);
						
						//System.out.println("Query #: " +documentFound);
						String finalQueryString = totalAbstract.toString();
						//System.out.println("Before edited: " +finalQueryString);
						String[] misc = finalQueryString.split(" ");
						StringBuilder queryRemoval = new StringBuilder();
						for (String miscWords : misc) 
						{												
							miscWords = miscWords.toLowerCase();  
							miscWords = miscWords.replaceAll("[-&^%'*$+|{}?!\\/<\\>=.,;_:()\\[\\]\"\\d]", "");
							queryRemoval.append(miscWords);
							queryRemoval.append(" ");
						}
						finalQueryString = queryRemoval.toString();
						//System.out.println("After edited: " +finalQueryString);
						queryIDandQuery.get(documentFound).add(finalQueryString);
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



			//System.out.println("docAndAbstract HEEEEEEERE: " +queryIDandQuery);
			/*
			//System.out.println("");
			System.out.println("");
			System.out.println("After CRAZY HAPPENS");
			System.out.println(docNorms);
			System.out.println("");
			System.out.println(docAndTitles);
			System.out.println("");
			System.out.println(docAndAbstract);
			System.out.println("");
			System.out.println(docAndAuthors);
			System.out.println("");
			System.out.println(termWeights);
			System.out.println("");
			System.out.println(Arrays.toString(sortedArray));
			System.out.println("");
			System.out.println(Arrays.toString(sortedArrayUnique));
			System.out.println("");
			System.out.println("Doc count: "+documentCountFinal);
			*/
			
			
			
			
			
			
		
			// END OF LOOKING AT ALL DOCS
			
			//System.out.println("Docs and titles: "+docAndTitles);
			
			//System.out.println("All terms" +allTerms);
			 sortedArray = allTerms.toArray(new String[0]);
			 //sortedArrayUnique = uniqueTerms.toArray(new String[0]);
			//System.out.println(Arrays.toString(sortedArray));
			
			Arrays.sort(sortedArray);
			//Arrays.sort(sortedArrayUnique);
			
			
			
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
			
		
			
		
			//System.out.println("docAndAbstract HEEEEEEERE: " +queryIDandQuery);   //Shows the query ID and the worded query itself for each query
			//System.out.println("queryIDandQuery.size(): " +queryIDandQuery.size());
			//System.out.println("queryAndRelDocs"  + queryAndRelDocs);
			
			double totalAP = 0.0;
			
			
			//"THIS IS START OF DICTIONARTY" 
			
			
						
					for (int u = 1; u < (queryIDandQuery.size()+1); u++)	//64 times because there are 64 qurries 
				    {															
						  List<Integer> values = queryAndRelDocs.get(u);  //single query from user selected depeding on value of U
						  
			            	if (values == null) {		
			            		//System.out.println("QUERY ID: " +u+ " DOES NOT EXSIST");
			            		//System.out.println("PROOF: " +queryAndRelDocs.get(u));
			            		   continue;
			            	}
			            	//System.out.println("values: " +values);   
						
						//queryIDandQuery.get(documentFound).add(finalQueryString);
						
						
						String temp = null;	
						
						//System.out.println("Enter a query: ");
						
		            	//Scanner scanner = new Scanner(System.in);
		            	//String foo = scanner.nextLine();
		            	
						
						
						
		            	String enterQuery = queryIDandQuery.get(u).toString();
		            	
		            	
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
						for (int j = 0; j < sortedArrayUnique.length; ++j)					 // go thru each word in the vocab, CURRENT TERM
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
						//System.out.println("WEights for ALL DOCS is: "+termWeights);
						
						
						
						
						
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
						HashMap<Integer, Double> cosineScore = new HashMap<Integer, Double>();  //Relevant Doc ID, Cosine score
						for (int cosineSim = 1; cosineSim < documentCountFinal+1; cosineSim++)
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
							//System.out.println("total: " +total);
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
						//System.out.println("");
						
						
						
						
						
						int topK = 0;
						int noValue = 0;
					
						List<Integer> relatedDocuments = new ArrayList<Integer>();
						
						for (Integer name: cosineScore.keySet())
						{
							
							
							if (topK < 800)
							{	
					            String key =name.toString();
					            //String value = cosineScore.get(name).toString();  
					            if (!(cosineScore.get(name) <= 0))
					            {
					            	relatedDocuments.add(Integer.parseInt(key));
					            	
				            					            					            						            						            
					            	
					            		
						            	//System.out.println("Doc: "+key);
					            /*		NOT NEEDED BECAUSE DONT NEED TITLE 
						            	StringBuilder builder = new StringBuilder();
						            	for (StringBuilder value : docAndTitles.get(name)) {
						            	    builder.append(value);
						            	}
						            	String text = builder.toString();
						            	
						       */
					            	//System.out.println("Title:\n" +docAndTitles.get(name));
					            	//System.out.println("Title: " +text);
					            	
					            	
					            	
					            	
					            	// AUTHORS
					            	//System.out.println("Authors:\n" +docAndAuthors.get(name));
					            	/*
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
					            	
					            	*/
					            	
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
					            	
//					            	System.out.println("");
					            }
					            else {
					            	noValue++;
					            }
					            topK++;
							}
					            
										
			            	
							
							if (noValue == documentCountFinal)
							{
								//System.out.println("No documents contain query!");
							}
						} 
					
						topK=0;
						noValue = 0;
						
						//System.out.println("");
						//System.out.println("U is "+u);
						//System.out.println("relatedDocuments size: " +relatedDocuments.size());
						//System.out.println("relatedDocuments: " +relatedDocuments);	
						//System.out.println("queryAndRelDocs: " +queryAndRelDocs); //SHOWS EVERY QUERY AND ITS RELATED DOCS FROM USER
						
						double precisionCounter = 0;		
						double precisionTotal = 0.0;
						double docFound = 0;
						boolean newPrecisionFound = false;
						
						
						for (int i=0; i<relatedDocuments.size();i++) 	//My IR got these many docs back
						{
							//System.out.println("queryAndRelDocs.get(u): "+ queryAndRelDocs.get(u));
							for (int j = 0; j <queryAndRelDocs.get(u).size(); j++)	//queryAndRelDocs.get(u) is the querry from user picked
							{
						
								
								//Important lesson learned: DONT assume 1 == 1. Make it so that 1.equals(1) 
								if (relatedDocuments.get(i).equals(queryAndRelDocs.get(u).get(j)))
								{
									docFound++;
									newPrecisionFound = true;
									//System.out.println("THERE WAS A MATCH!!!!");
									break;
								}
							}
							//System.out.println("");
							
							precisionCounter++;
							
							double precision = docFound/precisionCounter;
							//System.out.println(docFound+" / " +precisionCounter+ " = " +precision);
							if (newPrecisionFound)
							{
								precisionTotal = precisionTotal + precision;
								newPrecisionFound = false; 
							}
						}
						System.out.println("R-Precision for query " +u+ " is: " +docFound+ "/" +precisionCounter+ " = " + (docFound/precisionCounter));
						System.out.println("");
						double lol = precisionTotal/queryAndRelDocs.get(u).size();
						//System.out.println("Current AP: "+ lol);
						totalAP = totalAP + lol;
						//System.out.println("totalAP: "+totalAP);
						//topK=0;
						//noValue = 0;
					
				    }
					System.out.println("Final calculations complete");
					//System.out.println("(queryIDandQuery.size()+1): " +(queryIDandQuery.size()));
					System.out.println("totalAP: "+totalAP);
					System.out.println("MAP: = " +totalAP+ "/" + (queryIDandQuery.size()) + " = " +(totalAP/(queryIDandQuery.size())));
					
					
					
								

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
						
		    
			  
			    
			
			
			
			
	
			Map<Integer, List<Double>> pageRankMatrixCompleted = new HashMap<>();
			
			
			
			for (int i = 0; i < 3204; i++)
			{	
				pageRankMatrixCompleted.put(i+1, new ArrayList<Double>());
				for (int j = 0; j < 3204; j++)
				{
					if (pageRankMatrix.get(i+1).contains(j+1))
					{
						pageRankMatrixCompleted.get(i+1).add(1.0);
					}
					else
					{
						pageRankMatrixCompleted.get(i+1).add(0.0);
					}
				}
				double occurrences = Collections.frequency(pageRankMatrixCompleted.get(i+1), 1.0);
				for (int j = 0; j < 3204; j++)
				{			
					if (occurrences == 0.0)
					{
						pageRankMatrixCompleted.get(i+1).set(j, (       ((1.0/totalDocumentsFinal)*(1-dampingFactor))+(dampingFactor/totalDocumentsFinal)    ));
						
						
					}
					
					else if (pageRankMatrixCompleted.get(i+1).get(j) == 1)
					{
						
						
						pageRankMatrixCompleted.get(i+1).set(j, (          (  ((pageRankMatrixCompleted.get(i+1).get(j))/occurrences)  )     *(1-dampingFactor)+(dampingFactor/totalDocumentsFinal)           ));
					}
					else if (pageRankMatrixCompleted.get(i+1).get(j) == 0)
					{
						pageRankMatrixCompleted.get(i+1).set(j, (          (  ((pageRankMatrixCompleted.get(i+1).get(j))/occurrences)  )     *(1-dampingFactor)+(dampingFactor/totalDocumentsFinal)           ));
					}
				}
		
				
			}
			
			
			List<Double> termPageRank = new ArrayList<Double>((pageRankMatrixCompleted.get(1)));
			
			for (int k = 0; k < 3204; k++)
			{
				List<Double> finalPageRank = new ArrayList<Double>(termPageRank);		
				double finalCount = 0.0;
				for (int i = 0; i < 3204; i++)
				{				
					for (int j = 0; j < 3204; j++)
					{
						double x = (pageRankMatrixCompleted.get(j+1).get(i));
						double y = finalPageRank.get(j);
						finalCount = finalCount + (x*y);
					}
					termPageRank.set((i), finalCount);
				}
				
				System.out.println("AT: " +k);
			}
			
			System.out.println("Final Page rank: " +termPageRank);
/*
			List<Double> finalPageRank = new ArrayList<Double>((pageRankMatrixCompleted.get(1)));		
			double finalCount = 0.0;
			for (int i = 0; i < 3204; i++)
			{				
				for (int j = 0; j < 3204; j++)
				{
					double x = (pageRankMatrixCompleted.get(j+1).get(i));
					double y = finalPageRank.get(j);
					finalCount = finalCount + (x*y);
				}
				
			}
*/
			
			//System.out.println(pageRankMatrixCompleted.get(1));
			//System.out.println(pageRankMatrixCompleted.get(3204));
			//System.out.println(pageRankMatrixCompleted.size());
			//System.out.println(pageRankMatrixCompleted.get(1).size());
			
			
			
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

