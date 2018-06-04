package com.xiesange.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;


public class FileUtil {
    public FileUtil() {
    }

    public static final String FILE_ENCODING = getFileEncoding();
    public static final String RELATIVE_FLAG = "//";
    
    private static Logger logger = LogUtil.getLogger(FileUtil.class);

    /**
     * 得到文件系统的字符集描述，若该文件系统的字符集描述为GBK则返回GB2312, 若字符集描述为ISO8859_1则返回ISO-8859-1。
     *
     * @return 字符集描述
     */
    public static String getFileEncoding() {
        String str = System.getProperty("file.encoding");
        if (null == str) {
            return "";
        }
        str = str.trim().toUpperCase();
        if (str.equals("GBK")) {
            str = "GB2312";
        } else if (str.equals("ISO8859_1")) {
            str = "ISO-8859-1";
        }
        // CBossLogUtil.getLogger("").debug(str);
        return str;
    }

    public static String[] getFileList(String sDir, final String filterName) {
        File fileDir = new File(sDir);
        return fileDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String rr = new File(name).toString();
                return rr.endsWith(filterName);
            }
        });
    }
    
    public static File[] getFileList(String sDir) {
        File fileDir = new File(sDir);
        return fileDir.listFiles();
    }

    /**
     * 新建目录
     *
     * @param folderPath String 如 c:/fqf
     * @return boolean
     */
    public static void newFolder(String folderPath) throws Exception {
        java.io.File myFilePath = new java.io.File(folderPath);
        if (!myFilePath.exists()) {
            myFilePath.mkdir();
        }
    }

    /**
     * 新建文件
     *
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     * @return boolean
     */
    public static void newFile(String filePathAndName) throws Exception {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
        } catch (Exception e) {
            throw new Exception("No such file or directory[" + filePathAndName + "]", e);
        }
    }

    /**
     * 新建文件
     *
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     * @param fileContent     String 文件内容
     */
    public static void newFile(String filePathAndName, String fileContent) throws Exception {
        FileWriter resultFile = null;
        PrintWriter myFile = null;
        try {
            File myFilePath = new File(filePathAndName);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            resultFile = new FileWriter(myFilePath);
            myFile = new PrintWriter(resultFile);
            myFile.print(fileContent);
            resultFile.close();
        } finally {
            try {
                if (resultFile != null) resultFile.close();
            } catch (Exception e) {
            }
            try {
                if (myFile != null) myFile.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 新建文件
     *
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     * @param fileContent     String 文件内容
     * @return boolean
     */
    public static void newFile(String filePathAndName, byte[] fileContent) throws Exception {
        FileOutputStream resultFile = null;
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            resultFile = new FileOutputStream(myFilePath);
            //PrintWriter myFile = new PrintWriter(resultFile);
            resultFile.write(fileContent);
            resultFile.close();
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "[" + filePathAndName + "]", e);
        } finally {
            try {
                resultFile.close();
            } catch (Exception ex) {
                // do nothing
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     * @return boolean
     */
    public static void delFile(String filePathAndName) throws Exception {
    		if(NullUtil.isEmpty(filePathAndName)){
    			return;
    		}
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();
            logger.debug("______________delete file:"+filePathAndName);
        
    }

    /**
     * 删除文件夹
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            ;;//SWTUtil.alert(e.getMessage() + "[" + folderPath + "]");
        }
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            ;//SWTUtil.alert(e.getMessage() + "[" + oldPath + "," + newPath + "]");
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            ;//SWTUtil.alert(e.getMessage() + "[" + oldPath + "," + newPath + "]");
        }
    }

    /**
     * 批量移动文件
     *
     * @param oldFiles
     * @param srcPath
     */
    public static void moveFile(String[] oldFiles, String srcPath, String destPath) {
        if (oldFiles == null
                || oldFiles.length == 0
                || srcPath == null
                || srcPath.trim().length() == 0
                || destPath == null
                || destPath.trim().length() == 0) return;
        for (int i = 0; i < oldFiles.length; i++) {
            String srcFile = null;
            if (srcPath.endsWith(File.separator))
                srcFile = srcPath + oldFiles[i];
            else
                srcFile = srcPath + oldFiles[i];
            String destFile = null;
            if (destPath.endsWith(File.separator))
                destFile = destPath + oldFiles[i];
            else
                destFile = destPath + File.separator + oldFiles[i];
            moveFile(srcFile, destFile);
        }
    }

    /**
     * 批量移动文件
     *
     * @param oldFiles
     * @param destPath
     */
    public static void moveFile(String[] oldFiles, String destPath) {
        if (oldFiles == null
                || oldFiles.length == 0
                || destPath == null
                || destPath.trim().length() == 0) return;
        for (int i = 0; i < oldFiles.length; i++) {
            String tmp = oldFiles[i].substring(oldFiles[i].lastIndexOf(System.getProperty("file.seperator")) + 1);
            String destFile = null;
            if (destPath.endsWith(File.separator))
                destFile = destPath + tmp;
            else
                destFile = destPath + File.separator + tmp;
            moveFile(oldFiles[i], destFile);
        }
    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static void moveFile(String oldPath, String newPath) {
        File f = new File(oldPath);
        f.renameTo(new File(newPath));
        // copyFile(oldPath, newPath);
        // delFile(oldPath);
    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    public static void main(String[] args) throws Exception {
//      FileUtil f = new FileUtil();
//      f.moveFile("E:\\ftp_down\\DELAY_STA20060409791.999",
//              "E:\\history\\DELAY_STA20060409791.999");
        FileUtil.newFile("C:\\test_test_test.txt", "");
    }

    /**
     * 得到指定目录下以指定字符串开头的文件名列表 taojc add 2006-1-26
     *
     * @param sDir        String
     * @param filterBegin String
     * @return String
     */
    public static String[] getFileListStartsWithFilter(String sDir,
                                                       final String filterBegin) {
        File fileDir = new File(sDir);
        return fileDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String rr = new File(name).toString();
                return (rr.startsWith(filterBegin));
            }
        });
    }

    public static String getFileContent(String fileName, String charset) throws Exception {
        try {
            return new String(getFileContentBytes(fileName), charset);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage() + "[" + fileName + "]", ex);
        }
    }

    /**
     * @param filePathAndName String
     * @return long
     * @throws Exception
     */
    public static long getFileSize(String filePathAndName) throws Exception {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File file = new java.io.File(filePath);
            return file.length();
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "[" + filePathAndName + "]", e);
        }
    }

    public static String getFileContent(String fileName) throws Exception {
        return new String(getFileContentBytes(fileName));
    }

    public static String getFileContent(File fileName) throws Exception {
        try {
            return new String(getFileContentBytes(new FileInputStream(fileName)));
        } catch (Exception ex) {
            throw new Exception(ex.getMessage() + "[" + fileName + "]", ex);
        }
    }

    public static byte[] getFileContentBytes(String fileName) throws Exception {
        try {
            return getFileContentBytes(new FileInputStream(fileName));
        } catch (Exception ex) {
            throw new Exception(ex.getMessage() + "[" + fileName + "]", ex);
        }
    }

    public static byte[] getFileContentBytes(InputStream is) throws Exception {
        try {
            ByteArrayOutputStream byOut = new ByteArrayOutputStream();
            byte[] datas = new byte[1024];
            int len = -1;
            while ((len = is.read(datas)) > 0) {
                byOut.write(datas, 0, len);
            }
            return byOut.toByteArray();
        } finally {
            is.close();
        }
    }

    public static File checkDirectoryExists(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("路径" + path + "不存在!");
        }
        return file;
    }

    public static void appendFile(String fileName, String content) throws Exception {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName, true);
            fileWriter.write(content);
            fileWriter.flush();
        } catch (java.lang.Exception e) {
            throw new Exception(e.getMessage() + "[" + fileName + "]", e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (Exception e) {
                // do nothing
            }
        }
    }
    public static void writeFile(String fileName, String content) throws Exception {
        if (NullUtil.isEmpty(fileName) || content == null)
        {
            return;
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.write(content);
            fileWriter.flush();
        } catch (java.lang.Exception e) {
            throw new Exception(e.getMessage() + "[" + fileName + "]", e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (Exception e) {
            }
        }
    }
    

    public static void makeDir(String dir) throws Exception {
        try {
            File f = new File(dir);
            //2008-09-08 zengxr f.mkdir to f.mkdirs
            f.mkdirs();
        } catch (java.lang.Exception e) {
            throw new Exception(e.getMessage() + "[" + dir + "]", e);
        }
    }

    /*public static InputStream getStream(String fileName) throws Exception {
        InputStream in;
        try {
            in = new FileInputStream(fileName);
        } catch (FileNotFoundException f) {
            ;//SWTUtil.print("找不到文件[" + fileName + "],使用class.getResourceAsStream的方式查询文件");
            in = FileUtil.class.getResourceAsStream("/" + fileName);
        }
        if (in == null) throw new Exception("使用两种方式查询文件都找不到文件[" + fileName + "]");
        return in;
    }*/

    public static String getAbsolutePath(String baseDir, String path) {
        if (baseDir == null) baseDir = System.getProperty("user.dir");
        if (path.startsWith(RELATIVE_FLAG)) {
            return baseDir + File.separator + path.substring(RELATIVE_FLAG.length());
        }
        return path;
    }

    public static File createFile(String filePath) throws Exception{
        filePath = filePath.replace("\\", "/");
        
        String dir = filePath.substring(0, filePath.lastIndexOf("/"));
        File fold = new File(dir);
        File file = new File(filePath);
        if (!fold.exists()) {
            fold.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
    public static boolean isFile(String path){
        File file = new File(path);
        return file.isFile();
    }
    public static InputStream getFileInputStream(String path) throws FileNotFoundException{
        if(NullUtil.isEmpty(path)){
        	return null;
        }
    	InputStream is = null;
        String classpathPrefix = "classpath:";
        if(path.startsWith(classpathPrefix)){
        	is = FileUtil.class.getResourceAsStream(path.substring(classpathPrefix.length()));
        }else{
        	is = new FileInputStream(path);
        }
        return is;
    }
    
    /**
     * @Description: 资源是否存在
      * @author : wuyj
      * @date : 2012-4-19  
      * @param path,资源路径，可以是绝对路径(以C: D:等开头)，也可以是相对于classpath的相对路径
      * @return
     */
    public static boolean isFileExist(String path){
        return new File(path).exists();
    }
    
    /**
     * 
     * @param cpPath,如果"classpath:"开头的表示是相对于classpath下的路径
     * @return
     * @throws Exception
     * @author Wilson 
     * @date 下午7:58:26
     */
    public static Properties loadProperties(String cpPath) throws Exception
    {
        InputStream is = getFileInputStream(cpPath);
        if (is == null){
            return null;
        }
        Properties prop = loadProperties(is);
        is.close();
        return prop;
    }
    public static Properties loadProperties(InputStream is) throws Exception
    {
        if (is == null){
            return null;
        }
        
        Properties prop = new Properties();
        prop.load(new InputStreamReader(is,"UTF-8"));
        return prop;
    }
    
    public static File newFile(String filePath,InputStream inStream) throws Exception{
    	logger.debug("=================file encoding:"+getFileEncoding());
    	
    	File file = new File(filePath);
        if(!file.exists()){
        	file = FileUtil.createFile(filePath);
        }
        
        ByteArrayOutputStream byOut = new ByteArrayOutputStream();
        byte[] datas = new byte[1024];
        int len = -1;
        while ((len = inStream.read(datas)) > 0) {
            byOut.write(datas, 0, len);
        }
    	FileOutputStream fos = new FileOutputStream(file);
    	byOut.writeTo(fos);
    	
    	byOut.close();
        fos.close();
        
        return file;
    }
    /** 
     * 从网络Url中下载文件 
     * @param urlStr 
     * @param fileName 
     * @param savePath 
     * @throws IOException 
     */  
    public static void  downloadFromUrl(String urlStr,String fileName,String savePath) throws Exception{  
        URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
                //设置超时间为3秒  
        conn.setConnectTimeout(3*1000);  
        //防止屏蔽程序抓取而返回403错误  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
  
        //得到输入流  
        InputStream inputStream = conn.getInputStream();    
        //获取自己数组  
        byte[] getData = getFileContentBytes(inputStream);    
  
        //文件保存位置  
        File saveDir = new File(savePath);  
        if(!saveDir.exists()){  
            boolean result = saveDir.mkdir();  
            System.out.println(result);
        }
        
        File file = new File(saveDir+File.separator+fileName);      
        FileOutputStream fos = new FileOutputStream(file);       
        fos.write(getData);   
        if(fos!=null){  
            fos.close();    
        }  
        if(inputStream!=null){  
            inputStream.close();  
        }  
        
        logger.debug("download success : ["+url+"] to ["+file.getAbsolutePath()+"]");
    } 
    
    
    /** 
     * 解压缩zip包 
     * @param zipFilePath zip文件的全路径 
     * @param unzipFilePath 解压后的文件保存的路径 
     * @param includeZipFileName 解压后的文件保存的路径是否包含压缩文件的文件名。true-包含；false-不包含 
     */  
    public static void unzip(String zipFilePath, String unzipFilePath, boolean includeZipFileName) throws Exception  
    {  
        if (NullUtil.isEmpty(zipFilePath) || NullUtil.isEmpty(unzipFilePath))  
        {  
            return;            
        }  
        File zipFile = new File(zipFilePath);  
        //如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径  
        if (includeZipFileName)  
        {  
            String fileName = zipFile.getName();  
            if (NullUtil.isNotEmpty(fileName))  
            {  
                fileName = fileName.substring(0, fileName.lastIndexOf("."));  
            }  
            unzipFilePath = unzipFilePath + File.separator + fileName;  
        }  
        //创建解压缩文件保存的路径  
        File unzipFileDir = new File(unzipFilePath);  
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory())  
        {  
            unzipFileDir.mkdirs();  
        }  
          
        //开始解压  
        ZipEntry entry = null;  
        String entryFilePath = null, entryDirPath = null;  
        File entryFile = null, entryDir = null;  
        int index = 0, count = 0, bufferSize = 1024;  
        byte[] buffer = new byte[bufferSize];  
        BufferedInputStream bis = null;  
        BufferedOutputStream bos = null;  
        ZipFile zip = new ZipFile(zipFile);  
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zip.entries();  
        //循环对压缩包里的每一个文件进行解压       
        while(entries.hasMoreElements())  
        {  
            entry = entries.nextElement();  
            //构建压缩包中一个文件解压后保存的文件全路径  
            entryFilePath = unzipFilePath + File.separator + entry.getName();  
            //构建解压后保存的文件夹路径  
            index = entryFilePath.lastIndexOf(File.separator);  
            if (index != -1)  
            {  
                entryDirPath = entryFilePath.substring(0, index);  
            }  
            else  
            {  
                entryDirPath = "";  
            }             
            entryDir = new File(entryDirPath);  
            //如果文件夹路径不存在，则创建文件夹  
            if (!entryDir.exists() || !entryDir.isDirectory())  
            {  
                entryDir.mkdirs();  
            }  
              
            //创建解压文件  
            entryFile = new File(entryFilePath);  
            if (entryFile.exists())  
            {  
                //检测文件是否允许删除，如果不允许删除，将会抛出SecurityException  
                SecurityManager securityManager = new SecurityManager();  
                securityManager.checkDelete(entryFilePath);  
                //删除已存在的目标文件  
                entryFile.delete();   
            }  
              
            //写入文件  
            bos = new BufferedOutputStream(new FileOutputStream(entryFile));  
            bis = new BufferedInputStream(zip.getInputStream(entry));  
            while ((count = bis.read(buffer, 0, bufferSize)) != -1)  
            {  
                bos.write(buffer, 0, count);  
            }  
            bos.flush();  
            bos.close();
            zip.close();
        }  
    }
    
    /**
     * 读取zip文件中的文件内容,拼到一个字符串中
     * @param file
     * @return
     * @throws Exception
     * @author Wilson 
     * @date 下午5:25:38
     */
    public static String readZipFile(String file) throws Exception {  
        ZipFile zf = new ZipFile(file);  
        InputStream in = new BufferedInputStream(new FileInputStream(file));  
        ZipInputStream zin = new ZipInputStream(in);  
        ZipEntry ze;
        StringBuffer sb = new StringBuffer();
        while ((ze = zin.getNextEntry()) != null) {  
            if (!ze.isDirectory()) {
                long size = ze.getSize();  
                if (size > 0) {  
                    BufferedReader br = new BufferedReader(  
                            new InputStreamReader(zf.getInputStream(ze),"UTF-8"));  
                    String line;  
                    while ((line = br.readLine()) != null) {  
                    	sb.append(line).append("\n");
                    }  
                    br.close();  
                }  
            }  
        }  
        zin.closeEntry();
        zin.close();
        zf.close();
        return sb.toString();
    }
    
    
}
