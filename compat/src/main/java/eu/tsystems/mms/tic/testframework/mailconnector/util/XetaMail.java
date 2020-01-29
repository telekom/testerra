package eu.tsystems.mms.tic.testframework.mailconnector.util;

import javax.mail.internet.MimeMessage;

/**
 * @deprecated Use {@link Email} instead
 */
@Deprecated
public class XetaMail extends Email {
    public XetaMail(MimeMessage javaMessage) {
        super(javaMessage);
    }
}
