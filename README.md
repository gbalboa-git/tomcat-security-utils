# Encription utility classes for Tomcat

# Description
Utility classes to encrypt passwords using the **_PBKDF2WithHmacSHA512_** algorithm, to avoid storing databse user passwrods in plain text in Tomcat configuration files. 

# A few words before...
I do not intent to open a debate about storing passwords in plain text in configuration files, there are a lot o articles, blogs, etc., etc., out there talking about that topic. You can read [here](https://cwiki.apache.org/confluence/display/TOMCAT/Password) the Tomcat team official information about passwords in plain text stored in confioguration files.  

I agree with many people that thinks that if an attacker has control over your tomcat files, the passwords in config files might not be your worst problem :), I also agreed that having those passwords encrypted somehow, would add an additional layer of security to your app, helps to please the auditors and may allow you to pass their checklist.

# How it works

When you configure a resource to connect to your database, you create something like the code below (you can read the Tomcat documentation [JNDI Datasource How-to](
https://tomcat.apache.org/tomcat-9.0-doc/jndi-datasource-examples-howto.html) ). The resource uses a default factory **[org.apache.tomcat.jdbc.pool.DataSourceFactory](https://github.com/waihoyu/Apache-Tomcat-9/blob/master/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/DataSourceFactory.java)**, you can read about the factory and type [here](https://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html#JNDI_Factory_and_Type).<br> 

```
<GlobalNamingResources>
  ...
  <Resource name="sharedDataSource"
            global="sharedDataSource"
            type="javax.sql.DataSource"
            factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
            alternateUsernameAllowed="true"
            username="bar"
            password="barpass"
            ...
  ...

```

The class **_org.balvigu.jdbc.EncryptedDataSourceFactory_** in this project, extends from the class **_DataSourceFactory_** and overrides its method **_createDataSource_** with the following simple code that reads the property password and decrypt it befor create the data source.

```
@Override
    public DataSource createDataSource(Properties properties, Context context, boolean XA) throws Exception {
        String encryptedPassword=properties.getProperty(PROP_PASSWORD);
        properties.put(PROP_PASSWORD, cryptor.decrypt(encryptedPassword));
        return super.createDataSource(properties, context, XA);
    }
```
# How to use it


## To encrypt passwords:
Compile the code using maven, go to target directory and type: 
``` 
java -jar tomcat-security-utils-1.0.jar MySupperSecret
```
the output would be something like: 
```
MySupperSecret:24cbb79e6659359cf88a76ddb9922c2d1814038755626acd1739bb38de2b15b1c50fe43b8a024730677dd3c64e723f1e
```
## How to use in Tomcat
Once you have the compiled jar, copy it into $CATALINA_HOME/lib directory, encrypt your database bassword as described en previous section, copy the output hash, and use it in your resource configuration as your password.

You just need tu use org.balvigu.jdbc.EncryptedDataSourceFactory insted of default one and set the property password with the encrypted hash.

```
<Resource name="jdbc/authority" 
            factory="org.balvigu.jdbc.EncryptedDataSourceFactory"             
            type="javax.sql.DataSource"
            username="your_db_user" 
            password="your_encrypted_pass" 
            ...
            other stuff...
```

Any suggestions are welcome.
<br>


<font size="2">_Most of the credit for the ideas and fragments of code that I used for this project should be for the guy who wrote this article: [Encrypting passwords in Tomcat](https://www.jdev.it/encrypting-passwords-in-tomcat/)_ </font>  





