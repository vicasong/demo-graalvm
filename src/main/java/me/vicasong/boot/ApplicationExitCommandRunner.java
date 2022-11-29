package me.vicasong.boot;

import me.vicasong.CliState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Exit runner
 *
 * @author vicasong
 * @since 2022-07-28 10:34
 */
@Component
public class ApplicationExitCommandRunner implements ApplicationRunner, Ordered {

    /** log recorder */
    private static final Logger log = LoggerFactory.getLogger(ApplicationExitCommandRunner.class);

    private final ApplicationContext context;

    public ApplicationExitCommandRunner(ApplicationContext context) {
        this.context = context;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        int exit = SingleCommandRunner.currentExit();
        int returnCode = SpringApplication.exit(context, () -> exit);
        log.info("Program is going to exit. status=[{}]", returnCode);
        CliState.EXITING = true;
        System.exit(returnCode);
    }

    /** 顺序为最后 */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
