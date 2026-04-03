package com.djungelorm.discord.client;

import com.djungelorm.discord.bot.DiscordConfiguration;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DiscordClientFactory
{
    private final DiscordConfiguration discordConfiguration;

    @Bean
    public JDA discordClient()
    {
        return JDABuilder.createLight( discordConfiguration.getToken() ).build();
    }
}
