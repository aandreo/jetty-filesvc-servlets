package com.andralf;

import com.andralf.props.FileProviderProperties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.MessageFormat;

public class FileUploadTest {

	public static Main main;

	@BeforeClass
	public static void one_time_setup() throws Exception{
		main = new Main();
		main.start(TestConstants.TEST_PORT_NUMBER, false);
	}

	@Before
	public void setup(){
		deleteTestFile();
	}

	@Test
	public void upload_test_file__then_http_response_code_200_must_be_returned_and_file_is_stored() throws Exception{
		URL url = FileUploadTest.class.getResource("/" + TestConstants.TEST_FILE_FILENAME);
		Part[] parts = {
				new FilePart(TestConstants.TEST_FILE_FILENAME,  new File(url.toURI()))
		};

		PostMethod filePost = new PostMethod(getUploadURL());
		filePost.setRequestEntity(
				new MultipartRequestEntity(parts, filePost.getParams()));
		HttpClient client = new HttpClient();
		int statusCode = client.executeMethod(filePost);
		filePost.releaseConnection();

		Assert.assertEquals(HttpStatus.SC_OK, statusCode);
		Assert.assertTrue(TestUtils.getTestFile().exists());
		Assert.assertEquals(FileUtils.checksumCRC32(new File(url.toURI())),
				FileUtils.checksumCRC32(TestUtils.getTestFile()));

	}


	@Test
	public void sent_http_request_with_no_file__then_http_response_code_500_must_be_returned_and_file_is_stored() throws Exception{

		File file = File.createTempFile("file_above_default_max_file_size_limit", ".tmp");
		file.deleteOnExit();
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(1024 * 1024 * (FileProviderProperties.DEFAULT_MAX_FILE_SIZE_IN_MB + 1));
		raf.close();
		System.out.println(FileUtils.sizeOf(file));
		Part[] parts = {
				new FilePart(TestConstants.TEST_FILE_FILENAME,  file)
		};

		PostMethod filePost = new PostMethod(getUploadURL());
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		HttpClient client = new HttpClient();
		int statusCode = client.executeMethod(filePost);
		filePost.releaseConnection();

		Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, statusCode);

	}

	private String getUploadURL(){
		return MessageFormat.format(TestConstants.UPLOAD_SERVLET_URL, TestConstants.TEST_PORT_NUMBER);
	}


	@AfterClass
	public static void one_time_teardown() throws Exception{
		deleteTestFile();
		main.stop();
	}

	private static void deleteTestFile() {
		File testFile = TestUtils.getTestFile();
		if (testFile.exists()) testFile.delete();
	}

}
