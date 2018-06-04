package com.xiesange.core.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 专门处理Excel的工具类
 * @author wuyujie Jan 11, 2015 8:45:35 PM
 *
 */
public class ExcelUtil {
	public static void main(String[] args) throws Exception {
		//InputStream is = ExcelUtil.class.getResourceAsStream("/template_file/product_template.xslx");
		//readExcel2007(is,1,null);
		
		
		String path = "C:\\Users\\Think\\Desktop\\temp\\message\\test.xls";
		List<ExcelHeaderCell> headers = ClassUtil.newList();
		headers.add(new ExcelHeaderCell("name","客户",250));
		headers.add(new ExcelHeaderCell("prod","订购产品",250));
		headers.add(new ExcelHeaderCell("memo","备注",150));
		List<ExcelBodyRow> datalist = ClassUtil.newList();
		for(int i=0;i<2;i++){
			datalist.add(new ExcelBodyRow()
							.addCell("name", "吴宇杰\n\r13588830404\r\n杭州市拱墅区万家花城")
							.addCell("prod", "梭子蟹(5两),1斤,￥28/斤*2=￥56\r\n梭子蟹(5两),1斤,￥28/斤*2=￥56")
			);
		}
		
		generate(path,headers,datalist,null);
		System.out.println("------------------");
	}
	
	public static void generate(String filePath,List<ExcelHeaderCell> headers,List<ExcelBodyRow> datalist,Integer fontSize) throws Exception{
		// 新建一输出文件流
		FileOutputStream fileOS = new FileOutputStream(filePath);
		generate(fileOS,headers,datalist,fontSize);
		fileOS.flush();
		
		// 操作结束，关闭文件
		fileOS.close();
	}
	public static void generate(OutputStream fileOS,List<ExcelHeaderCell> headers,List<ExcelBodyRow> datalist,Integer fontSize) throws Exception{
		generate(fileOS,headers,datalist,null,fontSize);
	};
	
