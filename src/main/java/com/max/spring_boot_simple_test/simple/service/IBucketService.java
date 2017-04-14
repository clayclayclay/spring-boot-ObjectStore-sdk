/**
 * File Name:IBucketService.java
 * Description: the operation about BucketService
 * Copyright: Copyright (c) 2017 
 * @author clayclayclay
 * @date Apr 13, 2017 5:42:32 PM
 * @version V1.0
 */
 
package com.max.spring_boot_simple_test.simple.service;

import java.util.List;

/**
  * ClassName: IBucketService
  * Description: the operation about BucketService
  * @author clayclayclay
  * @date Apr 13, 2017 5:42:32 PM
  */

public interface IBucketService
{

    public boolean createBucket(String bucketName);
    
    public boolean cleanBucket(String bucketName);
    
    public boolean deleteBucket(String bucketName);
    
    public List<String> getBuckets();
    
}
 
