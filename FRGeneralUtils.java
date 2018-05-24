package org.ovgu.de.fiction.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Suhita
 */

/**
 * The class is a utility class.
 */
public class FRGeneralUtils {

	/**
	 * @param s
	 * @return The method checks whether the text is in Uppercase
	 */
	public static boolean isUpperCase(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLowerCase(s.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * @param propName
	 * @return The method returns the property value associated wit the property name passed in the
	 *         method
	 * @throws IOException
	 */
	public static  String getPropertyVal(String propName) throws IOException {
		Properties prop = new Properties();

		//InputStream input = new FileInputStream(FRConstants.PROPERTIES_FILE_LOC + FRConstants.CONFIG_FILE);
		 InputStream input = FRFileOperationUtils.class.getClassLoader()
	                .getResourceAsStream(FRConstants.CONFIG_FILE);

		prop.load(input);
		return (prop.getProperty(propName));
	}
	
	/**
	 * @param propName
	 * @return The method returns the property value associated wit the property name passed in the
	 *         method
	 * @throws IOException
	 */
	public static  String getPropertyValTune2(String propName) throws IOException {
		Properties prop = new Properties();

		//InputStream input = new FileInputStream(FRConstants.PROPERTIES_FILE_LOC + FRConstants.CONFIG_FILE);
		 InputStream input = FRFileOperationUtils.class.getClassLoader()
	                .getResourceAsStream(FRConstants.CONFIG_FILE_TUNE2);

		prop.load(input);
		return (prop.getProperty(propName));
	}

	public static List<String> performStopwordRemoval(List<String> stopwords, List<String> lemmas) {
		List<String> tokenFinal = new ArrayList<>();
		if (stopwords != null && stopwords.size() > 0) {
			tokenFinal = lemmas.parallelStream().filter(x -> (!x.isEmpty() && !stopwords.contains(x))).collect(Collectors.toList());
		}
		return tokenFinal;
	}

	

	
	public static Set<String> getPrepositionList() {

		Set<String> prepList = new HashSet<>();
		prepList.add("in");
		prepList.add("at");
		prepList.add("on");
		prepList.add("beside");
		prepList.add("near");
		prepList.add("towards");
		return prepList;
	}
	
	public static int countSyllables(String word)
    {
        char[] vowels = { 'a', 'e', 'i', 'o', 'u', 'y' };
        //String currentWord = word;
        int numVowels = 0;
        boolean lastWasVowel = false;
        char[] wc = word.toCharArray();
        for(int j=0;j<wc.length;j++)
        {
            boolean foundVowel = false;
            for (int v=0;v<vowels.length;v++)
            {
                //don't count diphthongs
                if (vowels[v]==wc[j] && lastWasVowel==true)
                {
                    foundVowel = true;
                    lastWasVowel = true;
                    break;
                }
                else if (vowels[v]==wc[j] && !lastWasVowel)
                {
                    numVowels++;
                    foundVowel = true;
                    lastWasVowel = true;
                    break;
                }
            }

            //if full cycle and no vowel found, set lastWasVowel to false;
            if (!foundVowel)
                lastWasVowel = false;
        }
        //remove es, it's _usually? silent
        if (word.length() > 2 && 
            		word.substring(word.length() - 2).equals("es"))
            numVowels--;
        // remove silent e
        else if (word.length() > 1 &&
            word.substring(word.length() - 1).equals("e"))
            numVowels--;

        return numVowels;
    }

}