	/**
	 * 生成excel
	 * @author wuyujie Feb 9, 2015 12:36:10 PM
	 * @param fileOS，需要生成excel的输出流
	 * @param headers，标题栏
	 * @param datalist，List<List>数据栏，list中的元素都是一个List<String>，存放每一个单元格的数据
	 * @param totals,合计栏，list中的元素都是一个List<String>，存放每一个单元格的合计，如果该栏不需要合计就为空
	 * @throws Exception
	 */
	public static void generate(OutputStream fileOS,List<ExcelHeaderCell> headers,List<ExcelBodyRow> datalist,List<String> totals,Integer fontSize) throws Exception{
		// 创建新的Excel 工作簿
		// 在Excel工作簿中建一工作表，其名为缺省值
		HSSFWorkbook WORK_BOOK = new HSSFWorkbook();
		HSSFSheet sheet = WORK_BOOK.createSheet();
		List<HSSFCellStyle> bodyCellStyles = ClassUtil.newList();
		
		HSSFFont font = null;
		if(fontSize != null){
			font = WORK_BOOK.createFont(); 	
			font.setFontName("宋体");
	        font.setFontHeightInPoints(fontSize.shortValue());// 设置字体大小
		}		
		
		//如果有传入标题栏则创建第一行标题行
		if(NullUtil.isNotEmpty(headers)){
			HSSFRow titleRow = sheet.createRow((short)0);
			titleRow.setHeightInPoints(20);
			
			HSSFCellStyle titleCellStyle = createHeaderCellStyle(WORK_BOOK,font);
			for(int i=0;i<headers.size();i++){
				HSSFCell titleCell = titleRow.createCell(i);
				titleCell.setCellStyle(titleCellStyle);
				titleCell.setCellValue(headers.get(i).getName());
				if(headers.get(i).width != null){
					//设置列宽度,字符数=(像素-5)/7,但excel的列宽度是1/256的字符宽度，所以要乘以256
					sheet.setColumnWidth(i, ((headers.get(i).width-5)/7)*256);
				}
				
				//bodycell其实跟列属性挂钩，每行都是一样的，所以可以先创建好
				bodyCellStyles.add(createBodyCellStyle(WORK_BOOK,headers.get(i),font));
			}
		}
		
		//创建内容体
		if(NullUtil.isNotEmpty(datalist)){
			ExcelHeaderCell headerCell = null;
			Object bodyValue = null;
			for(int i=0;i<datalist.size();i++){
				ExcelBodyRow bodyCellValues = datalist.get(i);
				HSSFRow bodyRow = sheet.createRow(i+1);
				if(bodyCellValues.getHeight() != null){
					bodyRow.setHeightInPoints(bodyCellValues.getHeight());
				}
				
				HSSFCellStyle bodyCellStyle = null;
				for(int k=0;k<headers.size();k++){
					headerCell = headers.get(k);
					bodyValue = bodyCellValues.getCell(headerCell.getCode());
					HSSFCell bodyCell = bodyRow.createCell(k);
					if(bodyValue == null){
						bodyCell.setCellValue("");
					}else{
						bodyCell.setCellValue(String.valueOf(bodyValue));
					}
					bodyCellStyle = bodyCellStyles.get(k);
					bodyCell.setCellStyle(bodyCellStyle);
				}
			}
		}
		if(NullUtil.isNotEmpty(totals)){
			HSSFCellStyle totalCellStyle = createTotalCellStyle(WORK_BOOK);
			HSSFRow totalRow = sheet.createRow(sheet.getLastRowNum()+1);//最后一行
			totalRow.setHeightInPoints(20);
			for(int i=0;i<totals.size();i++){
				HSSFCell totalCell = totalRow.createCell(i);
				totalCell.setCellStyle(totalCellStyle);
				totalCell.setCellValue(totals.get(i));
				/*if(headers.get(i).width != null){
					//设置列宽度,字符数=(像素-5)/7,但excel的列宽度是1/256的字符宽度，所以要乘以256
					sheet.setColumnWidth(i, ((headers.get(i).width-5)/7)*256);
				}*/
			}
		}
		
		// 把相应的Excel 工作簿存盘
		WORK_BOOK.write(fileOS);
	};
	
	/**
     * 默认从第一行开始读取数据，并读取所有有效的列
     * @author wuyujie Feb 11, 2015 7:02:07 AM
     * @param filePath,解析的文件路径，支持xls和xlsx
     * @return
     * @throws IOException
     */
	public static List<String[]> readExcel(String filePath) throws Exception {
		return readExcel(filePath,null,null);
	}
	/**
     * 
     * @author wuyujie Feb 11, 2015 7:02:07 AM
     * @param filePath,解析的文件路径，支持xls和xlsx
     * @param startRow，指定从哪一行开始解析，默认是0从第一行开始解析。如果有些excel存在标题列可以指定1，从第二行开始
     * @param colCount,指定一共要解析几列，默认是所有有效的列
     * @return
     * @throws IOException
     */
	public static List<String[]> readExcel(String filePath,Integer startRow,Integer colCount) throws Exception {
		InputStream is = new FileInputStream(filePath);
		boolean is2007 = filePath.endsWith(".xlsx");
		List<String[]> result = is2007 ? readExcel2007(is,startRow,colCount) : readExcel(is,startRow,colCount);
		is.close();
		return result;
	}
	/**
	 * 从excel中解析出数据。
	 * @author wuyujie Feb 5, 2015 9:25:03 PM
	 * @param filePath,excel文件，支持xls和xlsx
	 * @return
	 * @throws Exception
	 */
    public static List<String[]> readExcel(InputStream is) throws IOException {
    	return readExcel(is,null,null);
    }
    
    
    public static List<String[]> readExcel(InputStream is,Integer startRow,Integer colCount) throws IOException {
    	POIFSFileSystem fs = new POIFSFileSystem(is);
        HSSFWorkbook WORK_BOOK = new HSSFWorkbook(fs);
        HSSFSheet sheet = WORK_BOOK.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        HSSFRow row = null;
        // 正文内容应该从第二行开始,第一行为表头的标题
        List<String[]> result = new ArrayList<String[]>();
        if(startRow == null){
        	startRow = 0;
        }
        for (int i = startRow; i <= rowNum; i++) {
            row = sheet.getRow(i);
            if(row == null)
            	continue;
            int colNum = colCount ==null ? row.getLastCellNum() : colCount;
            String[] values = new String[colNum];
            for (int k = 0; k < colNum; k++) {
                //title[i] = getStringCellValue(row.getCell((short) i));
            	values[k] = getStringCellValue(row.getCell(k));//getCellFormatValue(row.getCell((short) i));
            }
            //System.out.println("finish : "+i);
            result.add(values);
        }
        return result;
    }
    
