package org.example.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description pur_dic_table_permission_params
 * @author BEJSON
 * @date 2023-11-20
 */
@Data
public class PurDicTablePermissionParams implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * id
     */
    private String id;

    /**
     * pid
     */
    private String pid;

    /**
     * param
     */
    private String param;

    /**
     * remark
     */
    private String remark;

    /**
     * value
     */
    private String value;

    /**
     * addtime
     */
    private Date addtime;

    public PurDicTablePermissionParams() {}
}