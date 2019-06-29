package cn.orditech.stockanalysis.entity;

/**
 * @author kimi
 */
public class WechatPublic extends BaseEntity {
    private static final long serialVersionUID = 5454155825314635342L;
    
    /** 
     * id 
     **/
    private Long id;
    /** 
     * 公众号名称 
     **/
    private String name;
    /** 
     * 功能介绍 
     **/
    private String description;
    /** 
     * 账号主体 
     **/
    private String certifiedText;
    /** 
     * 微信号 
     **/
    private String account;
    /** 
     * 公众号原始ID 
     **/
    private String wxid;
    /** 
     * 新榜公众号标签 
     **/
    private String tags;
    /** 
     * 新榜公众号类别 
     **/
    private String type;
    /** 
     * headImageUrl 
     **/
    private String headImageUrl;
    /** 
     * codeImageUrl 
     **/
    private String codeImageUrl;
    /** 
     * 新榜指数 
     **/
    private Integer rankIndex;

    public void setId(Long value) {
        this.id = value;
    }
    public Long getId() {
        return this.id;
    }
    
    public void setName(String value) {
        this.name = value;
    }
    public String getName() {
        return this.name;
    }
    
    public void setDescription(String value) {
        this.description = value;
    }
    public String getDescription() {
        return this.description;
    }
    
    public void setCertifiedText(String value) {
        this.certifiedText = value;
    }
    public String getCertifiedText() {
        return this.certifiedText;
    }
    
    public void setAccount(String value) {
        this.account = value;
    }
    public String getAccount() {
        return this.account;
    }
    
    public void setWxid(String value) {
        this.wxid = value;
    }
    public String getWxid() {
        return this.wxid;
    }
    
    public void setTags(String value) {
        this.tags = value;
    }
    public String getTags() {
        return this.tags;
    }
    
    public void setType(String value) {
        this.type = value;
    }
    public String getType() {
        return this.type;
    }
    
    public void setHeadImageUrl(String value) {
        this.headImageUrl = value;
    }
    public String getHeadImageUrl() {
        return this.headImageUrl;
    }
    
    public void setCodeImageUrl(String value) {
        this.codeImageUrl = value;
    }
    public String getCodeImageUrl() {
        return this.codeImageUrl;
    }
    
    public void setRankIndex(Integer value) {
        this.rankIndex = value;
    }
    public Integer getRankIndex() {
        return this.rankIndex;
    }
    
    
}

