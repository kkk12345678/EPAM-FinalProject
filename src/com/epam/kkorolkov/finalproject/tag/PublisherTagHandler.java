package com.epam.kkorolkov.finalproject.tag;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Publisher;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;

public class PublisherTagHandler extends SimpleTagSupport {
    private Publisher publisher;
    private final StringWriter stringWriter = new StringWriter();

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();
        DataSource dataSource = null;
        Connection connection = null;
        try {
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            PublisherDao publisherDao = AbstractDaoFactory.getInstance().getPublisherDao();
            List<Publisher> publishers = publisherDao.getAll(connection);
            getJspBody().invoke(stringWriter);
            if (publisher == null || publisher.getId() == 0) {
                out.println("<option selected disabled>" + stringWriter + "</option>");
                for (Publisher p : publishers) {
                    out.println("<option value=\"" + p.getId() + "\">" + p.getTag() + "</option>");
                }
            } else {
                for (Publisher p : publishers) {
                    if (p.getId() == publisher.getId()) {
                        out.println("<option selected value=\"" + p.getId() + "\">" + p.getTag() + "</option>");
                    } else {
                        out.println("<option value=\"" + p.getId() + "\">" + p.getTag() + "</option>");
                    }
                }
            }
        } catch (IOException | DBException e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
