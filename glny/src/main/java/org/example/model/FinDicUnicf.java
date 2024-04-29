package org.example.model;


public class FinDicUnicf {

  private String id;
  private String type;
  private String title;
  private String sscc;
  private String lot;
  private String explry;
  private String prodDate;
  private String content;
  private String quantity;
  private String custPartNo;
  private String orderNumber;
  private String pid;
  private String addtime;
  private String flowNo;

  private Integer number;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public String getSscc() {
    return sscc;
  }

  public void setSscc(String sscc) {
    this.sscc = sscc;
  }


  public String getLot() {
    return lot;
  }

  public void setLot(String lot) {
    this.lot = lot;
  }


  public String getExplry() {
    return explry;
  }

  public void setExplry(String explry) {
    this.explry = explry;
  }


  public String getProdDate() {
    return prodDate;
  }

  public void setProdDate(String prodDate) {
    this.prodDate = prodDate;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }


  public String getCustPartNo() {
    return custPartNo;
  }

  public void setCustPartNo(String custPartNo) {
    this.custPartNo = custPartNo;
  }


  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }


  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }


  public String getAddtime() {
    return addtime;
  }

  public void setAddtime(String addtime) {
    this.addtime = addtime;
  }

  public String getFlowNo() {
    return flowNo;
  }

  public void setFlowNo(String flowNo) {
    this.flowNo = flowNo;
  }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

  @Override
  public String toString() {
    return "FinDicUnicf{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", title='" + title + '\'' +
            ", sscc='" + sscc + '\'' +
            ", lot='" + lot + '\'' +
            ", explry='" + explry + '\'' +
            ", prodDate='" + prodDate + '\'' +
            ", content='" + content + '\'' +
            ", quantity='" + quantity + '\'' +
            ", custPartNo='" + custPartNo + '\'' +
            ", orderNumber='" + orderNumber + '\'' +
            ", pid='" + pid + '\'' +
            ", addtime='" + addtime + '\'' +
            ", flowNo='" + flowNo + '\'' +
            '}';
  }

}
