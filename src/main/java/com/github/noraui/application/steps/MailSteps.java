/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
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

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.cucumber.annotation.RetryOnFailure;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

import cucumber.api.java.en.And;
import cucumber.api.java.fr.Et;

/**
 * This class contains Gherkin callable steps that aim for expecting a specific result.
 */
public class MailSteps extends Step {

    /**
     * Valid activation email.
     *
     * @param mailHost
     *            example: imap.gmail.com
     * @param mailUser
     *            login of mail box
     * @param mailHost
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
    @RetryOnFailure(attempts = 3, delay = 60)
    @Conditioned
    @Et("Je valide le mail d'activation '(.*)'[\\.|\\?]")
    @And("I valid activation email '(.*)'[\\.|\\?]")
    public void validActivationEmail(String mailHost, String mailUser, String mailPassword, String senderMail, String subjectMail, String firstCssQuery, List<GherkinStepCondition> conditions)
            throws FailureException, TechnicalException {
        RestTemplate restTemplate = createRestTemplate();
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imap");
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect(mailHost, mailUser, mailPassword);
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
            SearchTerm filterA = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            SearchTerm filterB = new FromTerm(new InternetAddress(senderMail));
            SearchTerm filterC = new SubjectTerm(subjectMail);
            SearchTerm[] filters = { filterA, filterB, filterC };
            SearchTerm searchTerm = new AndTerm(filters);
            Message[] messages = inbox.search(searchTerm);
            for (Message message : messages) {
                Document doc = Jsoup.parse(getTextFromMessage(message));
                Element link = doc.selectFirst(firstCssQuery);
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(link.attr("href"), HttpMethod.GET, entity, String.class);
                if (!response.getStatusCode().equals(HttpStatus.OK)) {
                    new Result.Failure<>("", Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_MAIL_ACTIVATION), subjectMail), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
                }
            }
        } catch (Exception e) {
            new Result.Failure<>("", Messages.format(Messages.getMessage(Messages.FAIL_MESSAGE_MAIL_ACTIVATION), subjectMail), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    private static RestTemplate createRestTemplate() {
        String proxyUser = "";
        String proxyPassword = "";
        String proxyUrl = "";
        int proxyPort = 0;
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        if (!"".equals(proxyUrl)) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxyUrl, proxyPort), new UsernamePasswordCredentials(proxyUser, proxyPassword));
            HttpHost myProxy = new HttpHost(proxyUrl, proxyPort);
            clientBuilder.setProxy(myProxy).setDefaultCredentialsProvider(credsProvider).disableCookieManagement();
        }
        HttpClient httpClient = clientBuilder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        return restTemplate;
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

}
