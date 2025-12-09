package toy.com.egov;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.AbstractPaginationRenderer;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

public class EgovImgPaginationRenderer extends AbstractPaginationRenderer implements ServletContextAware {
    private ServletContext servletContext;

    public void initVariables() {
        this.firstPageLabel = "<li class=\"first\"><a href=\"#\" onclick=\"{0}({1}); return false;\"></a></li>";
        this.previousPageLabel = "<li class=\"prev\"><a href=\"#\" onclick=\"{0}({1}); return false;\"></a></li>";
        this.currentPageLabel = "<li class=\"active\">{0}</li>";
        this.otherPageLabel = "<li><a href=\"#\" onclick=\"{0}({1}); return false;\">{2}</a></li>";
        this.nextPageLabel = "<li class=\"next\"><a href=\"#\" onclick=\"{0}({1}); return false;\"></a></li>";
        this.lastPageLabel = "<li class=\"last\"><a href=\"#\" onclick=\"{0}({1}); return false;\"></a></li>";
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.initVariables();
    }
}