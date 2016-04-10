package com.andralf;

import com.andralf.props.FileProviderProperties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;

public class FileDownloadTest {

	public static Main main;
	private static final int TEST_PORT_NUMBER = 48080;

	@BeforeClass
	public static void one_time_setup() throws Exception{
		main = new Main();
		main.start(TEST_PORT_NUMBER, false);
	}

	@Before
	public void setup() throws Exception{
		URL url = FileUploadTest.class.getResource("/" + TestConstants.TEST_FILE_FILENAME);
		FileUtils.copyFileToDirectory(new File(url.toURI()),
				new File(FileProviderProperties.getInstance().getIncomingDirectory()));
	}


	@Test
	public void when_file_name_param_is_test_file__then_test_file_must_be_downloaded() throws Exception{
		String downloadUrl = MessageFormat.format(TestConstants.DOWNLOAD_SERVLET_URL,
				TestConstants.TEST_PORT_NUMBER, TestConstants.TEST_FILE_FILENAME);
		GetMethod httpGet = new GetMethod(downloadUrl);

		HttpClient httpClient = new HttpClient();
		int statusCode = httpClient.executeMethod(httpGet);

		Assert.assertEquals(HttpStatus.SC_OK, statusCode);

		File downloadedTestFile = File.createTempFile(TestConstants.TEST_FILE_FILENAME, "tmp");
		downloadedTestFile.deleteOnExit();

		InputStream in = new BufferedInputStream(httpGet.getResponseBodyAsStream());
		OutputStream out = new BufferedOutputStream(new FileOutputStream(downloadedTestFile));
		out.flush();
		IOUtils.copy(in,out);
		out.close();
		httpGet.releaseConnection();

		Assert.assertNotNull(downloadedTestFile);
		Assert.assertEquals(FileUtils.checksumCRC32(TestUtils.getTestFile()),
				FileUtils.checksumCRC32(downloadedTestFile));

	}

	@Test
	public void file_name_is_malformed__then_return_http_error_code_404() throws Exception{
		String downloadUrl = MessageFormat.format(TestConstants.DOWNLOAD_SERVLET_URL,
			TestConstants.TEST_PORT_NUMBER, "../in/" +TestConstants.TEST_FILE_FILENAME);
		GetMethod httpGet = new GetMethod(downloadUrl);

		HttpClient httpClient = new HttpClient();
		int statusCode = httpClient.executeMethod(httpGet);

		Assert.assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
	}

	@After
	public void teardown() throws Exception{
		new File(FileProviderProperties.getInstance().getIncomingDirectory(), TestConstants.TEST_FILE_FILENAME)
			.delete();
	}

	@AfterClass
	public static void one_time_teardown() throws Exception{
		main.stop();
	}
}
