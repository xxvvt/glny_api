package org.example.server;

import cn.hutool.crypto.digest.MD5;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.example.dao.AppDao;
import org.example.model.*;
import org.example.utils.HttpUtil;
import org.example.utils.SapUtils;
import org.example.utils.VeDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("AppServer")
public class AppServer {
    @Resource private AppDao appDao;
    @Value("${file.path}")
    private String FILE_PATH;
    @Value("${file.apk-path}")
    private String APK_PATH;

    public List<Map<String,String>> test() {
        return appDao.getBomList();
    }

    //生成gs1编码
    public String getGs1(FinDicUnicf finDicUnicf) {
        try{
            StringBuffer gs1 = new StringBuffer("");
            gs1.append(finDicUnicf.getType());//拼接类型
            gs1.append("69201081");//拼接公司前缀
            FineDicDict dict = appDao.getBomListByCode("fin_dic_unicf_flow_no",finDicUnicf.getType());
            //流水号+1
            String flowNo = Integer.parseInt(dict.getValue())+1+"";
            dict.setValue(flowNo);
            //流水号在前面补0到8位
            for (int i = 0; i < 8-flowNo.length(); i++) {
                gs1.append("0");
            }
            gs1.append(flowNo);
            //更新流水号
            appDao.updateBomListByCode(dict);
            //计算校验位
            char checkDigit = calculateCheckDigit(gs1.toString());
            return gs1.toString()+checkDigit;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //生成校验位
    public  char calculateCheckDigit(String input) {
        int sum = 0;
        for (int i = 0; i < input.length(); i++) {
            int digit = Character.getNumericValue(input.charAt(i));
            int weight = ((i+1) % 2 == 0) ? 1 : 3; // 设置权重，奇数位为3，偶数位为1
            sum += digit * weight;
//            System.out.println("第"+i+"位；digit:"+digit+" weight:"+weight+" sum:"+sum);
        }

        int checkDigitValue = 10 - (sum % 10);
        return Character.forDigit(checkDigitValue % 10, 10); // 将个位数字转换为字符
    }

    //根据uid获取文件，返回二进制流
    public void getFile(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //从response中获取uid
        String uid = request.getParameter("uid");
        FileModel fileModel = appDao.getFile(uid);
        String src = "";
        if (fileModel!=null){
            src = fileModel.getSrc();
        }else{
            FinDicUnicf finDicUnicf = appDao.getUnicf(uid);
            src = setEwm(finDicUnicf);
        }
        File picFile = new File(FILE_PATH+src);
        //读取指定路径下面的文件
        InputStream in = new FileInputStream(picFile);
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        //创建存放文件内容的数组
        byte[] buff =new byte[1024];
        //所读取的内容使用n来接收
        int n;
        //当没有读取完时,继续读取,循环
        while((n=in.read(buff))!=-1){
            //将字节数组的数据全部写入到输出流中
            outputStream.write(buff,0,n);
        }
        //强制将缓存区的数据进行输出
        outputStream.flush();
        //关流
        outputStream.close();
        in.close();
    }

    public Map<String,String> getFinDicUnicf(String id){
        FinDicUnicf finDicUnicf = appDao.getUnicf(id);

        Map<String,String> map = new HashMap<>();
        map.put("id",finDicUnicf.getId());
        map.put("sscc",finDicUnicf.getSscc());
        map.put("lot",finDicUnicf.getLot());
        map.put("prod_date",getYearMonthDay(finDicUnicf.getProdDate()));
        map.put("explry",getYearMonthDay(finDicUnicf.getExplry()));
        map.put("content",finDicUnicf.getContent());
        map.put("quantity",finDicUnicf.getQuantity());
        map.put("cust_part_no",finDicUnicf.getCustPartNo());
        map.put("order_number",finDicUnicf.getOrderNumber());
        return map;
    }

    //获取字符串日期中的年月日
    public String getYearMonthDay(String date){
        String year = date.substring(2,4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);
        return year+month+day;
    }

    public void getImg(HttpServletResponse response, HttpServletRequest request,String id) throws IOException {
        HttpUtil.sendGet("http://localhost:8110/papi/getImg/"+id);
        String path = "http://localhost:8110/papi/getImg/"+id;
        System.out.println(path);

        File picFile = new File(path);
        //读取指定路径下面的文件
        InputStream in = new FileInputStream(picFile);
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        //创建存放文件内容的数组
        byte[] buff =new byte[1024];
        //所读取的内容使用n来接收
        int n;
        //当没有读取完时,继续读取,循环
        while((n=in.read(buff))!=-1){
            //将字节数组的数据全部写入到输出流中
            outputStream.write(buff,0,n);
        }
        //强制将缓存区的数据进行输出
        outputStream.flush();
        //关流
        outputStream.close();
        in.close();
    }

    // Java读取网络图片 BufferedImage
    public static void setImg(String path) {
        BufferedImage image = null;
        try {
            URL url = new URL("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1406480540,1698303361&fm=26&gp=0.jpg");
            image = ImageIO.read(url);

            ImageIO.write(image, "jpg", new File("C:\\Users\\asus\\Desktop\\copy.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("下载成功~");
    }


    //生成二维码
    public  String setEwm(FinDicUnicf finDicUnicf) throws UnsupportedEncodingException {
        // 设置 Data Matrix 码的数据
        //构建二维码内容
        StringBuffer sb = new StringBuffer("");//起始符得是FNC1但是FNC1在java中是不可见字符，所以用]d2代替
        String produceDate = finDicUnicf.getProdDate();
        String expireDate = finDicUnicf.getExplry();
        sb.append("(00)").append(finDicUnicf.getSscc());//拼接sscc

        sb.append("(10)").append(finDicUnicf.getLot());//拼接lot
        sb.append("<GS>"); // 添加占位符
        sb.append("(11)").append(getYearMonthDay(produceDate));//拼接生产日期
        sb.append("(17)").append(getYearMonthDay(expireDate));//拼接过期日期
        sb.append("(02)").append(finDicUnicf.getContent());//拼接销售单元GTIN
        sb.append("(37)").append(finDicUnicf.getQuantity());//拼接数量
        sb.append("<GS>"); // 添加占位符
        sb.append("(241)").append(finDicUnicf.getCustPartNo());//拼接客户零件号
        sb.append("<GS>"); // 添加占位符
        sb.append("(400)").append(finDicUnicf.getOrderNumber());//拼接订单号

        String data = sb.toString();
        //将字符串中的占位符替换为GS分隔符
        data = data.replaceAll("<GS>","\u001D");

        // 设置 Data Matrix 码的宽度和高度（像素）
        int width = 150; // 0.743 mm 转换为像素
        int height = 150; // 0.743 mm 转换为像素
        int margin = 5; // 0.743 mm 转换为像素
        String charset = "ISO-8859-1"; // 编码格式
        data = new String(data.getBytes(charset), charset);
        System.out.println("data:"+data);
        // 设置 Data Matrix 码的错误校正级别（C 级别）
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;

        // 设置 Data Matrix 码的模块样式
        SymbolShapeHint shape = SymbolShapeHint.FORCE_SQUARE;
        EncodeHintType errorCorrectionType = EncodeHintType.ERROR_CORRECTION;
        // 设置生成参数
        EncodeHintType hintType = EncodeHintType.MARGIN;
        // 设置 Data Matrix 码的错误校正级别（C 级别）

        EncodeHintType shapeType = EncodeHintType.DATA_MATRIX_SHAPE;
        EncodeHintType charsetType = EncodeHintType.CHARACTER_SET;

        // 创建生成参数
        EncodeHintType[] hintTypes = { hintType,errorCorrectionType, shapeType, charsetType };
        Object[] hintValues = { margin, errorCorrectionLevel, shape, charset };
        EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        for (int i = 0; i < hintTypes.length; i++) {
            hints.put(hintTypes[i], hintValues[i]);
        }
        hints.put(errorCorrectionType, ErrorCorrectionLevel.M);
        try {
            // 使用 ZXing 创建 Data Matrix 码的 BitMatrix
            BitMatrix bitMatrix = new DataMatrixWriter().encode(data, BarcodeFormat.DATA_MATRIX, width, height, hints);

            // 创建 BufferedImage 并将 BitMatrix 绘制到其中
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }
            String src = "/upload/DataMatrix_"+finDicUnicf.getId()+".png";
            String path = FILE_PATH + src;
            System.out.println(path);
            // 将 Data Matrix 码保存为图片文件
            File outputFile = new File(path);
            ImageIO.write(image, "png", outputFile);
            //将文件信息保存到数据库
            FileModel fileModel = new FileModel();
            fileModel.setUid(finDicUnicf.getId());
            fileModel.setSrc(src);
            //获取UUID
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileModel.setId(uuid);
            fileModel.setFileName("DataMatrix_"+finDicUnicf.getId()+".png");
            //获取时间字符串
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fileModel.setAddtime(sdf.format(new Date()));
            appDao.insertFile(fileModel);
            System.out.println("Data Matrix 码已生成并保存为 "+src);
            return src;
        } catch (IOException  e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setDataEwm(String gs1Data) throws UnsupportedEncodingException {

        try {
            gs1Data = gs1Data.replaceAll("FNC1", new String(new byte[] {0x1d}));

            // 设置 Data Matrix 码的宽度和高度（像素）
            int width = 150;
            int height = 150;
            int margin = 5;
            String charset = "gbk";

            // 设置 Data Matrix 码的错误校正级别（C 级别）
            ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;

            // 设置 Data Matrix 码的模块样式
            SymbolShapeHint shape = SymbolShapeHint.FORCE_SQUARE;

            // 设置生成参数
            EncodeHintType hintType = EncodeHintType.MARGIN;
            EncodeHintType errorCorrectionType = EncodeHintType.ERROR_CORRECTION;
            EncodeHintType shapeType = EncodeHintType.DATA_MATRIX_SHAPE;
            EncodeHintType charsetType = EncodeHintType.CHARACTER_SET;

            // 创建生成参数
            EncodeHintType[] hintTypes = { hintType, errorCorrectionType, shapeType, charsetType };
            Object[] hintValues = { margin, errorCorrectionLevel, shape, charset };

            EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            for (int i = 0; i < hintTypes.length; i++) {
                hints.put(hintTypes[i], hintValues[i]);
            }

            // 使用 ZXing 创建 Data Matrix 码的 BitMatrix
            BitMatrix bitMatrix = new DataMatrixWriter().encode(gs1Data, BarcodeFormat.DATA_MATRIX, width, height, hints);
            // 创建 BufferedImage 并将 BitMatrix 绘制到其中
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            String src = "/upload/GS1DataMatrix.png";
            String path = FILE_PATH + src;

            // 将 Data Matrix 码保存为图片文件
            File outputFile = new File(path);
            ImageIO.write(image, "png", outputFile);

            System.out.println("GS1 DataMatrix 已生成并保存为 " + src);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setReuse(String id){
        try {
            FinDicUnicf finDicUnicf = appDao.getUnicf(id);
            Integer number = finDicUnicf.getNumber();
            String gs1 = finDicUnicf.getSscc();
            FineDicDict dict = appDao.getBomListByCode("fin_dic_unicf_flow_no",finDicUnicf.getType());
            //String dict.getValue()转int
            String qz = finDicUnicf.getType()+"69201081";
            System.out.println("需要生成"+number+"个二维码");
            for (int i = 0; i < number; i++) {
                  if (i>0){
                      StringBuilder data = new StringBuilder(qz);
                      //流水号+1
                      String flowNo = Integer.parseInt(dict.getValue())+1+"";
                      dict.setValue(flowNo);
                      //流水号在前面补0到8位
                      for (int j = 0; j < 8-flowNo.length(); j++) {
                          data.append("0");
                      }
                      data.append(flowNo);
                      //更新流水号
                      appDao.updateBomListByCode(dict);
                      //计算校验位
                      char checkDigit = calculateCheckDigit(data.toString());
                      data.append(checkDigit);
                      gs1 = data.toString();
                      System.out.println("生成"+gs1);
                      dict.setValue(flowNo);
                  }
                  appDao.insertReuse(UUID.randomUUID().toString().replaceAll("-", ""),gs1,id);
            }
            //更新流水号
            appDao.updateBomListByCode(dict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //通过表名获取对应的permission参数组装
    public PurDicTablePermission getPermission(String title,String department){
        PurDicTablePermission permission = new PurDicTablePermission();
        //将字符串department 按 , 分割成数组
        String[] split = department.split(",");
        for (int i = 0; i < split.length; i++) {
            permission = appDao.getPermission(title,split[i]);
            if (permission!=null){
                break;
            }
        }
        List<PurDicTablePermissionParams> params = appDao.getPermissionParams(permission.getId());
        permission.setParams(params);
        return permission;
    }

    //获取物料信息
    public List<LogDicMaterialInformationTrue> getMaterialInformation(AppPostModel appPostModel) throws JCoException {
        LogDicMaterialInformationTrue file = appDao.getPrintTitle();

        List<LogDicMaterialInformationTrue> list = appDao.getMaterialInformation(appPostModel.getMatnr(),appPostModel.getCharg());
        if (list.size()==0){
            list = appDao.getMaterialInformationOldTable(appPostModel.getMatnr(),appPostModel.getCharg());
        }
        for (int i = 0; i < list.size(); i++) {
            LogDicMaterialInformationTrue item = list.get(i);
            if(file!=null){
                item.setFileName(file.getFileName());
                item.setFileNo(file.getFileNo());
            }
            Map<String,String> map = new HashMap<>();
            map.put("I_BUKRS","2410");
            map.put("I_CHARG",item.getCharg());
            map.put("I_MATNR",item.getMatnr());
            SapUtils sapUtils = new SapUtils();
            try{
                JCoTable jtab = sapUtils.createDataFile("ZFM_FPBI_MB52", "O_MB52",map );
//            JCoTable jtab = createDataFile("ZFM_FPBI_MB52", "O_MB52",map );
                if (jtab.getNumRows()>0){
                    jtab.setRow(0);
                    item.setInsme(getStringFromTable(jtab,"INSME"));//待验数量
                    item.setSpeme(getStringFromTable(jtab,"SPEME"));//不合格数量
                    item.setLabst(getStringFromTable(jtab,"LABST"));//合格数量
                }
            }catch (Exception e){
                System.out.println("ZFM_FPBI_MB52接口调用失败");
                e.printStackTrace();
            }
            try{
                sapUtils = new SapUtils();
                JCoTable jtab2 = sapUtils.createDataFile("ZFM_FPBI_INSPECTION_RESULT", "O_INSP_RESULT",map );
                if (jtab2.getNumRows()>0){
                    jtab2.setRow(0);
                    String zspecno = jtab2.getValue(21).toString();
                    String zspeno = jtab2.getValue(22).toString();
                    item.setZspeno(zspeno);//请验批号
                    item.setZspecno(zspecno);//检验编号
                }
            }catch (Exception e){
                System.out.println("ZFM_FPBI_INSPECTION_RESULT接口调用失败");
                e.printStackTrace();
            }
        }
        return list;
    }

    public  String getStringFromTable(JCoTable table, String fieldName) {
        try {
            table.getString("MATNR");
            return table.getString(fieldName);
        } catch (Exception e) {
            System.out.println("字段不存在"+fieldName);
            e.printStackTrace();
            // 如果字段不存在，返回一个默认值或者空字符串，这里返回空字符串
            return "";
        }
    }

    /**
     * APP登录接口
     * @param appLogin
     * @return
     */
    public AppLogin login(AppLogin appLogin) {

        try{
            String password = MD5.create().digestHex(appLogin.getPassword());
            PdaAppUser data = appDao.appLogin(appLogin.getUsername(),password);
            if (data!=null ){
                appLogin.setPdaAppUser(data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return appLogin;
    }

    //返回指定目录文件的文件流
    public void getApk(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //从response中获取uid

        File picFile = new File(APK_PATH+"QRCodeTool.apk");
        //读取指定路径下面的文件
        InputStream in = new FileInputStream(picFile);
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        //创建存放文件内容的数组
        byte[] buff =new byte[1024];
        //所读取的内容使用n来接收
        int n;
        //当没有读取完时,继续读取,循环
        while((n=in.read(buff))!=-1){
            //将字节数组的数据全部写入到输出流中
            outputStream.write(buff,0,n);
        }
        //强制将缓存区的数据进行输出
        outputStream.flush();
        //关流
        outputStream.close();
        in.close();
    }
    //获取指定的apk
    public void getApk2(HttpServletResponse response, HttpServletRequest request,String version) throws IOException {

        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("pda"+version+".apk","UTF-8"));
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        System.out.println(APK_PATH+"QRCodeTool"+version+".apk");
        File picFile = new File(APK_PATH+"QRCodeTool"+version+".apk");
        //读取指定路径下面的文件
        InputStream in = new FileInputStream(picFile);
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        //创建存放文件内容的数组
        byte[] buff =new byte[1024];
        //所读取的内容使用n来接收
        int n;
        //当没有读取完时,继续读取,循环
        while((n=in.read(buff))!=-1){
            //将字节数组的数据全部写入到输出流中
            outputStream.write(buff,0,n);
        }

        //强制将缓存区的数据进行输出
        outputStream.flush();
        //关流
        outputStream.close();
        in.close();
    }
}
