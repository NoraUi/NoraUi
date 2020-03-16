package com.github.noraui.application.page;

public class PageWithPrivatePageElement extends Page {

    // This element is used to check illegal access to page element
    @SuppressWarnings("unused")
    private final PageElement privateElement = new PageElement("-privateElement");

    @Override
    public boolean checkPage(Object... elements) {
        // TODO Auto-generated method stub
        return false;
    }
}