/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application;

import java.util.HashMap;
import java.util.Map;

public class Application {

    private Map<String, String> urlPages;
    private String homeKey;
    private String homeUrl;

    /**
     * Constructor by default.
     */
    public Application() {
        this.urlPages = new HashMap<>();
    }

    /**
     * Constructor using home page (default page).
     *
     * @param homeKey
     *            is key corresponding to home page
     * @param homeUrl
     *            is url of home page
     */
    public Application(String homeKey, String homeUrl) {
        this.homeKey = homeKey;
        this.homeUrl = homeUrl;
        this.urlPages = new HashMap<>();
        this.urlPages.put(homeKey, homeUrl);
    }

    /**
     * Getter of homeKey.
     *
     * @return homeKey
     */
    public String getHomeKey() {
        return homeKey;
    }

    /**
     * Getter of homeUrl.
     *
     * @return homeUrl
     */
    public String getHomeUrl() {
        return homeUrl;
    }

    public Map<String, String> getUrlPages() {
        return urlPages;
    }

    /**
     * add a url page to an application.
     *
     * @param key
     *            of page
     * @param url
     *            of page
     */
    public void addUrlPage(String key, String url) {
        this.urlPages.put(key, url);
    }

}