package com.example.yangxiaoguang.cordovademo;

import android.util.Log;

import org.apache.http.HttpEntity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by yangxiaoguang on 2016/12/6.
 * 文件下载功能
 */

public class FileController {

    private HttpController httpController;
    private String downloadurl;
    private String filePath;

    /**
     * 初始化 文件下载
     * @param url 下载http 地址
     * @param filepath 下载到的路径
     */
    public FileController(String url,String filepath)
    {
        httpController =new HttpController();
        downloadurl= url;
        filePath = filepath;

    }



    public Boolean streamDownLoadFile(String filename) {
        Log.i("下载地址", downloadurl);

        httpController.openRequest(downloadurl, httpController.REQ_METHOD_POST);
        httpController.sendRequest();
        HttpEntity httpEntity = httpController.getHttpResponse().getEntity();
        if (httpEntity == null)
            return false;
        InputStream inStream;
        ByteArrayOutputStream outStream;
        byte[] bufferfile = null;
        try {
            inStream = httpEntity.getContent();
            outStream = new ByteArrayOutputStream();
            Log.i("下载文件大小inStream:", String.valueOf(inStream.available()));
            int maxbuff = 1024 * 5000;
            byte[] buffer = new byte[maxbuff];
            int len = 0;

            if (inStream == null) {
                return false;
            }
            System.gc();
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            bufferfile = outStream.toByteArray();
            outStream.close();
            inStream.close();
            if (bufferfile == null)
                throw new Exception();
            if (bufferfile.length <=50) {
                Log.e("下载",new String (bufferfile));
                throw new Exception();

            }
            Log.i("下载文件大小:", String.valueOf(bufferfile.length));

            /***
             * 下载存到本地
             */
            File file = new File(filePath, filename);
            if (file.exists())
            {
                file.delete();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bufferfile);
            fileOutputStream.close();
            httpController.closeRequest();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            httpController.closeRequest();
            return false;
        }
    }


    /**
     * 解压文件
     * @param zipFile
     * @param folderPath
     * @return
     * @throws ZipException
     * @throws IOException
     */
    public  Boolean upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        //public static void upZipFile() throws Exception{
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d("upZipFile", "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finishs");
        return true;
    }



    /**
     * 40     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * 41     * @param baseDir 指定根目录
     * 42     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * 43     * @return java.io.File 实际的文件
     * 44
     */
    private  File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d("upZipFile", "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ret = new File(ret, substr);
            Log.d("upZipFile", "2ret = " + ret);
            return ret;
        }


        return  new File(baseDir, absFileName);
    }

}
