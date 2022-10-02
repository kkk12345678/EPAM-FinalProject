package com.epam.kkorolkov.finalproject.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

public class TitleTagHandler extends SimpleTagSupport {
    private final StringWriter stringWriter = new StringWriter();

    public void doTag() {
        PageContext context = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        try {
            JspWriter writer = context.getOut();
            String[] parts = request.getRequestURI().split("/");
            getJspBody().invoke(stringWriter);
            String[] titles = stringWriter.toString().split(",");
            if (parts[parts.length - 1].equals("login")) {
                writer.print(titles[0]);
            } else {
                writer.print(titles[1]);
            }
        } catch (IOException | JspException e) {
            e.printStackTrace();
        }
    }
}
