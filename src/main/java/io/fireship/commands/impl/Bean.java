package io.fireship.commands.impl;

import io.fireship.commands.Command;
import io.fireship.commands.HasOptions;
import io.fireship.model.Option;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Bean implements Command, HasOptions {
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        User moderator = event.getUser();
        User beaned = Objects.requireNonNull(event.getOption("user")).getAsUser();
        var opt = event.getOption("reason");
        var reason = opt == null ? null : opt.getAsString();

        MessageEmbed response = new EmbedBuilder()
                .setColor(Role.DEFAULT_COLOR_RAW)
                .setTitle("Bean Result:")
                .setDescription(getDescriptionFor(moderator, reason))
                .addField(
                    "Beaned:",
                    formatName(beaned).replace("_", "\\_"),
                    false
                )
                .build();

        beaned.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(
                        "You were beaned by %s".formatted(moderator.getAsMention())
                ))
                .queue();

        event.replyEmbeds(response).queue();
    }

    private String getDescriptionFor(@NotNull User moderator, String reason) {
        var description = "<:activity:1137406861855969310> **Reason:** %s\n" +
                "<:trustedAdmin:1137406869535735850> **Moderator:** %s" +
                "<:whitelistUser:1137406878033387610><:whitelistInvite:1137406875302891680><:whitelistChannel:1137406873868451901><:upvoter:1137406871435759736><:potentialDanger:1137406863349121036>";

        reason = reason == null ? "No reason." : reason;

        return description.formatted(reason, moderator.getAsMention());
    }

    @NotNull
    private String formatName(User beaned) {
        var discriminator = beaned.getDiscriminator();
        var formatted = new StringBuilder("<:space:1137406865299488798><:success:1137406866251591731> %s");

        if (!discriminator.equals("0000")) {
            formatted.append("#")
                    .append(discriminator);
        }

        formatted.append(" [`%s`]");

        return formatted.toString().formatted(beaned.getName(), beaned.getId());
    }

    @Override
    public List<Option> getOptions() {
        return List.of(
                new Option("user", "The user to bean", true, OptionType.USER),
                new Option("reason", "The reason you are beaning this user for", false, OptionType.STRING)
        );
    }
}
