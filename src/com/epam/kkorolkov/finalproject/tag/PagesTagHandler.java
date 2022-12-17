package com.epam.kkorolkov.finalproject.tag;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 *  Class {@code PagesTagHandler} supports custom JSP tag
 *  '<pages>'. It is used to render pagination control buttons.
 *  The tag has two required parameters: <ul>
 *      <li>current</li> - the current page;
 *      <li>total</li> - total number of pages;
 *  </ul>
 */
public class PagesTagHandler extends SimpleTagSupport {
    /** HTML tags */
    private final static String TAG_TOTAL_HIDDEN = "<span id=\"total\" hidden>%d</span>";
    private final static String TAG_PAGE = "<button class=\"button-page\">%d</button>";
    private final static String TAG_LEFT = "<button class=\"button-page\"><</button>";
    private final static String TAG_RIGHT = "<button class=\"button-page\">></button>";
    private final static String TAG_TOTAL = "<button style=\"border: none\">...</button>\n" +
            "<button class=\"button-page\">%d</button>";
    private final static String TAG_FIRST = "<button class=\"button-page\">1</button>";
    private final static String TAG_ETC = "<button style=\"border: none\">...</button>";

    /** Total number of pages */
    private int total;

    /** Current page */
    private int current;

    public void setTotal(int total) {
        this.total = total;
    }

    @SuppressWarnings("unused")
    public void setCurrent(int current) {
        this.current = current;
    }

    /**
     * Writes to {@code JspWriter} HTML tags necessary to
     * render pagination control buttons.
     *
     * @throws IOException is thrown if an input or output exception occurs.
     */
    public void doTag() throws IOException {
        if (total != 1) {
            JspWriter out = getJspContext().getOut();
            out.println(String.format(TAG_TOTAL_HIDDEN, total));
            if (total < 10) {
                for (int i = 1; i <= total; i++) {
                    out.println(String.format(TAG_PAGE, i));
                }
            } else {
                out.println(TAG_LEFT);
                if (current <= 3) {
                    for (int i = 1; i <= 3; i++) {
                        out.println(String.format(TAG_PAGE, i));
                    }
                    out.println(String.format(TAG_TOTAL, total));
                    out.println(TAG_RIGHT);
                } else if (total - current <= 2) {
                    out.println(TAG_FIRST);
                    out.println(TAG_ETC);
                    for (int i = current - 1; i <= total; i++) {
                        out.println(String.format(TAG_PAGE, i));
                    }
                    out.println(TAG_RIGHT);
                } else {
                    out.println(TAG_FIRST);
                    out.println(TAG_ETC);
                    for (int i = current - 1; i <= current + 1; i++) {
                        out.println(String.format(TAG_PAGE, i));
                    }
                    out.println(TAG_ETC);
                    out.println(TAG_TOTAL);
                    out.println(TAG_RIGHT);
                }
            }
        }
    }
}
