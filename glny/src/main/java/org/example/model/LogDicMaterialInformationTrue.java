package org.example.model;

import lombok.Data;

import java.util.Date;

/**
 * @description log_dic_material_information_true
 * @author BEJSON
 * @date 2023-12-25
 */
@Data
public class LogDicMaterialInformationTrue {

    private static final long serialVersionUID = 1L;


    /**
     * id
     */
    private String id;

    /**
     * matnr
     */
    private String matnr;

    /**
     * charg
     */
    private String charg;

    /**
     * maktx
     */
    private String maktx;

    /**
     * maker
     */
    private String maker;

    /**
     * vfdat
     */
    private String vfdat;

    /**
     * fydat
     */
    private String fydat;

    /**
     * storage
     */
    private String storage;

    /**
     * lfadt
     */
    private String lfadt;

    /**
     * mnum
     */
    private String mnum;

    /**
     * num
     */
    private String num;

    /**
     * matyp
     */
    private String matyp;

    /**
     * input_time
     */
    private Date inputTime;

    /**
     * input_user
     */
    private String inputUser;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * update_user
     */
    private String updateUser;
    private String labst;
    private String insme;
    private String speme;
    private String zspecno;
    private String zspeno;
    private String fileName;
    private String fileNo;

    public LogDicMaterialInformationTrue() {}
}