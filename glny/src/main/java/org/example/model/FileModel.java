package org.example.model;




public class FileModel {

    /**
     * 文件ID
     */
    private String id;

    /**
     * 文件名
     */
    private String fileName;


    /**
     * 文件相对路径
     */
    private String src;

    /**
     * 关联记录id
     */
    private String uid;

    /**
     * 添加时间
     */
    private String addtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
