package noraui.application.page;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import noraui.exception.Callbacks.Callback;
import noraui.exception.TechnicalException;
import noraui.utils.Context;

public abstract class Page implements IPage {

    private static String pagesPackage = Page.class.getPackage().getName() + '.';

    private static Logger logger = Logger.getLogger(Page.class.getName());

    private static ArrayList<Page> instances = new ArrayList<>();

    private static volatile WebDriver webDriver = null;

    protected Page motherPage = null;

    protected String pageKey;

    protected String application;

    protected Callback callBack;

    protected Page() {
        webDriver = Context.getDriver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callback getCallBack() {
        return callBack;
    }

    /**
     * Create a instance of page if class in parameter not in instances list else use instance already exist.
     *
     * @param c
     *            is a class
     * @return a Page
     * @throws TechnicalException
     *             if InstantiationException or IllegalAccessException in getInstance of Page.
     */
    public static Page getInstance(Class<?> c) throws TechnicalException {
        for (Page page : instances) {
            if (page.getClass() == c) {
                Page.webDriver = Context.getDriver();
                return page;
            }
        }
        try {
            return (Page) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new TechnicalException("InstantiationException or IllegalAccessException in getInstance of Page", e);
        }
    }

    /**
     * Finds a Page by its name (not the full qualified name).
     *
     * @param className
     *            The name of the class to find. Full qualified name is not required.
     *            Ex: 'MyPage' or 'mypackageinpages.MyPage'
     * @return
     *         A Page instance
     * @throws TechnicalException
     *             if InstantiationException or IllegalAccessException in getInstance of Page.
     */
    public static Page getInstance(String className) throws TechnicalException {
        try {
            return getInstance(Class.forName(pagesPackage + className));
        } catch (ClassNotFoundException e) {
            throw new TechnicalException("Unable to retrieve Page with name: " + className, e);
        }
    }

    /**
     * Sets the Page main package used to find pages by their class name.
     *
     * @param packageName
     *            The new Page package name
     */
    public static void setPageMainPackage(String packageName) {
        pagesPackage = packageName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageElement getPageElementByKey(String key) {
        PageElement p;
        for (Field f : getClass().getDeclaredFields()) {
            if (f.getType() == PageElement.class) {
                try {
                    p = (PageElement) f.get(this);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error(e);
                    return null;
                }
                if (key.equals(p.getKey())) {
                    return p;
                }
            }
        }
        return null;
    }

    public static WebDriver getDriver() {
        return webDriver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPageKey() {
        return pageKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getApplication() {
        return application;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page getMotherPage() {
        return motherPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMotherPage(Page motherPage) {
        this.motherPage = motherPage;
    }

    public class PageElement implements IPageElement {
        private String key = "";
        private String label = "";

        public PageElement(String key) {
            this.key = key;
        }

        public PageElement(String key, String label) {
            this.key = key;
            this.label = label;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getKey() {
            return this.key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getLabel() {
            return this.label;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Page getPage() {
            return Page.this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if ("".equals(label)) {
                return key;
            }
            return label;
        }

    }
}
