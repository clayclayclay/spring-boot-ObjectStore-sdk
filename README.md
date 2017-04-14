# spring-boot-ObjectStore-sdk

This is a spring-boot-ObjectStore-sdk based spring-boot 1.0.2 and aws-java-sdk 1.11.116.

You need to read the below details before you want to use it.

## prerequisite

* You should have an account on AWS.
* you should have the service about S3.
* basic Java development experiment.

## Getting Started

#### install the sdk

1.download the source

2.compile it to the jar

#### the interface about SDK

* the operation on object

```java
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
```

* the operation on bucket

```java
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
```



#### some details about Interface

* putObject method
* getObject method

the method :`putObject`:

```java
public String putObject(InputStream in, String fileName, String id);
```

##### Input:

object's inputStream, fileName, the operation id

##### Return: 

the object's keyName



the method :`putObject`:

```java
 public List<String> putObject(MultipartRequest request, String id);
```

##### Input:

request,The MultipartRequest type (from Web front) , the operation id

##### Return:

the object's name list.



the method: 

```java
public ObjectContent getObject(String keyName);
```

##### Input:

the object's key

##### Return:

the ObjectContent instance.

The ObjectContent contains three fields:

```java
    private String fileName;
    private InputStream inputStream;
    private String createUser;
```

So, once you get an ObjectContent instance, you can get the fileName , the Object's inputStream and the user.



