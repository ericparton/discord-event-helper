package com.djungelorm.discord.bot.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledEventReminderScheduler
{
    private final JDA discordClient;

    @Scheduled( cron = "${discord.scheduledEventReminder.cronExpression}" )
    public void sendEventReminders()
    {
        var now = OffsetDateTime.now();

        log.info( "Sending scheduled event reminders" );

        for ( Guild guild : discordClient.getGuilds() )
        {
            log.info( "Found guild: '{}'", guild.getName() );

            for ( ScheduledEvent scheduledEvent : guild.retrieveScheduledEvents().complete() )
            {
                var startTime = scheduledEvent.getStartTime();

                if ( startTime.isBefore( now ) )
                {
                    continue;
                }

                var embed = getMessageEmbed( scheduledEvent );

                for ( Integer days : List.of( 1, 2, 3 ) )
                {
                    if ( startTime.isBefore( now.plusDays( days ) ) )
                    {
                        log.info( "Found scheduled event: '{}' ({})", scheduledEvent.getName(), startTime );

                        for ( Member member : scheduledEvent.retrieveInterestedMembers().complete() )
                        {
                            log.info( "Notifying interested user: '{}'", member.getEffectiveName() );

                            try
                            {
                                sendMessageToUser( member, embed, scheduledEvent.getJumpUrl() );
                            }
                            catch ( ErrorResponseException ex )
                            {
                                log.error( "Unable to send notification to user: {} ({})", member.getEffectiveName(), ex.getMeaning() );
                            }
                        }

                        break;
                    }
                }
            }
        }

        log.info( "Scheduled event reminders sent" );
    }

    private static void sendMessageToUser( Member member, MessageEmbed messageEmbed, String url )
    {
        var actionRow = ActionRow.of( Button.link( url, "View Event" ) );

        member.getUser().openPrivateChannel()
              .flatMap( channel -> channel.sendMessageEmbeds( messageEmbed ).addComponents( actionRow ) )
              .complete();
    }

    private static MessageEmbed getMessageEmbed( ScheduledEvent scheduledEvent )
    {
        return new EmbedBuilder()
            .setTitle( "Event Reminder" )
            .setDescription( "The following event is happening soon!" )
            .addField( "Event", scheduledEvent.getName(), false )
            .addField( "Location", scheduledEvent.getLocation(), false )
            .setTimestamp( scheduledEvent.getStartTime() )
            .setColor( new Color( 100, 149, 237 ) )
            .setUrl( scheduledEvent.getJumpUrl() )
            .setAuthor( scheduledEvent.getCreator().getEffectiveName(), null, scheduledEvent.getCreator().getEffectiveAvatarUrl() )
            .setThumbnail( scheduledEvent.getGuild().getIconUrl() )
            .build();
    }
}