    public static List<String[]> readExcel2007(InputStream is,Integer startRow,Integer colCount) throws IOException {
    	BufferedInputStream bfis = new BufferedInputStream(is);
    	XSSFWorkbook WORK_BOOK = new XSSFWorkbook(bfis);
    	XSSFSheet sheet = WORK_BOOK.getSheetAt(0);
    	
    	 // 得到总行数
        int rowNum = sheet.getLastRowNum();
        XSSFRow row = null;
        // 正文内容应该从第二行开始,第一行为表头的标题
        List<String[]> result = new ArrayList<String[]>();
        if(startRow == null){
        	startRow = 0;
        }
        for (int i = startRow; i <= rowNum; i++) {
            row = sheet.getRow(i);
            int colNum = colCount ==null ? row.getLastCellNum() : colCount;
            String[] values = new String[colNum];
            for (int k = 0; k < colNum; k++) {
                //title[i] = getStringCellValue(row.getCell((short) i));
            	values[k] = getStringCellValue(row.getCell(k));//getCellFormatValue(row.getCell((short) i));
            }
            result.add(values);
        }
        return result;
    }
    
    private static String getStringCellValue(Cell cell) {
        String strCell = "";
        if(cell != null){
	        switch (cell.getCellType()) {
	        case HSSFCell.CELL_TYPE_STRING:
	            strCell = cell.getStringCellValue();
	            break;
	        case HSSFCell.CELL_TYPE_NUMERIC:
	        	DecimalFormat format = new DecimalFormat("0.############");
	        	Double doubleValue = cell.getNumericCellValue();
	            strCell = format.format(doubleValue);
	            break;
	        case HSSFCell.CELL_TYPE_BOOLEAN:
	            strCell = String.valueOf(cell.getBooleanCellValue());
	            break;
	        case HSSFCell.CELL_TYPE_BLANK:
	            strCell = "";
	            break;
	        default:
	            strCell = "";
	            break;
	        }
        }
        return strCell;
    }
	
	private static HSSFCellStyle createHeaderCellStyle(HSSFWorkbook WORK_BOOK,HSSFFont font){
		HSSFCellStyle titleCellStyle = WORK_BOOK.createCellStyle();
		//对齐方式
		titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
		
		titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
		
		if(font != null){
			titleCellStyle.setFont(font);
        }
		
		//边框线条和边框颜色
		titleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		titleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		titleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		titleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		titleCellStyle.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
		titleCellStyle.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
		titleCellStyle.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);
		titleCellStyle.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
		
		//标题栏颜色#9DD1F4
		HSSFPalette palette = WORK_BOOK.getCustomPalette();
		palette.setColorAtIndex(
				HSSFColor.BLACK.index,
				(byte)Integer.parseInt("9D", 16), 
				(byte)Integer.parseInt("D1", 16), 
				(byte)Integer.parseInt("F4", 16)
		);
		
		titleCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		titleCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		return titleCellStyle;
	}
	private static HSSFCellStyle createBodyCellStyle(HSSFWorkbook WORK_BOOK,ExcelHeaderCell headerCell,HSSFFont font){
		HSSFCellStyle bodyCellStyle = WORK_BOOK.createCellStyle();
		//垂直对齐
		bodyCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
		
        if(font != null){
        	bodyCellStyle.setFont(font);
        }
		//水平对齐方式
		if("right".equals(headerCell.getBodyAlign())){
			bodyCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//水平靠右
		}else if("center".equals(headerCell.getBodyAlign())){
			bodyCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
		}else{
			bodyCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);//水平靠左
		}
		bodyCellStyle.setWrapText(true);//自动换行
		
		//边框和边框颜色
		bodyCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		bodyCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		bodyCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		bodyCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		bodyCellStyle.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
		bodyCellStyle.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
		bodyCellStyle.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);
		bodyCellStyle.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
		return bodyCellStyle;
	}
	
	private static HSSFCellStyle createTotalCellStyle(HSSFWorkbook WORK_BOOK){
		HSSFCellStyle totalCellStyle = WORK_BOOK.createCellStyle();
		//对齐方式
		totalCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
		totalCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//水平靠右
		
		//边框线条和边框颜色
		totalCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		totalCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		totalCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		totalCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		totalCellStyle.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
		totalCellStyle.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
		totalCellStyle.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);
		totalCellStyle.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
		
		//标题栏颜色#9DD1F4
		HSSFPalette palette = WORK_BOOK.getCustomPalette();
		palette.setColorAtIndex(
				HSSFColor.RED.index,
				(byte)Integer.parseInt("F5", 16), 
				(byte)Integer.parseInt("F5", 16), 
				(byte)Integer.parseInt("F5", 16)
		);
		
		totalCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		totalCellStyle.setFillForegroundColor(HSSFColor.RED.index);
		return totalCellStyle;
	}
	
	
	public static class ExcelHeaderCell{
		private String code;
		private String name;
		private Integer width;//像素，但是excel的列宽并不是像素作为单位，而是1/256的字符宽度作为单位，需要经过计算转换
		private Integer datatype;
		private String headerAlign;//标题栏文字对齐方式,left/center/right
		private String bodyAlign;//数据栏文字对齐方式,left/center/right
		private Integer fontSize;//字体大小
		public ExcelHeaderCell(String code,String name){
			this.code = code;
			this.name = name;
		}
		
		public ExcelHeaderCell(String code,String name,int width){
			this.code = code;
			this.name = name;
			this.width = width;
		}
		public ExcelHeaderCell(String code,String name,int width,int fontSize){
			this.code = code;
			this.name = name;
			this.width = width;
			this.fontSize = fontSize;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public Integer getDatatype() {
			return datatype;
		}
		public void setDatatype(int datatype) {
			this.datatype = datatype;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public void setWidth(Integer width) {
			this.width = width;
		}
		public void setDatatype(Integer datatype) {
			this.datatype = datatype;
		}
		public String getHeaderAlign() {
			return headerAlign;
		}
		public void setHeaderAlign(String headerAlign) {
			this.headerAlign = headerAlign;
		}
		public String getBodyAlign() {
			return bodyAlign;
		}
		public void setBodyAlign(String bodyAlign) {
			this.bodyAlign = bodyAlign;
		}

		public Integer getFontSize() {
			return fontSize;
		}
		
	}
	public static class ExcelBodyRow{
		private Map<String,Object> values;
		private Integer height;
		private Short bgColor;
		public ExcelBodyRow(){
			values = ClassUtil.newMap();
		}
		public ExcelBodyRow(Integer height,Short bgColor){
			values = ClassUtil.newMap();
			this.height = height;
			this.bgColor = bgColor;
		}
		
		public ExcelBodyRow addCell(String code,Object val) {
			values.put(code, val);
			return this;
		}
		public Map<String,Object> getCells() {
			return values;
		}
		public Object getCell(String code) {
			return values.get(code);
		}
		public Integer getHeight() {
			return height;
		}
		public Short getBgColor() {
			return bgColor;
		}
	}
	
	
	
}
