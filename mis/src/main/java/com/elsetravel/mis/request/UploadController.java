package com.elsetravel.mis.request;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.elsetravel.baseweb.request.RequestHeader;
import com.elsetravel.baseweb.request.RequestParam;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.NullUtil;
@Controller
@RequestMapping("/upload")
public class UploadController extends WebController{
	public RequestParam parseRequestParameter(HttpServletRequest request,boolean isEncrypt) throws Exception{
		ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
		servletFileUpload.setHeaderEncoding("UTF-8");//解决http报头乱码，即中文文件名乱码
		List<FileItem> items = servletFileUpload.parseRequest(request);
		
		
		RequestHeader requestHeader = new RequestHeader();
		UploadRequestBody requestBody = new UploadRequestBody();
		StringBuffer all_param_sb = new StringBuffer();
		StringBuffer input_param_sb = new StringBuffer();
		List<UploadFile> uploadFiles = new ArrayList<UploadFile>();
        for (Object item : items) {
            FileItem fileItem = (FileItem)item;
            String fName = fileItem.getFieldName();
            if(fileItem.isFormField()){
            	//普通字段，是json格式
            	String fVal = fileItem.getString("UTF-8");
            	
            	RequestUtil.fillRequestParamStr(requestHeader,requestBody,fName,fVal,all_param_sb,input_param_sb);
            	
            }else{
            	//文件流
            	UploadFile uploadFile = new UploadFile(fileItem);
            	uploadFiles.add(uploadFile);//文件流
            	if(all_param_sb.length() > 0){
            		all_param_sb.append("&");
    			}
            	all_param_sb.append(fName).append("=[Blob]");
            }
        }
        
        logger.info("parameter : "+all_param_sb.toString());
		requestBody.setUploadFiles(uploadFiles);
		logger.debug("total item count:"+(NullUtil.isEmpty(items) ? 0 : items.size())+",file item count:"+uploadFiles.size());
		
		return new RequestParam(requestHeader,requestBody,input_param_sb.toString());
	}
}
