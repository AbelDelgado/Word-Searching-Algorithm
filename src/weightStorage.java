//Stuff for reading files
//Stuff for Hashmap
//Stuff for sorting hashmaps
import java.util.*;
import java.io.*;

//Math
import java.lang.*;

//writing to file
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class weightStorage
{
    public static void main(String[] args) throws IOException 
    {
        
    	String enterQuery = null;
               
	    	
				//{1=[0.47712125471966244, 0.0, 0.47712125471966244, 0.0, 0.0, 0.0, 0.47712125471966244, 0.47712125471966244, 0.47712125471966244, 0.47712125471966244, 0.0, 0.0, 0.0, 0.47712125471966244, 0.47712125471966244, 0.0, 0.47712125471966244, 0.22910001000567795, 0.22910001000567795, 0.0, 0.47712125471966244, 0.22910001000567795, 0.47712125471966244, 0.0], 2=[0.0, 0.47712125471966244, 0.0, 0.47712125471966244, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.47712125471966244, 0.0, 0.47712125471966244, 0.0, 0.0, 0.47712125471966244, 0.0, 0.0, 0.0, 0.17609125905568124, 0.0, 0.0, 0.0, 0.47712125471966244], 3=[0.0, 0.0, 0.0, 0.0, 0.47712125471966244, 0.47712125471966244, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.17609125905568124, 0.0, 0.0, 0.0, 0.0]}	
				
					
				System.out.println("Enter a query: ");
				
		    	Scanner scanner = new Scanner(System.in);
		    	enterQuery = scanner.nextLine();
		    
    	
		    	/*
		    	BufferedWriter bw = null;
				FileWriter fw = null;
		    	try {
					fw = new FileWriter("C:\\Users\\abel\\eclipse-workspace\\Lab1 - Web Search Files\\weights.txt");
					bw = new BufferedWriter(fw);
					// Dictionary File
					//String[] array = map.values().toArray(new String[hmap2.size()]);
				

						// (temp+" ");
						bw.write("{1=[0.47712125471966244, 0.0, 0.47712125471966244, 0.0, 0.0, 0.0, 0.47712125471966244, 0.47712125471966244, 0.47712125471966244, 0.47712125471966244, 0.0, 0.0, 0.0, 0.47712125471966244, 0.47712125471966244, 0.0, 0.47712125471966244, 0.22910001000567795, 0.22910001000567795, 0.0, 0.47712125471966244, 0.22910001000567795, 0.47712125471966244, 0.0], 2=[0.0, 0.47712125471966244, 0.0, 0.47712125471966244, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.47712125471966244, 0.0, 0.47712125471966244, 0.0, 0.0, 0.47712125471966244, 0.0, 0.0, 0.0, 0.17609125905568124, 0.0, 0.0, 0.0, 0.47712125471966244], 3=[0.0, 0.0, 0.0, 0.0, 0.47712125471966244, 0.47712125471966244, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.17609125905568124, 0.0, 0.0, 0.0, 0.0]}");						
						// //": " +docCount);						
						bw.append(System.lineSeparator());
						
				

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
				*/
		    	
		    HashMap<Integer, Double> docNorms2 = new HashMap<Integer, Double>();		    
    		Map <Integer, List<Double>> queryAndRelDocs2 = new HashMap<>();
    		
    		
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
                	 docNorms2.put(count, normValue);
                	// System.out.println("Line is: "+count);
                }
                //System.out.println("docNorms2: " +docNorms2);
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
                	 queryAndRelDocs2.put(count, new ArrayList<Double>());
                	 for (int i = 0; i < queryRelevantArray.length; i++)
                	 {         
                		 //System.out.println("Cuurent wait: " +queryRelevantArray[i]);
                		 double weight = Double.parseDouble(queryRelevantArray[i]);
                		 queryAndRelDocs2.get(count).add(weight);
                                       
	                	 	
	                	// System.out.println("Line is: "+weight);
                	 }
                	 
                }
                //System.out.println("docWeights: " +queryAndRelDocs2);
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
            
            
            
            /*
            try {
             
                FileReader fileReader = new FileReader(userRelQuerry);              
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
							queryAndRelDocs2.put(result, new ArrayList<Integer>());
							innerCount = count;
						}
						//System.out.println("result: " +result+ " and " +result2);
						queryAndRelDocs2.get(result).add(result2);
                }                  
                System.out.println("All the querrys and related docs: " +queryAndRelDocs2);
                System.out.println("All the querrys and related docs: " +(2*0.323));
                System.out.println("Size of a querry: "+queryAndRelDocs2.get(1).size());
                
              
                
                
                bufferedReader.close();         
            }
            catch(FileNotFoundException ex) {
                System.out.println(
                    "Unable to open file '" + 
                    userRelQuerry + "'");                
            }
            catch(IOException ex) {
                System.out.println(
                    "Error reading file '" 
                    + userRelQuerry + "'");                                  
            }

             */
          
           
					
				
			
			

            
        
    }

   
}

