package com.djungelorm.discord.bot;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties( prefix = "discord" )
@Getter
@Validated
@RequiredArgsConstructor
public class DiscordConfiguration
{
    @NotNull
    private final String token;
}
