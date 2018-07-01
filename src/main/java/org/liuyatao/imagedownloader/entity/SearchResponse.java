package org.liuyatao.imagedownloader.entity;

/**
 * @author liuyatao
 * @date 2018/7/1
 */
public class SearchResponse {
    private Integer page_no;
    private String id;
    private String regname;
    private String annDate;

    public Integer getPage_no() {
        return page_no;
    }

    public void setPage_no(Integer page_no) {
        this.page_no = page_no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegname() {
        return regname;
    }

    public void setRegname(String regname) {
        this.regname = regname;
    }

    public String getAnnDate() {
        return annDate;
    }

    public void setAnnDate(String annDate) {
        this.annDate = annDate;
    }
}
