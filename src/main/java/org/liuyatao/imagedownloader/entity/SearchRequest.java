package org.liuyatao.imagedownloader.entity;

/**
 * @author liuyatao
 * @date 2018/7/1
 */
public class SearchRequest {

    private String annType;

    private Integer page;

    private Integer rows;

    private Integer annNum;

    public String getAnnType() {
        return annType;
    }

    public void setAnnType(String annType) {
        this.annType = annType;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getAnnNum() {
        return annNum;
    }

    public void setAnnNum(Integer annNum) {
        this.annNum = annNum;
    }
}
