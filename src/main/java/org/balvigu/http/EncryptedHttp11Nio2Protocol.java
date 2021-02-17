package org.balvigu.http;

import org.balvigu.crypto.Cryptor;

public class EncryptedHttp11Nio2Protocol extends org.apache.coyote.http11.Http11NioProtocol {

    private Cryptor cryptor =  null;

    public EncryptedHttp11Nio2Protocol() {
        super();
        cryptor = new Cryptor();
    }

    @Override
    public void setKeystorePass(String keyPass) {
        try {
            super.setKeystorePass(cryptor.decrypt(keyPass));
        } catch (final Exception e){
            super.setKeystorePass("");
        }
    }

}
