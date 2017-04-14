
/**
 * File Name:ObjectServiceImpl.java
 * Description: the operation ob Object
 * 
 * @author clayclayclay
 * @date Apr 13, 2017 3:31:00 PM
 * @version V1.0
 */

package com.max.spring_boot_simple_test.simple.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.max.spring_boot_simple_test.simple.model.ObjectContent;
import com.max.spring_boot_simple_test.simple.service.IObjectService;
import com.max.spring_boot_simple_test.simple.util.AmazonS3ConnectionUtil;
import com.max.spring_boot_simple_test.simple.util.CommonUtil;

/**
 * ClassName: ObjectServiceImpl Description: the operation on Object
 * 
 * @author clayclayclay
 * @date Apr 13, 2017 3:31:00 PM
 */

@Service
@Lazy
public class ObjectServiceImpl implements IObjectService
{

    private static final Logger logger = Logger.getLogger(ObjectServiceImpl.class);

    @Autowired
    private AmazonS3ConnectionUtil amazonS3ConnectionUtil;

    private static AmazonS3 s3;

    private static String bucketName;

    @PostConstruct
    public void initAmazonS3()
    {
        s3 = amazonS3ConnectionUtil.getAmazonS3Client();
        bucketName = amazonS3ConnectionUtil.getBucketName();
    }

