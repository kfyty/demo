package com.kfyty.generate;

import com.kfyty.generate.configuration.GenerateConfigurable;
import com.kfyty.generate.configuration.GenerateConfiguration;
import com.kfyty.generate.database.AbstractDataBaseMapper;
import com.kfyty.generate.info.AbstractDataBaseInfo;
import com.kfyty.generate.template.AbstractGenerateTemplate;
import com.kfyty.jdbc.SqlSession;
import com.kfyty.jdbc.annotation.Query;
import com.kfyty.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 功能描述: 生成资源
 *
 * @author kfyty725@hotmail.com
 * @date 2019/8/12 10:28
 * @since JDK 1.8
 */
@Slf4j
public class GenerateSources {

    private SqlSession sqlSession;

    private GenerateConfigurable configurable;

    private List<? extends AbstractDataBaseInfo> dataBaseInfoList;

    public GenerateSources(GenerateConfigurable generateConfigurable) throws Exception {
        this.sqlSession = new SqlSession();
        this.configurable = generateConfigurable;
        this.initDataBaseInfo();
    }

    public GenerateSources(GenerateConfiguration generateConfiguration) throws Exception {
        this.sqlSession = new SqlSession();
        this.configurable = new GenerateConfigurable(generateConfiguration);
        this.initDataBaseInfo();
    }

    private String initDirectory(AbstractDataBaseInfo info) {
        String parentPath = new File(configurable.getFilePath()).getAbsolutePath();
        String savePath = parentPath.endsWith(File.separator) ?
                parentPath + configurable.getPackageName().replace(".", File.separator) :
                parentPath + File.separator + configurable.getPackageName().replace(".", File.separator);
        Optional.of(new File(savePath)).filter(e -> !e.exists()).map(File::mkdirs);
        String fileSuffix = Optional.ofNullable(configurable.getCurrentGenerateTemplate().fileSuffix()).orElse("");
        String fileTypeSuffix = Optional.ofNullable(configurable.getCurrentGenerateTemplate().fileTypeSuffix()).orElse(".java");
        return savePath + File.separator + CommonUtil.convert2Hump(info.getTableName(), true) + fileSuffix + fileTypeSuffix;
    }

    private File initFile(AbstractDataBaseInfo info) throws IOException {
        File file = new File(this.initDirectory(info));
        if(file.exists() && !file.delete()) {
            log.error(": delete file failed !");
            return null;
        }
        if(!file.createNewFile()) {
            log.error(": create file failed !");
            return null;
        }
        return file;
    }

    private void initDataBaseInfo() throws Exception {
        this.sqlSession.setDataSource(configurable.getDataSource());
        AbstractDataBaseMapper dataBaseMapper = sqlSession.getProxyObject(configurable.getDataBaseMapper());

        Set<String> tables = Optional.ofNullable(configurable.getTables()).orElse(new HashSet<>());
        if(!CommonUtil.empty(configurable.getQueryTableSql())) {
            Query annotation = configurable.getDataBaseMapper().getMethod("findTableList").getAnnotation(Query.class);
            CommonUtil.setAnnotationValue(annotation, "value", configurable.getQueryTableSql());
            tables.addAll(dataBaseMapper.findTableList());
        }

        List<? extends AbstractDataBaseInfo> dataBaseInfos = dataBaseMapper.findDataBaseInfo(configurable.getDataBaseName());
        List<? extends AbstractDataBaseInfo> filteredDataBaseInfo = Optional.ofNullable(configurable.getTables()).filter(e -> !e.isEmpty()).map(e -> dataBaseInfos.stream().filter(info -> e.contains(info.getTableName())).collect(Collectors.toList())).orElse(null);
        this.dataBaseInfoList = CommonUtil.empty(filteredDataBaseInfo) ? dataBaseInfos : filteredDataBaseInfo;
        for (AbstractDataBaseInfo info : this.dataBaseInfoList) {
            info.setTableInfos(dataBaseMapper.findTableInfo(info.getDataBaseName(), info.getTableName()));
        }
        if(log.isDebugEnabled()) {
            log.debug(": initialize data base info success !");
        }
    }

    public GenerateSources refreshGenerateConfiguration(GenerateConfiguration configuration) throws Exception {
        this.configurable.refreshGenerateConfiguration(configuration);
        return this;
    }

    public GenerateSources refreshGenerateTemplate(AbstractGenerateTemplate generateTemplate) throws Exception {
        this.configurable.refreshGenerateTemplate(generateTemplate);
        return this;
    }

    public void generate() throws Exception {
        File file = null;
        BufferedWriter out = null;
        while(configurable.hasGenerateTemplate()) {
            AbstractGenerateTemplate nextGenerateTemplate = configurable.getNextGenerateTemplate();
            for (AbstractDataBaseInfo dataBaseInfo : this.dataBaseInfoList) {
                if(file == null || !nextGenerateTemplate.sameFile()) {
                    file = this.initFile(dataBaseInfo);
                    out = new BufferedWriter(new FileWriter(file, nextGenerateTemplate.sameFile()));
                }
                nextGenerateTemplate.generate(dataBaseInfo, configurable.getPackageName(), out);
                out.flush();
                log.debug(": generate resource:[{}] success --> [{}]", file.getName(), file.getAbsolutePath());
            }
            if(out != null && !nextGenerateTemplate.sameFile()) {
                out.close();
            }
        }
    }
}
