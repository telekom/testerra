package eu.tsystems.mms.tic.testframework.mailconnector.util;

import javax.mail.internet.MimeMessage;

/**
 * @deprecated Use {@link TesterraMail} instead
 */
@Deprecated
public class XetaMail extends TesterraMail {
    public XetaMail(MimeMessage javaMessage) {
        super(javaMessage);
    }
}
