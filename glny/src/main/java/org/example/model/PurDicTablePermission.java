package org.example.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description public
 * @author BEJSON
 * @date 2023-11-20
 */
@Data
public class PurDicTablePermission implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * id
     */
    private String id;

    /**
     * title
     */
    private String title;

    /**
     * is_delete
     */
    private String isDelete;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * create_user
     */
    private String createUser;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * update_user
     */
    private Date updateUser;

    /**
     * version
     */
    private String version;

    /**
     * remarks
     */
    private String remarks;

    /**
     * field
     */
    private String field;

    /**
     * tid
     */
    private String tid;

    /**
     * department
     */
    private String department;

    private List<PurDicTablePermissionParams> params;

    public PurDicTablePermission() {}
}