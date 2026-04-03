package com.djungelorm.discord.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
@ComponentScan( basePackages = { "com.djungelorm" } )
public class DiscordEventHelperBot
{
    static void main( String[] args )
    {
        SpringApplication.run( DiscordEventHelperBot.class, args );
    }
}
