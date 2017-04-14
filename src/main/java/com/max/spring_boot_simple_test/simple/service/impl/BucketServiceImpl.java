
/**
 * File Name:BucketServiceImpl.java
 * Description: the operation on Bucket
 * 
 * @author clayclayclay
 * @date Apr 13, 2017 3:32:22 PM
 * @version V1.0
 */

package com.max.spring_boot_simple_test.simple.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.max.spring_boot_simple_test.simple.service.IBucketService;
import com.max.spring_boot_simple_test.simple.util.AmazonS3ConnectionUtil;

/**
 * ClassName: BucketService Impl Description: the operation on Bucket
 * 
 * @author clayclayclay
 * @date Apr 13, 2017 3:32:22 PM
 */

@Service
@Lazy
public class BucketServiceImpl implements IBucketService
{

    private static final Logger logger = Logger.getLogger(ObjectServiceImpl.class);

    @Autowired
    private AmazonS3ConnectionUtil amazonS3ConnectionUtil;

    private static AmazonS3 s3;

    @PostConstruct
    public void initAmazonS3()
    {
        s3 = amazonS3ConnectionUtil.getAmazonS3Client();
    }

    /**
     * Title: createBucket Description: create a bucket
     * 
     * @return boolean
     * @param bucketName
     *            to be created bucket's name
     * @return success or fail
     */
    public boolean createBucket(String bucketName)
    {
        try
        {
            if (!s3.doesBucketExist(bucketName))
            {
                s3.createBucket(bucketName);
                return true;
            }
            else
            {
                logger.error("the keyName already exist");
                return false;
            }
        }
        catch (AmazonServiceException ase)
        {
            logger.error(ase.getMessage());
            return false;
        }
        catch (AmazonClientException ace)
        {
            logger.error(ace.getMessage());
            return false;
        }
    }

    /**
     * Title: cleanBucket Description: clean the specified bucket
     * 
     * @return boolean
     * @param bucketName
     *            the specified bucket
     * @return success or fail
     */
    public boolean cleanBucket(String bucketName)
    {
        return false;
    }

    /**
     * Title: deleteBucket Description: delete the specified bucket
     * 
     * @return boolean
     * @param bucketName
     *            the specified bucket
     * @return success or fail
     */
    public boolean deleteBucket(String bucketName)
    {
        try
        {
            ObjectListing objectListing = s3.listObjects(bucketName);
            while (true)
            {
                for (Iterator<?> iterator = objectListing.getObjectSummaries().iterator(); iterator.hasNext();)
                {
                    S3ObjectSummary objectSummary = (S3ObjectSummary) iterator.next();
                    s3.deleteObject(bucketName, objectSummary.getKey());
                }

                if (objectListing.isTruncated())
                {
                    objectListing = s3.listNextBatchOfObjects(objectListing);
                }
                else
                {
                    break;
                }
            }
            ;
            VersionListing list = s3.listVersions(new ListVersionsRequest().withBucketName(bucketName));
            for (Iterator<?> iterator = list.getVersionSummaries().iterator(); iterator.hasNext();)
            {
                S3VersionSummary s = (S3VersionSummary) iterator.next();
                s3.deleteVersion(bucketName, s.getKey(), s.getVersionId());
            }
            s3.deleteBucket(bucketName);
        }
        catch (AmazonServiceException ase)
        {
            logger.error(ase.getMessage());
            return false;
        }
        catch (AmazonClientException ace)
        {
            logger.error(ace.getMessage());
            return false;
        }

        return true;

    }

    /**
     * Title: getBuckets Description: list all buckets
     * 
     * @return List<String>
     * @return the all bucket names
     */
    public List<String> getBuckets()
    {
        List<String> BucketsNameList = new ArrayList<String>();
        try
        {
            List<Bucket> buckets = s3.listBuckets();
            for (Bucket b : buckets)
            {
                BucketsNameList.add(b.getName());
            }
        }
        catch (AmazonServiceException ase)
        {
            logger.error(ase.getMessage());
        }
        catch (AmazonClientException ace)
        {
            logger.error(ace.getMessage());
        }
        return BucketsNameList;
    }
}
