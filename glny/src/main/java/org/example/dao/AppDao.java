package org.example.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.model.*;

import java.util.List;
import java.util.Map;

public interface AppDao {
    @Select("select  * from fin_city_dic ")
    List<Map<String,String>> getBomList();

    //获取字典表
    @Select("select  * from fin_dic_dict where code = #{code} and type = #{type}")
    FineDicDict getBomListByCode(String code,String type);
    //修改字典表
    @Update("update fin_dic_dict set value = #{value} where code = #{code} and type = #{type}")
    void updateBomListByCode(FineDicDict dict);

    //写入文件表
    @Insert("insert into fin_dic_unicf_file (id,file_name,src,u_id,addtime) values (#{id},#{fileName},#{src},#{uid},#{addtime})")
    void insertFile(FileModel map);

    //获取文件表
    @Select("select * from fin_dic_unicf_file where u_id = #{uid}")
    FileModel getFile(String uid);

    //获取unifc表
    @Select("select * from fin_dic_unicf where id = #{id}")
    FinDicUnicf getUnicf(String id);

    @Insert("insert into fin_dic_unicf_reuse (id,sscc,pid) values (#{id},#{sscc},#{pid})")
    void insertReuse(String id,String sscc,String pid);

    //通过表名获取对应的permission
    @Select("select * from pur_dic_table_permission where title = #{title} and department=#{department} limit 1")
    PurDicTablePermission getPermission(String title,String department);

    //通过permission的id获取对应的参数
    @Select("select * from pur_dic_table_permission_params where pid = #{pid}")
    List<PurDicTablePermissionParams> getPermissionParams(String pid);

    //通过物料编码和批号模糊查询
    @Select("select * from log_dic_material_information_true where matnr like #{matnr} and charg like #{charg}")
    List<LogDicMaterialInformationTrue> getMaterialInformation(String matnr,String charg);

    //通过物料编码和批号查询旧系统
    @Select("select * from log_dic_material_information where matnr like #{matnr} and charg like #{charg}")
    List<LogDicMaterialInformationTrue> getMaterialInformationOldTable(String matnr,String charg);

    @Select("select * from log_print_title where is_enable='1'")
    LogDicMaterialInformationTrue getPrintTitle();

    @Select("select a.* from pda_app_user a join pda_app_user_password b on b.id = '1' where a.username = #{username} and b.password = #{password}")
    PdaAppUser appLogin(String username,String password);
}
