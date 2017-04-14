

/**
 * File Name:IObjectService.java
 * Description: the operation about BucketService
 * Copyright: Copyright (c) 2017 
 * @author clayclayclay
 * @date Apr 13, 2017 5:45:03 PM
 * @version V1.0
 */
 
package com.max.spring_boot_simple_test.simple.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartRequest;

import com.max.spring_boot_simple_test.simple.model.ObjectContent;


/**
  * ClassName: IObjectService
  * Description: the operation about BucketService
  * @author clayclayclay
  * @date Apr 13, 2017 5:45:03 PM
  */

public interface IObjectService
{

    public String putObject(InputStream in, String fileName, String id);
    
    public List<String> putObject(MultipartRequest request, String id);
    
    public void downloadObject(String keyName, HttpServletResponse response);
    
    public ObjectContent getObject(String keyName);
    
    public List<ObjectContent> getSpecifyObjects(List<String> keyNameList);
    
    public List<ObjectContent> getAllObjects(String id);
    
    public List<String> getAllObjectsKey();
    
    public boolean deleteObject(String keyName);
    
    public String getContentType(String keyName);
    
    public Map<String, Object> getRawMetedata(String keyName);
    
    public Map<String, String> getUserMetedata(String keyName);
    
}
 
