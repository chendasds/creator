/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-15 11:38:25
 * @LastEditors: dingxiuchen 2745250790@qq.com
 * @LastEditTime: 2026-03-15 15:22:30
 * @FilePath: \build-one\src\main\java\com\creation\platform\CreationPlatformApplication.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.creation.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.creation.platform.mapper")
public class CreationPlatformApplication {

    public static void main(String[] args) {
        System.out.println("Creation Platform Application is starting...");
        SpringApplication.run(CreationPlatformApplication.class, args);
    }

}
