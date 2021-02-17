package org.balvigu.jdbc;

import org.balvigu.crypto.Cryptor;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;

import javax.naming.Context;
import javax.sql.DataSource;
import java.util.Properties;

public class EncryptedDataSourceFactory extends DataSourceFactory {
    
    private Cryptor cryptor =  null;

    public EncryptedDataSourceFactory() {
        cryptor = new Cryptor();
    }

    @Override
    public DataSource createDataSource(Properties properties, Context context, boolean XA) throws Exception {
        String encryptedPassword=properties.getProperty(PROP_PASSWORD);
        properties.put(PROP_PASSWORD, cryptor.decrypt(encryptedPassword));
        return super.createDataSource(properties, context, XA);
    }

}
