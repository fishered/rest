package com.asset.rest.config;

import com.xiong.cheetah.utils.IbeetlSqlParser;
import com.xiong.cheetah.utils.IbeetlSqlParserWithClean;
import com.xiong.cheetah.utils.SqlParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqlParserConfig {

    @Bean("sqlParserWithClean")
    public SqlParser createSqlParserWithClean(){
        return new IbeetlSqlParserWithClean();
    }

    @Bean("sqlParser")
    public SqlParser createSqlParser(){
        return new IbeetlSqlParser();
    }
}
