package com.zbkj.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.exception.CrmebException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 导出工具类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class ExportUtil {

    /**
     * 导出Excel文件
     *
     * @param fileName 文件名
     * @param title    文件标题
     * @param voList   数据列表
     * @param aliasMap 别名Map（别名需要与数据列表的数据对应）
     * @return 返回给前端的文件名（路径+文件名）
     */
    public static String exportExcel(String fileName, String title, List<?> voList, LinkedHashMap<String, String> aliasMap, String serverPath) {
        if (StrUtil.isBlank(fileName)) {
            throw new CrmebException("文件名不能为空");
        }
        if (StrUtil.isBlank(title)) {
            throw new CrmebException("标题不能为空");
        }
        if (CollUtil.isEmpty(voList)) {
            throw new CrmebException("数据列表不能为空");
        }
        if (CollUtil.isEmpty(aliasMap)) {
            throw new CrmebException("别名map不能为空");
        }
        // 文件名部分
        String modelPath = UploadConstants.UPLOAD_FILE_KEYWORD + "/" + UploadConstants.DOWNLOAD_FILE_KEYWORD + "/" + UploadConstants.UPLOAD_MODEL_PATH_EXCEL;
        String filePath = modelPath + "/" + CrmebDateUtil.nowDate(DateConstants.DATE_FORMAT_DATE).replace("-", "/") + "/";
        String path = FilenameUtils.separatorsToSystem(serverPath + filePath);
        String newFileName = filePath + fileName;

        // 判断是否存在当前目录，不存在则创建
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = path.concat(fileName);

        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter(fileName);
//        ExcelWriter writer = ExcelUtil.getWriter("d:/writeMapTest.xlsx");
        CellStyle headCellStyle = writer.getHeadCellStyle();
        Font font = writer.createFont();
        font.setBold(true);
        headCellStyle.setFont(font);
        // 解决操作excel时换行符（\n）只有鼠标双击才会生效
        CellStyle styleSet = writer.getCellStyle();
        styleSet.setWrapText(true);

        //自定义标题别名
        aliasMap.forEach((key, value) -> writer.addHeaderAlias(key, value));
        // 合并单元格后的标题行，使用默认标题样式
        writer.merge(aliasMap.size() - 1, title);
        writer.merge(aliasMap.size() - 1, StrUtil.format("生成时间:{}", DateUtil.now()));
        //设置宽度自适应
        writer.setColumnWidth(-1, 22);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(voList, true);
        // 关闭writer，释放内存
        writer.close();

        return newFileName;
    }

}
