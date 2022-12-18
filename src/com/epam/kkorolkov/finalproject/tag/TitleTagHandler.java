package com.epam.kkorolkov.finalproject.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

/**
 *  Class {@code TitleTagHandler} supports custom JSP tag
 *  'auth-title'. It is used to set page title depending on URL.
 */
public class TitleTagHandler extends SimpleTagSupport {
    private static final String SLASH = "/";
    private static final String COMMA = ",";
    private static final String LOGIN = "login";

    private final StringWriter stringWriter = new StringWriter();

    /**
     * Writes to {@link JspWriter} appropriate value of 'title' tag.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     * @throws JspException is thrown if an error occurred while the fragment.
     */
    public void doTag() throws IOException, JspException {
        PageContext context = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        JspWriter writer = context.getOut();
        String[] parts = request.getRequestURI().split(SLASH);
        getJspBody().invoke(stringWriter);
        String[] titles = stringWriter.toString().split(COMMA);
        if (parts[parts.length - 1].equals(LOGIN)) {
            writer.print(titles[0]);
        } else {
            writer.print(titles[1]);
        }
    }
}
