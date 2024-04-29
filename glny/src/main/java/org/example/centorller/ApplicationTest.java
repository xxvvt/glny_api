package org.example.centorller;


import cn.hutool.crypto.digest.MD5;
import com.alibaba.druid.sql.visitor.functions.Char;
import com.sap.conn.jco.JCoTable;
import io.swagger.annotations.ApiOperation;
import org.example.common.constant.Constant;
import org.example.common.domain.Result;
import org.example.model.*;
import org.example.server.AppServer;
import org.example.utils.SapUtils;
import org.example.utils.VeDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.utils.Ewm;
@RestController
@RequestMapping({"/api"})
public class ApplicationTest {
    @Resource
    private Ewm ewm;
    @Resource
    private AppServer appServer;
    @Value("${GLNY_TOKEN}")
    private String GLNY_TOKEN;

    @GetMapping("test")
    @ApiOperation("测试")
    public List<Map<String,String>> test(HttpServletRequest request) {

        return appServer.test();
    }

    @GetMapping("setEwm")
    @ApiOperation("生成二维码")
    public String setEwm(HttpServletRequest request) {
        String flowNo = "16920108100000006";
        char data = appServer.calculateCheckDigit(flowNo);
        flowNo = flowNo+data;
        return flowNo;
    }

    @PostMapping("getGs1")
    @ApiOperation("生成gs1编码")
    public Result<String> getGs1(@RequestBody FinDicUnicf finDicUnicf) {
        Result<String> result = new Result<>();
        result.setCode(Constant.SUCCESS);
        String gs1 = appServer.getGs1(finDicUnicf);
        System.out.println("生成"+gs1);
        result.setData(gs1);
        return result;
    }
    @GetMapping("getEwm")
    @ApiOperation("获取二维码")
    public void getEwm(HttpServletResponse response, HttpServletRequest request){
        try{
           appServer.getFile(response,request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("getDataEwm")
    @ApiOperation("获取二维码")
    public void getDataEwm(HttpServletResponse response, HttpServletRequest request,@RequestParam("id") String id){
        try{
             appServer.getImg(response,request,id);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @GetMapping("getFinDicUnicf")
    @ApiOperation("获取FinDicUnicf")
    public Map<String,String> getFinDicUnicf(@RequestParam("id") String id){
        return appServer.getFinDicUnicf(id);
    }

    @GetMapping("setReuse")
    @ApiOperation("设置复用")
    public Result<String> setReuse(@RequestParam("id") String id){
        Result<String> result = new Result<>();
        result.setCode(Constant.SUCCESS);
        appServer.setReuse(id);
        return result;
    }

    @GetMapping("getPermission")
    @ApiOperation("获取permission")
    public Result<PurDicTablePermission> getPermission(@RequestParam("title") String title,@RequestParam("department") String department){
        Result<PurDicTablePermission> result = new Result<>();
        result.setCode(Constant.SUCCESS);
        result.setData(appServer.getPermission(title,department));
        return result;
    }

    @PostMapping("getMaterialInformation")
    @ApiOperation("获取物料信息")
    public Result<List<LogDicMaterialInformationTrue>> getMaterialInformation(@RequestBody AppPostModel map)
    {
        Result<List<LogDicMaterialInformationTrue>> result = new Result<>();
        try{
            map.setCharg("%"+map.getCharg()+"%");
            map.setMatnr("%"+map.getMatnr()+"%");
            result.setData(appServer.getMaterialInformation(map));
            result.setCode(Constant.SUCCESS);
        }catch (Exception e){
            result.setCode(Constant.FAIL);
            result.setMessage("查询失败");
            e.printStackTrace();
        }
        return result;
    }

    @ApiOperation("获取mb52表数据")
    @PostMapping("/getSAPMb52")
    public List<MB52> GetSAPContract(@RequestBody AppPostModel appPostModel) {
        List<MB52> ret = new ArrayList<>();
        try{
            Map<String,String> map = new HashMap<>();
            map.put("I_BUKRS","2410");
            map.put("I_CHARG",appPostModel.getCharg());
            map.put("I_MATNR",appPostModel.getMatnr());
            SapUtils sapUtils = new SapUtils();
            JCoTable jtab = sapUtils.createDataFile("ZFM_FPBI_INSPECTION_RESULT", "O_INSP_RESULT",map );
//            JCoTable jtab = sapUtils.createDataFile("ZFM_FPBI_MB52", "O_MB52",map );
            for (int i = 0; i < jtab.getNumRows(); i++) {
                jtab.setRow(i);
                System.out.println(jtab);
                MB52 c = new MB52();
                c.setMatnr(jtab.getString("MATNR"));//
                c.setCharg(jtab.getString("CHARG"));//
                c.setZspeno(jtab.getValue(22).toString());//
                c.setZspecno(jtab.getValue(21).toString());//

                ret.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
        return ret;
    }
    @ApiOperation("app登录接口")
    @PostMapping("appLogin")
    public Result<PdaAppUser> login(@RequestBody AppLogin appLogin){
        Result<PdaAppUser> result = new Result<>();
        if(!appLogin.getToken().equals(GLNY_TOKEN)){
            result.setCode(Constant.FAIL);
            result.setMessage("token错误");
            return result;
        }
        PdaAppUser userData = appServer.login(appLogin).getPdaAppUser();
        if(userData==null){
            result.setCode(Constant.FAIL);
            result.setMessage("登录失败");
        }else{
            result.setData(userData);
            result.setCode(Constant.SUCCESS);
        }
        return result;
    }
    @GetMapping("download/getApk/pda.apk")
    @ApiOperation("获取apk")
    public void getApk(HttpServletResponse response, HttpServletRequest request){
        try{
            appServer.getApk(response,request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @GetMapping("download/getApk/pdaV1_1.apk")
    @ApiOperation("获取apk")
    public void getApk2(HttpServletResponse response, HttpServletRequest request){
        try{
            appServer.getApk(response,request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @GetMapping("download/getApk")
    @ApiOperation("获取指定版本的apk")
    public void getApkTest(HttpServletResponse response, HttpServletRequest request, @RequestParam("version") String version){
        try{
            appServer.getApk2(response,request,version);
//            return ResponseEntity.ok("APK文件已成功下载。");
            System.out.println("APK文件已成功下载。");
        } catch (FileNotFoundException e) {
            System.out.println("未找到指定的APK文件。");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到指定的APK文件。");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("服务器内部错误。");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误。");
        }
    }
    @GetMapping("getMd5String")
    @ApiOperation("获取md5")
    public Result<String> getMd5String(@RequestParam("id") String id){
        Result<String> result = new Result<>();
        result.setCode(Constant.SUCCESS);
        String md5String = MD5.create().digestHex(id);
        result.setData(md5String);
        return result;
    }
}
