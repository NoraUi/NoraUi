package noraui.application.page;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import noraui.exception.Callbacks.Callback;
import noraui.exception.TechnicalException;
import noraui.utils.Context;
import noraui.utils.Messages;

public abstract class Page implements IPage {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Page.class);

    private static final String PAGE_UNABLE_TO_RETRIEVE = "PAGE_UNABLE_TO_RETRIEVE";

    private static String pagesPackage = Page.class.getPackage().getName() + '.';

    private static ArrayList<Page> instances = new ArrayList<>();

    protected Page motherPage = null;

    protected String pageKey;

    protected String application;

    protected Callback callBack;

    protected Page() {
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
     *             if InstantiationException or IllegalAccessException in getInstance() of Page.
     */
    public static Page getInstance(Class<?> c) throws TechnicalException {
        for (final Page page : instances) {
            if (page.getClass() == c) {
                return page;
            }
        }
        try {
            return (Page) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new TechnicalException(Messages.format(Messages.getMessage(PAGE_UNABLE_TO_RETRIEVE), c), e);
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
     *             if ClassNotFoundException in getInstance() of Page.
     */
    public static Page getInstance(String className) throws TechnicalException {
        try {
            return getInstance(Class.forName(pagesPackage + className));
        } catch (final ClassNotFoundException e) {
            throw new TechnicalException(Messages.format(Messages.getMessage(PAGE_UNABLE_TO_RETRIEVE), className), e);
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
        for (final Field f : getClass().getDeclaredFields()) {
            if (f.getType() == PageElement.class) {
                try {
                    p = (PageElement) f.get(this);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error("error Page.getPageElementByKey()", e);
                    return null;
                }
                if (key.equals(p.getKey())) {
                    return p;
                }
            }
        }
        return new PageElement(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebDriver getDriver() {
        return Context.getDriver();
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
