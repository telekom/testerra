package eu.tsystems.mms.tic.testframework.mailconnector.util;

import javax.mail.internet.MimeMessage;

/**
 * @deprecated Use {@link EMail} instead
 */
@Deprecated
public class XetaMail extends EMail {
    public XetaMail(MimeMessage javaMessage) {
        super(javaMessage);
    }
}
