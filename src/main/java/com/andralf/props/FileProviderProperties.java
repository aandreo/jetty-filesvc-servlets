package com.andralf.props;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class FileProviderProperties {

	private static final String PROPERTIES_FILE_FILENAME = "fileprovider.properties";
	private static final String MAX_FILE_SIZE_PROPERTY_KEY = "max.file.size";
	private static final String INCOMING_DIR_PROPERTY_KEY = "incoming.dir";
	private static final String DEFAULT_INCOMING_DIRECTORY = "incoming";
	public static final int DEFAULT_MAX_FILE_SIZE_IN_MB = 30;

	private volatile static FileProviderProperties _instance;
	private Properties props = new Properties();

	private FileProviderProperties(){
		try{
			// file expected to be found on current working directory.
			InputStream input = new FileInputStream(PROPERTIES_FILE_FILENAME);
			props.load(input);
		}
		catch(Exception re){
			re.printStackTrace();
		}
	}

	public static FileProviderProperties getInstance() {
		if (_instance == null) {
			synchronized (FileProviderProperties.class) {
				if (_instance == null) {
					_instance = new FileProviderProperties();
				}
			}
		}
		return _instance;
	}

	public String getIncomingDirectory(){
		String incomingDir = props.getProperty(INCOMING_DIR_PROPERTY_KEY);
		return !Strings.isNullOrEmpty(incomingDir)
				? incomingDir
				: new File(System.getProperty("user.dir"), DEFAULT_INCOMING_DIRECTORY).getAbsolutePath();
	}

	public int getMaxFileSize(){
		int maxFileSize = DEFAULT_MAX_FILE_SIZE_IN_MB;
		String maxFileSizeStr = props.getProperty(MAX_FILE_SIZE_PROPERTY_KEY);
		try{
			maxFileSize = Integer.parseInt(maxFileSizeStr);
		}
		catch(NumberFormatException nfe){
			nfe.printStackTrace();
		}

		return maxFileSize;
	}

}
