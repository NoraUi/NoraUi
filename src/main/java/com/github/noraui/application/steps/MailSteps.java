/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.cucumber.annotation.Experimental;
import com.github.noraui.cucumber.annotation.RetryOnFailure;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.HttpServiceException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.service.HttpService;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.google.inject.Inject;

/**
 * This class contains Gherkin callable steps that aim for expecting a specific result.
 */
public class MailSteps extends Step {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSteps.class);

    @Inject
    private HttpService httpService;

    /**
     * Valid activation email.
     *
     * @param mailHost
     *            example: imap.gmail.com
     * @param mailUser
     *            login of mail box
     * @param mailPassword
     *            password of mail box
     * @param firstCssQuery
     *            the first matching element
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link com.github.noraui.gherkin.GherkinStepCondition}).
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Experimental(name = "validActivationEmail")
    @RetryOnFailure(attempts = 3, delay = 60)
    @Conditioned
    //@Et("Je valide le mail d activation {string}\\./\\?")
    //@And("I valid activation email {string}\\./\\?")
    public void validActivationEmail(String mailHost, String mailUser, String mailPassword, String senderMail, String subjectMail, String firstCssQuery, List<GherkinStepCondition> conditions)
            throws FailureException, TechnicalException {
        try {
            final Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imap");
            final Session session = Session.getDefaultInstance(props, null);
            final Store store = session.getStore("imaps");
            store.connect(mailHost, mailUser, mailPassword);
            final Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
            final SearchTerm filterA = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            final SearchTerm filterB = new FromTerm(new InternetAddress(senderMail));
            final SearchTerm filterC = new SubjectTerm(subjectMail);
            final SearchTerm[] filters = { filterA, filterB, filterC };
            final SearchTerm searchTerm = new AndTerm(filters);
            final Message[] messages = inbox.search(searchTerm);
            for (final Message message : messages) {
                validateActivationLink(subjectMail, firstCssQuery, message);
            }
        } catch (final Exception e) {
            new Result.Failure<>("", Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_MAIL_ACTIVATION), subjectMail), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * @param message
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            final MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    /**
     * @param mimeMultipart
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        final StringBuilder result = new StringBuilder();
        final int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            final BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n");
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append("\n");
                result.append((String) bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private void validateActivationLink(String subjectMail, String firstCssQuery, Message message) throws MessagingException, IOException, TechnicalException, FailureException {
        final Document doc = Jsoup.parse(getTextFromMessage(message));
        final Element link = doc.selectFirst(firstCssQuery);
        try {
            final String response = httpService.get(link.attr("href"));
            LOGGER.debug("response is {}.", response);
        } catch (final HttpServiceException e) {
            LOGGER.error(Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_MAIL_ACTIVATION), subjectMail), e);
            new Result.Failure<>("", Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_MAIL_ACTIVATION), subjectMail), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }
}
