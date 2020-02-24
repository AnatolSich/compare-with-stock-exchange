package download;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import service.DateTimeService;


@RequiredArgsConstructor
public class CloudStorageClient {

    private static final Logger log = Logger.getLogger(CloudStorageClient.class);


    private final String bucketName;
    private final AmazonS3 s3client;

    public void uploadString(String key, String content) {
        if (!s3client.doesBucketExistV2(bucketName)) {
            s3client.createBucket(bucketName);
        }
        s3client.putObject(bucketName, key, content);
        log.info(key + " uploaded to AWS");
    }
}