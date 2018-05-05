package main.java;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.LengthCheckInputStream;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AwsMain {
    public static void main(String[] args){
        AWSCredentials credentials = new BasicAWSCredentials(args[0],args[1]);
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
        List<Bucket> list = s3client.listBuckets();
        Iterator<Bucket> itr = list.listIterator();
        while (itr.hasNext()){
            Bucket bucket = (Bucket)itr.next();
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucket.getName()).withMaxKeys(1000);
            ListObjectsV2Result result = s3client.listObjectsV2(listObjectsV2Request);
            System.out.println("Bucket Name : " + bucket.getName());
            System.out.println("Bucket Creation Date : " + bucket.getCreationDate());
            System.out.println("Number of files : " + result.getObjectSummaries().size() );
            S3ObjectSummary lastModifieds3ObjectSummary;
            Long totalFileSize = Long.parseLong("0");
            Date date= null;
            for(S3ObjectSummary s3ObjectSummary : result.getObjectSummaries()){
                System.out.println("File Key : " + s3ObjectSummary.getKey() + " | File Size : " + s3ObjectSummary.getSize()/1024 +" kb");
                if(date == null){
                    date = s3ObjectSummary.getLastModified();
                }
                if(date.before(s3ObjectSummary.getLastModified())){
                    date = s3ObjectSummary.getLastModified();
                }
                totalFileSize = totalFileSize + s3ObjectSummary.getSize();
            }
            System.out.println("Total file size : " + totalFileSize/1024 +"kb");
            System.out.println("last modified date : " + date);
            System.out.println("Bucket Name : " + bucket.getName() + " End here");
        }

    }
}
