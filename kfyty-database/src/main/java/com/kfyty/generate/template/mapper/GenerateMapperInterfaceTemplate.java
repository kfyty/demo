package com.kfyty.generate.template.mapper;

import com.kfyty.generate.GenerateSourcesBufferedWriter;
import com.kfyty.generate.info.AbstractDataBaseInfo;
import com.kfyty.generate.template.pojo.GeneratePojoTemplate;
import com.kfyty.util.CommonUtil;

import java.io.IOException;

/**
 * 功能描述: 生成 mapper 接口模板
 *
 * @author kfyty725@hotmail.com
 * @date 2019/9/3 17:37
 * @since JDK 1.8
 */
public class GenerateMapperInterfaceTemplate extends GeneratePojoTemplate {

    @Override
    public String fileSuffix() {
        return "Mapper";
    }

    @Override
    public void generateImport(AbstractDataBaseInfo dataBaseInfo, String basePackage, GenerateSourcesBufferedWriter out) throws IOException {
        super.importEntity(dataBaseInfo, basePackage, out);
        out.writeLine("import org.apache.ibatis.annotations.Param;");
        out.writeLine("import org.springframework.stereotype.Repository;\n");
        out.writeLine("import java.sql.SQLException;\n");
    }

    @Override
    public void generateClassDefinition(AbstractDataBaseInfo dataBaseInfo, GenerateSourcesBufferedWriter out) throws IOException {
        out.write("public interface {}", this.className);
    }

    @Override
    public void generateClassAnnotation(AbstractDataBaseInfo dataBaseInfo, GenerateSourcesBufferedWriter out) throws IOException {
        out.writeLine("@Repository");
    }

    @Override
    public void generateTableInfo(AbstractDataBaseInfo dataBaseInfo, GenerateSourcesBufferedWriter out) throws IOException {

    }

    @Override
    public void generateCustomCode(AbstractDataBaseInfo dataBaseInfo, String basePackage, GenerateSourcesBufferedWriter out) throws IOException {
        generateMapperInterfaces(dataBaseInfo, basePackage, out);
    }

    public void generateMapperInterfaces(AbstractDataBaseInfo dataBaseInfo, String basePackage, GenerateSourcesBufferedWriter out) throws IOException {
        String varName = CommonUtil.convert2Hump(dataBaseInfo.getTableName(), false);
        String className = CommonUtil.convert2Hump(dataBaseInfo.getTableName(), true);
        String entityVarName = varName + entityFileSuffix();
        String entityClassName = className + entityFileSuffix();

        out.writeLine("\t{} findById(@Param(\"id\") String id) throws SQLException;\n", entityClassName);

        out.writeLine("\tList<{}> findBy(@Param(\"field\") String field, @Param(\"value\") Object value) throws SQLException;\n", entityClassName);

        out.writeLine("\tList<{}> findLike(@Param(\"field\") String field, @Param(\"value\") Object value) throws SQLException;\n", entityClassName);

        out.writeLine("\tList<{}> findAll() throws SQLException;\n", entityClassName);

        out.writeLine("\tvoid insert(@Param(\"{}\") {} {}) throws SQLException;\n", entityVarName, entityClassName, entityVarName);

        out.writeLine("\tvoid updateById(@Param(\"id\") String id, @Param(\"{}\") {} {}) throws SQLException;\n", entityVarName, entityClassName, entityVarName);

        out.writeLine("\tvoid deleteById(@Param(\"id\") String id) throws SQLException;\n");
    }
}
