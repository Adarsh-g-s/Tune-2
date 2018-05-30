package org.ovgu.de.fiction.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Suhita
 */

/**
 * The class is a utility class.
 */
public class FRGeneralUtils {
	
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

}
