package me.vicasong.autoconfig;

import me.vicasong.boot.SingleCommandRunner;
import me.vicasong.console.ToolTerminal;
import me.vicasong.console.SystemTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tool config
 *
 * @author vicasong
 * @since 2022-07-21 20:57
 */
@Configuration
public class ToolCliConfiguration implements ApplicationContextAware {

    /** log recorder */
    private static final Logger log = LoggerFactory.getLogger(ToolCliConfiguration.class);

    private ApplicationContext context;

    /** console bean */
    @Bean(destroyMethod = "close")
    public ToolTerminal terminal() {
        return new SystemTerminal();
    }



    /** command entrance */
    @Bean
    public SingleCommandRunner commandRunner() {
        return new SingleCommandRunner(context);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
