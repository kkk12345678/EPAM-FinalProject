package com.epam.kkorolkov.finalproject.tag;

import com.epam.kkorolkov.finalproject.db.dao.AbstractDaoFactory;
import com.epam.kkorolkov.finalproject.db.dao.CategoryDao;
import com.epam.kkorolkov.finalproject.db.dao.PublisherDao;
import com.epam.kkorolkov.finalproject.db.datasource.AbstractDataSourceFactory;
import com.epam.kkorolkov.finalproject.db.datasource.DataSource;
import com.epam.kkorolkov.finalproject.db.entity.Category;
import com.epam.kkorolkov.finalproject.exception.DBException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;

public class CategoryTagHandler extends SimpleTagSupport {
    private Category category;
    private final StringWriter stringWriter = new StringWriter();

    public void setCategory(Category category) {
        this.category = category;
    }

    public void doTag() throws JspException {

        DataSource dataSource = null;
        Connection connection = null;
        try {
            JspWriter out = getJspContext().getOut();
            dataSource = AbstractDataSourceFactory.getInstance().getDataSource();
            connection = dataSource.getConnection();
            CategoryDao categoryDao = AbstractDaoFactory.getInstance().getCategoryDao();
            List<Category> categories = categoryDao.getAll(connection);
            getJspBody().invoke(stringWriter);
            if (category == null || category.getId() == 0) {
                out.println("<option selected disabled>" + stringWriter + "</option>");
                for (Category c : categories) {
                    out.println("<option value=\"" + c.getId() + "\">" + c.getTag() + "</option>");
                }
            } else {
                for (Category c : categories) {
                    if (c.getId() == category.getId()) {
                        out.println("<option selected value=\"" + c.getId() + "\">" + c.getTag() + "</option>");
                    } else {
                        out.println("<option value=\"" + c.getId() + "\">" + c.getTag() + "</option>");
                    }
                }
            }
        } catch (IOException | DBException e) {
            // TODO handle DBException
        } finally {
            if (dataSource != null) {
                dataSource.release(connection);
            }
        }
    }
}
