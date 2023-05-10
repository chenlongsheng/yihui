package com.jeeplus.modules.starnet.util;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 流拷贝
 * <p>Title : FileUtil</p>
 * <p>Description : </p>
 * <p>DevelopTools : Eclipse_x64_v4.8.1</p>
 * <p>DevelopSystem : Windows 10</p>
 * <p>Company : org.wcy</p>
 * @author : WangChenYang
 * @date : 2018年9月25日 上午12:39:14
 * @version : 0.0.1
 */
public class FileCopyUtil {

    public static boolean createDirectory(String path) {
        File file = new File(path);
        if(!file.exists()) {
            file.mkdir();
        }
        return true;
    }

    /**
     * base64转File
     * @param base64
     * @param dest
     */
    public static boolean base64ToFile(String base64,File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //由于base64在传输过程中，+和/和=会被替换所以在解码前需要将base64还原成可用的base64
            base64 = base64.replaceAll(" ","+");
            base64 = base64.replaceAll("%2F","/");
            base64 = base64.replaceAll("%3D","=");
            //当使用springMVC时无需使用以上方法进行还原
            byte[] bytes = Base64.decodeBase64(base64.replace("\r\n", ""));
            bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
            bos = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] bts = new byte[1024];
            int len = -1;
            while((len = bis.read(bts)) != -1) {
                bos.write(bts,0,len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}