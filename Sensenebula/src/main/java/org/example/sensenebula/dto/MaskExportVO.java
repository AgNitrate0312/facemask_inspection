package org.example.sensenebula.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Date;

@Data
public class MaskExportVO {
    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("摄像机")
    @ColumnWidth(20)
    private String cameraName;

    @ExcelProperty("通道")
    private Integer channel;

    @ExcelProperty("抓拍时间")
    @ColumnWidth(20)
    private String snapTime;

    @ExcelProperty("性别")
    private String personGender;

    @ExcelProperty("年龄")
    private Integer ageValue;

    @ExcelProperty("口罩状态")
    @ColumnWidth(15)
    private String maskStatus;

    @ExcelProperty("处理状态")
    private String handleStatusStr; // Converted string

    @ExcelProperty("备注")
    @ColumnWidth(30)
    private String remark;

    @ExcelProperty("入库时间")
    @ColumnWidth(20)
    private Date createTime;
}
