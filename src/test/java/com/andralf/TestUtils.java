package com.andralf;

import com.andralf.props.FileProviderProperties;

import java.io.File;

public class TestUtils {

	private TestUtils(){}

	public static File getTestFile() {
		return new File(FileProviderProperties.getInstance().getIncomingDirectory(), TestConstants.TEST_FILE_FILENAME);
	}
}
