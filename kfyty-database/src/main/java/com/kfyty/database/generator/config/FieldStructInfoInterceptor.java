package com.kfyty.database.generator.config;

import com.kfyty.database.generator.info.AbstractFieldStructInfo;
import com.kfyty.database.jdbc.intercept.QueryInterceptor;
import com.kfyty.support.method.MethodParameter;
import com.kfyty.support.utils.JdbcTypeUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static com.kfyty.support.utils.CommonUtil.mapping;

/**
 * 描述: 字段信息拦截器
 *
 * @author kfyty725
 * @date 2021/9/17 18:47
 * @email kfyty725@hotmail.com
 */
public class FieldStructInfoInterceptor implements QueryInterceptor {

    @Override
    public Object intercept(PreparedStatement ps, ResultSet rs, Object retValue, MethodParameter... params) throws SQLException {
        return retValue instanceof Collection ? mapping(retValue, e -> {
            if (e instanceof AbstractFieldStructInfo) {
                AbstractFieldStructInfo info = (AbstractFieldStructInfo) e;
                info.setJdbcType(JdbcTypeUtil.convert2JdbcType(info.getFieldType()));
            }
            return e;
        }) : retValue;
    }
}
