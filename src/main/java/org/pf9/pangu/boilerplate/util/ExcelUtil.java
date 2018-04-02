package org.pf9.pangu.boilerplate.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.List;

public class ExcelUtil {

    public static HSSFWorkbook excelOut(String[] cloumName, List<List<Object>> list){
        //声明一个工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //生成一个表格
        HSSFSheet sheet = workbook.createSheet();
        //设置表格默认列宽度为20个字符
        sheet.setDefaultColumnWidth(20);
        //生成一个样式，用来设置标题样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 表头居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //生成一个字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12); // 字体高度
        font.setFontName(" 黑体 "); // 字体
        //把字体应用到当前的样式
        style.setFont(font);
        //创建字体
//        HSSFFont font = workBook.createFont();
//        font.setFontHeightInPoints((short) 16);
//        //font.setFontHeight((short)320); 效果和上面一样。用这个方法设置大小，值要设置为字体大小*20倍，具体看API文档
//        font.setColor(HSSFColor.GREEN.index);
//        font.setBold(true);
//        style.setFont(font);
//        //设置背景
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setFillForegroundColor(HSSFColor.RED.index);


        // 生成并设置另一个样式,用于设置内容样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setAlignment(HorizontalAlignment.CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setFontName(" 黑体 "); // 字体
        // 把字体应用到当前的样式
        style2.setFont(font2);

        HSSFRow row = sheet.createRow(0);
        for(int i = 0; i < cloumName.length; i++){
            //单元格
            HSSFCell cellHead = row.createCell(i);
            cellHead.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(cloumName[i]);
            cellHead.setCellValue(text);
        }

        for (int i = 0; i < list.size(); i++){
            row = sheet.createRow(i + 1);
            List<Object> dataList = list.get(i);
            for (int j = 0; j < dataList.size(); j++) {
                // 表格内容样式设置
                HSSFCell cellHead = row.createCell(j);
                cellHead.setCellStyle(style2);
                HSSFRichTextString text = new HSSFRichTextString(String.valueOf(dataList.get(j)));

                // 为空
                if(text == null || text.toString() == ""){
                    cellHead.setCellValue("");
                }
                // 整数,不为电话
                else if(ValidateUtil.isInteger(String.valueOf(text))
                        && !(StringUtils.startsWith(String.valueOf(text),"1")
                        && String.valueOf(text).length() == 11)){
                    cellHead.setCellValue(Integer.parseInt(String.valueOf(text)));
                }
                // 有小数、或为电话
                else if(ValidateUtil.isDouble(dataList.get(j).toString())){
                    cellHead.setCellValue(Double.parseDouble(String.valueOf(text)));
                }
                // 字符串
                else{
                    cellHead.setCellValue(String.valueOf(text));
                }
            }
        }

        return workbook;
    }
}