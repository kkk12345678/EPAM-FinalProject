package com.epam.kkorolkov.finalproject.tag;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class PagesTagHandler extends SimpleTagSupport {
    private int total;
    private int current;

    public void setTotal(int total) {
        this.total = total;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void doTag() {
        if (total != 1) {
            try {
                JspWriter out = getJspContext().getOut();
                out.println("<span id=\"total\" hidden>" + total + "</span>");
                if (total < 10) {
                    for (int i = 1; i <= total; i++) {
                        out.println("<button class=\"button-page\">" + i + "</button>");
                    }
                } else {
                    out.println("<button class=\"button-page\"><</button>");
                    if (current <= 3) {
                        for (int i = 1; i <= 3; i++) {
                            out.println("<button class=\"button-page\">" + i + "</button>");
                        }
                        out.println("<button style=\"border: none\">...</button>");
                        out.println("<button class=\"button-page\">" + total + "</button>");
                        out.println("<button class=\"button-page\">></button>");
                    } else if (total - current <= 2) {
                        out.println("<button class=\"button-page\">1</button>");
                        out.println("<button style=\"border: none\">...</button>");
                        for (int i = current - 1; i <= total; i++) {
                            out.println("<button class=\"button-page\">" + i + "</button>");
                        }
                        out.println("<button class=\"button-page\">></button>");
                    } else {
                        out.println("<button class=\"button-page\">1</button>");
                        out.println("<button style=\"border: none\">...</button>");
                        for (int i = current - 1; i <= current + 1; i++) {
                            out.println("<button class=\"button-page\">" + i + "</button>");
                        }
                        out.println("<button style=\"border: none\">...</button>");
                        out.println("<button class=\"button-page\">" + total + "</button>");
                        out.println("<button class=\"button-page\">></button>");
                    }
                }
            } catch (IOException e) {
                // TODO handle IOException
            }
        }
    }
}
