package com.huadong;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author jinjinhui
 * @date 2019/5/9
 */
@SpringBootApplication
public class SpoonWebApplication {

	public static void main(String[] args) {
        new SpringApplicationBuilder(SpoonWebApplication.class).listeners(new ApplicationStartingEventListener()).run(args);
	}

}
