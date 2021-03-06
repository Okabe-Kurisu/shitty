package webby.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * program: webby
 * description: 日志配置器
 * author: Makise
 * create: 2019-04-13 18:12
 **/
public class WebbyLogConfig {
    private static LogConfig config;
    private static final String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

    /**
     * Description: 读取配置，并给logback进行配置
     * Param: [properties]
     * return: void
     * Author: Makise
     * Date: 2019/4/13
     */
    public static void loadProperties(PropertiesReader pr){
        config = new LogConfig();
        //从配置文件中读取配置
        config.setLevel(String.valueOf(pr.getOrDefault("webby.log.level", "INFO")));
        config.setConsolePattern(String.valueOf(pr.getOrDefault("webby.log.console.pattern", "%d{yyyy-MM-dd HH:mm:ss} [%p][%c][%M][%L]-> %m%n")));
        config.setFileName(String.valueOf(pr.get("webby.log.file.name")));
        config.setFilePattern(String.valueOf(pr.getOrDefault("webby.log.file.pattern", "%d{yyyy-MM-dd HH:mm:ss} [%p][%c][%M][%L]-> %m%n")));
        startLog();
    }

    private static void startLog(){
        //设置输出等级
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = lc.getLogger("webby");
        logger.setLevel(Level.toLevel(config.getLevel()));
        lc.getLogger("io.netty").setLevel(Level.toLevel(config.getLevel()));

        //设置输出在控制台的格式
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setPattern(formart(config.getConsolePattern()));


        encoder.setImmediateFlush(true);

        Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        ConsoleAppender<ILoggingEvent> ca = (ConsoleAppender<ILoggingEvent>) rootLogger.getAppender("console");
        ca.setContext(lc);
        ca.setEncoder(encoder);

        encoder.start();
        ca.start();
        rootLogger.addAppender(ca);

        //如果要在文件中输出，就设置输出器
        if (!StringUtils.isBlank(config.getFileName()) && !"null".equals(config.getFileName())){
            PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
            fileEncoder.setContext(lc);
            fileEncoder.setCharset(Charset.forName("UTF-8"));
            fileEncoder.setPattern(formart(config.getFilePattern()));
            fileEncoder.setImmediateFlush(true);

            FileAppender<ILoggingEvent> fa = new FileAppender<>();
            fa.setContext(lc);
            fa.setName("file");
            fa.setEncoder(fileEncoder);
            fa.setFile(formart(config.getFileName()));
            fa.setAppend(true);

            fileEncoder.start();
            fa.start();
            logger.addAppender(fa);
        }
    }

    private static String formart(String s){
        s = s.replace("{app.name}", WebbyConfig.getConfig().getAppName());
        s = s.replace("{date}", date);
        return s;
    }

    @Data
    private static class LogConfig {
        private String level;
        private String consolePattern;
        private String fileName;
        private String filePattern;
    }

    public static LogConfig getConfig(){
        return config;
    }
}