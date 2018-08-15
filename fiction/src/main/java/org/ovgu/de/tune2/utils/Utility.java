package org.ovgu.de.tune2.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Suhita 
 */

/**
 * This is utility class for file operations
 */
public class Utility {

	private static Logger LOG = Logger.getLogger(Utility.class);

	/**
	 * @return
	 * @throws IOException
	 *             The method fetches file names present in the folder configured in
	 *             the properties file
	 */
	public static List<Path> getFileNames(String folderName) throws IOException {
		try (Stream<Path> paths = Files.walk(Paths.get(folderName))) {
			List<Path> filePathList = paths.filter(Files::isRegularFile).collect(Collectors.toList());
			return filePathList;
		}
	}

	/**
	 * @param src
	 * @param des
	 * @return The methods copies files from folder location passed in the method
	 */
	public static boolean copyFile(Path src, Path des) {
		File source = new File(src.toString());
		File dest = new File(des.toString());
		try {
			FileUtils.copyFileToDirectory(source, dest);
			return true;
		} catch (IOException e) {
			LOG.error("Files could not be copied -" + e.getMessage());
		}
		return false;
	}

	/**
	 * @param path
	 * @return
	 */
	public static String readFile(String path) {
		File file = new File(path);
		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			return new String(bytes);
		} catch (IOException e) {
			LOG.error("Files could not be read -" + e.getMessage());
		}
		return "";
	}

	/**
	 * @param propName
	 * @return The method returns the property value associated wit the property
	 *         name passed in the method, for phase1
	 * @throws IOException
	 */
	public static String getPropertyValPhase1(String propName) throws IOException {
		Properties prop = new Properties();

		InputStream input = Utility.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE_PHASE1);

		prop.load(input);
		return (prop.getProperty(propName));
	}

	/**
	 * @param propName
	 * @return The method returns the property value associated wit the property
	 *         name passed in the method, for phase2
	 * @throws IOException
	 */
	public static String getPropertyValPhase2(String propName) throws IOException {
		Properties prop = new Properties();

		InputStream input = Utility.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE_PHASE2);

		prop.load(input);
		return (prop.getProperty(propName));
	}
}