    /**
     * Title: putObject Description: upload an Object to AWS S3 server
     * 
     * @return BasicJson :
     * @param in
     *            Object's inputStream
     * @param fileName
     *            Object's name
     * @param id
     *            the operation user id
     * @return the return json
     */
    public String putObject(InputStream in, String fileName, String id)
    {
        File f;
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        try
        {
            logger.info("run in the putObject1 try block");
            ObjectMetadata ObjectMetadata = new ObjectMetadata();
            Map<String, String> userMetadata = new HashMap<String, String>();
            userMetadata.put("Name", fileName);
            f = new File(fileName);
            userMetadata.put("Content-Type", mimetypesFileTypeMap.getContentType(f));
            String keyName = CommonUtil.getKeyName(bucketName, id, fileName);
            userMetadata.put("key_name", keyName);
            userMetadata.put("user", id);
            ObjectMetadata.setUserMetadata(userMetadata);
            s3.putObject(bucketName, keyName, in, ObjectMetadata);
            return keyName;
        }
        catch (SdkClientException e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * Title: putObject Description: upload an Object to AWS S3 server, allowing to put multiple Objects
     * 
     * @return List<BasicJson>
     * @param request
     *            the request must be a MultipartRequest type
     * @param id
     *            the operation user id
     * @return the batch result json
     */
    public List<String> putObject(MultipartRequest request, String id)
    {
        List<String> keyNameList = new ArrayList<String>();
        try
        {
            Map<String, MultipartFile> filesMap = request.getFileMap();
            Set<String> nameSet = filesMap.keySet();
            Iterator<String> it = nameSet.iterator();
            while (it.hasNext())
            {
                MultipartFile file = filesMap.get(it.next());
                ObjectMetadata ObjectMetadata = new ObjectMetadata();
                Map<String, String> userMetadata = new HashMap<String, String>();
                userMetadata.put("Name", file.getOriginalFilename());
                userMetadata.put("Content-Type", file.getContentType());
                String keyName = CommonUtil.getKeyName(bucketName, id, file.getOriginalFilename());
                userMetadata.put("key_name", keyName);
                userMetadata.put("user", id);
                ObjectMetadata.setUserMetadata(userMetadata);
                InputStream in = file.getInputStream();
                s3.putObject(bucketName, keyName, in, ObjectMetadata);
                keyNameList.add(keyName);
            }
        }
        catch (AmazonS3Exception e)
        {
            logger.error(e.getMessage());
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
        return keyNameList;
    }

    /**
     * Title: downloadObject Description: to call this interface from browser and download the Object directly
     * 
     * @return void
     * @param keyName
     *            the object's key
     * @param response
     *            filling the object's data into response
     */
    public void downloadObject(String keyName, HttpServletResponse response)
    {
        response.reset();
        OutputStream out;
        try
        {
            out = new BufferedOutputStream(response.getOutputStream());
            S3Object o = s3.getObject(bucketName, keyName);
            String fileName = o.getObjectMetadata().getUserMetadata().get("name");
            InputStream in = o.getObjectContent();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            FileCopyUtils.copy(in, out);
            out.close();
            in.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
        catch (AmazonS3Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    /**
     * Title: getObject Description: get a object by key
     * 
     * @return ObjectContent
     * @param keyName
     *            the object's key
     * @return return a ObjectContent instance, including fileName, inputStream, user
     */
    public ObjectContent getObject(String keyName)
    {
        ObjectContent oc = new ObjectContent();
        try
        {
            S3Object o = s3.getObject(bucketName, keyName);
            Map<String, String> userMEtadata = o.getObjectMetadata().getUserMetadata();
            oc.setFileName(userMEtadata.get("name"));
            InputStream in = o.getObjectContent();
            int len;
            byte[] b = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while ((len = in.read(b)) != -1)
            {
                sb.append(new String(b, 0, len, Charset.forName("utf-8")));
            }
            logger.info(">>>>>>>>>> the inputStream content is : " + sb.toString());
            oc.setInputStream(o.getObjectContent());
            oc.setCreateUser(userMEtadata.get("user"));
            return oc;
        }
        catch (AmazonS3Exception e)
        {
            logger.error(e.getMessage());
            return null;
        }
        catch (IOException e)
        {

            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * Title: getSpecifyObjects Description: get objects by some keys
     * 
     * @return List<ObjectContent>
     * @param keyNameList
     *            some keys
     * @return the batch result about ObjectContent instance
     */
    public List<ObjectContent> getSpecifyObjects(List<String> keyNameList)
    {
        List<ObjectContent> objectContentList = new ArrayList<ObjectContent>();
        for (String keyName : keyNameList)
        {
            objectContentList.add(getObject(keyName));
        }
        return objectContentList;
    }

    /**
     * Title: getAllObjects Description: get All objects by user's id
     * 
     * @return List<ObjectContent>
     * @param id
     *            the operation user id
     * @return the batch result about ObjectContent instance
     */
    public List<ObjectContent> getAllObjects(String id)
    {
        List<ObjectContent> objectContentList = new ArrayList<ObjectContent>();
        try
        {
            ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(id));
            List<S3ObjectSummary> s3ObjectSummary = objectListing.getObjectSummaries();
            for (S3ObjectSummary summary : s3ObjectSummary)
            {
                objectContentList.add(getObject(summary.getKey()));
            }
            return objectContentList;
        }
        catch (AmazonS3Exception e)
        {
            logger.error(e.getMessage());
            return objectContentList;
        }
    }

    /**
     * Title: getAllObjectsKey Description: get all Object keys by a bucket
     * 
     * @return List<String>
     * @return the all object keys list
     */
    public List<String> getAllObjectsKey()
    {
        List<String> keyNameList = new ArrayList<String>();
        try
        {
            ObjectListing ol = s3.listObjects(bucketName);
            List<S3ObjectSummary> objects = ol.getObjectSummaries();
            for (S3ObjectSummary os : objects)
            {
                keyNameList.add(os.getKey());
            }
        }
        catch (AmazonS3Exception e)
        {
            logger.error(e.getMessage());
        }
        return keyNameList;

    }

    /**
     * Title: deleteObject Description: delete a object by a key
     * 
     * @return boolean
     * @param keyName
     *            the object key
     * @return success or fail
     */
    public boolean deleteObject(String keyName)
    {
        try
        {
            s3.deleteObject(bucketName, keyName);
        }
        catch (AmazonS3Exception e)
        {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Title: getContentType Description: get an object's ContentType
     * 
     * @return String
     * @param keyName
     *            the object key
     * @return the contentType
     */
    public String getContentType(String keyName)
    {
        S3Object o = s3.getObject(bucketName, keyName);
        return o.getObjectMetadata().getContentType();
    }

    /**
     * Title: getRawMetedata Description: get an object's RawMetedata
     * 
     * @return Map<String,Object>
     * @param keyName
     *            the object key
     * @return the map about rawMeteData
     */
    public Map<String, Object> getRawMetedata(String keyName)
    {
        S3Object o = s3.getObject(bucketName, keyName);
        return o.getObjectMetadata().getRawMetadata();
    }

    /**
     * Title: getUserMetedata Description: the an object's userMetedata
     * 
     * @return Map<String,String>
     * @param keyName
     *            the object key
     * @return the map about userMetedata
     */
    public Map<String, String> getUserMetedata(String keyName)
    {
        S3Object o = s3.getObject(bucketName, keyName);
        return o.getObjectMetadata().getUserMetadata();
    }
}
