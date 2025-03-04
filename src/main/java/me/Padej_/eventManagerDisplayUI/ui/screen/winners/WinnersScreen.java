package me.Padej_.eventManagerDisplayUI.ui.screen.winners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.EventManagerMainScreen;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WinnersScreen extends EventManagerScreenTemplate implements Listener {
    private static final List<String> winners;
    private static String finalWinner;
    private static final Map<Player, Boolean> awaitingInput = new HashMap<>();
    private static final Map<Player, Integer> awaitingPosition = new HashMap<>();
    private final List<TextDisplayButtonWidget> winnerWidgets = new ArrayList<>();
    private TextDisplayButtonWidget finalWinnerWidget;

    static {
        winners = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            winners.add(null);
        }
    }

    public WinnersScreen() {
        super();
    }

    public WinnersScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return EventManagerMainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition leftTopEdge = new WidgetPosition(-0.43f, 0.8f);
        WidgetPosition rightTopEdge = new WidgetPosition(0.52f, 0.8f);
        float step = -0.1f;
        float horizontalStep = 0.07f;
        float scale = .35f;

        // Создаем виджеты для всех 6 позиций
        for (int i = 0; i < 6; i++) {
            // Номер позиции
            createTextWidget(new TextDisplayButtonConfig(
                            Component.text((i + 1) + "."),
                            Component.text((i + 1) + "."),
                            () -> {
                            }
                    )
                            .setPosition(leftTopEdge.clone().addVertical(step * i))
                            .setScale(scale, scale, scale)
                            .disableClickSound()
                            .setBackgroundAlpha(0)
                            .setHoveredBackgroundAlpha(0)
            );

            // Имя победителя
            TextDisplayButtonWidget winnerWidget = createTextWidget(new TextDisplayButtonConfig(
                            Component.text("----------------").color(Palette.GRAY_LIGHT),
                            Component.text("----------------").color(Palette.GRAY_LIGHT),
                            () -> {
                            }
                    )
                            .setPosition(leftTopEdge.clone().addHorizontal(0.47).addVertical(step * i))
                            .setScale(scale, scale, scale)
                            .disableClickSound()
                            .setTolerance(0)
                            .setTextAlignment(TextDisplay.TextAlignment.LEFT)
                            .setBackgroundAlpha(0)
                            .setHoveredBackgroundAlpha(0)
            );

            int finalI1 = i;
            winnerWidget.setUpdateCallback(() -> {
                if (viewer != null && winners.get(finalI1) != null) {
                    winnerWidget.setText(
                            Component.text(winners.get(finalI1)).color(Palette.WHITE_LIGHT),
                            Component.text(winners.get(finalI1)).color(Palette.WHITE_LIGHT)
                    );
                }
            });

            winnerWidgets.add(winnerWidget);

            // Кнопка добавления
            int finalI = i;
            createTextWidget(new TextDisplayButtonConfig(
                            Component.text("+").color(TextColor.fromHexString("0dcf2c")),
                            Component.text("+").color(TextColor.fromHexString("098b1d")),
                            () -> {
                                awaitingInput.put(player, true);
                                awaitingPosition.put(player, finalI);
                                player.sendMessage(Component.text("Введите имя игрока").color(Palette.GRAY_LIGHT));
                            }
                    )
                            .setPosition(rightTopEdge.clone().addVertical(step * i).addDepth(-0.01f))
                            .setScale(scale, scale, scale)
                            .setTolerance(0.015)
                            .setHoveredTransformation(new Transformation(
                                    new Vector3f(0, -0.05f, 0),
                                    new AxisAngle4f(),
                                    new Vector3f(.5f, .5f, .5f),
                                    new AxisAngle4f()
                            ), 2)
                            .setBackgroundAlpha(0)
                            .setHoveredBackgroundAlpha(0)
            );

            // Кнопка удаления
            int finalI2 = i;
            createTextWidget(new TextDisplayButtonConfig(
                            Component.text("-").color(Palette.RED_LIGHT),
                            Component.text("-").color(Palette.RED_DARK),
                            () -> {
                                winners.set(finalI2, null); // Очищаем позицию
                                TextDisplayButtonWidget widget = winnerWidgets.get(finalI2);
                                widget.setText(
                                        Component.text("----------------").color(Palette.GRAY_LIGHT),
                                        Component.text("----------------").color(Palette.GRAY_LIGHT)
                                );
                            }
                    )
                            .setPosition(rightTopEdge.clone().addVertical(step * i).addHorizontal(horizontalStep).addDepth(-0.01f))
                            .setScale(scale, scale, scale)
                            .setTolerance(0.015)
                            .setHoveredTransformation(new Transformation(
                                    new Vector3f(0, -0.05f, 0),
                                    new AxisAngle4f(),
                                    new Vector3f(.5f, .5f, .5f),
                                    new AxisAngle4f()
                            ), 2)
                            .setBackgroundAlpha(0)
                            .setHoveredBackgroundAlpha(0)
            );

            // Кнопка колокольчика
            int finalI3 = i;
            createTextWidget(new TextDisplayButtonConfig(
                            Component.text("🔔").color(Palette.YELLOW_LIGHT),
                            Component.text("🔔").color(Palette.YELLOW_DARK),
                            () -> {
                                String winner = winners.get(finalI3);
                                if (winner != null) {
                                    String message = String.format("В %d раунде победил(а) %s", finalI3 + 1, winner);
                                    Bukkit.broadcast(Component.text(message).color(Palette.YELLOW_LIGHT));
                                }
                            }
                    )
                            .setPosition(rightTopEdge.clone().addVertical(step * i).addHorizontal(horizontalStep * 2).addDepth(-0.01f))
                            .setScale(scale, scale, scale)
                            .setTolerance(0.015)
                            .setHoveredTransformation(new Transformation(
                                    new Vector3f(0, -0.05f, 0),
                                    new AxisAngle4f(),
                                    new Vector3f(.5f, .5f, .5f),
                                    new AxisAngle4f()
                            ), 2)
                            .setBackgroundAlpha(0)
                            .setHoveredBackgroundAlpha(0)
            );
        }

        // После создания 6 обычных строк добавляем финальную строку
        // Буква F для финала
        createTextWidget(new TextDisplayButtonConfig(
                        Component.text("F.").color(Palette.PURPLE_LIGHT),
                        Component.text("F.").color(Palette.PURPLE_LIGHT),
                        () -> {
                        }
                )
                        .setPosition(leftTopEdge.clone().addVertical(step * 7))
                        .setScale(scale, scale, scale)
                        .disableClickSound()
                        .setBackgroundAlpha(0)
                        .setHoveredBackgroundAlpha(0)
        );

        // Имя финального победителя
        finalWinnerWidget = createTextWidget(new TextDisplayButtonConfig(
                        Component.text("----------------").color(Palette.GRAY_LIGHT),
                        Component.text("----------------").color(Palette.GRAY_LIGHT),
                        () -> {
                        }
                )
                        .setPosition(leftTopEdge.clone().addHorizontal(0.47).addVertical(step * 7))
                        .setScale(scale, scale, scale)
                        .disableClickSound()
                        .setTolerance(0)
                        .setTextAlignment(TextDisplay.TextAlignment.LEFT)
                        .setBackgroundAlpha(0)
                        .setHoveredBackgroundAlpha(0)
        );

        finalWinnerWidget.setUpdateCallback(() -> {
            if (viewer != null && finalWinner != null) {
                finalWinnerWidget.setText(
                        Component.text(finalWinner).color(Palette.WHITE_LIGHT),
                        Component.text(finalWinner).color(Palette.WHITE_LIGHT)
                );
            }
        });

        // Кнопка добавления для финала
        createTextWidget(new TextDisplayButtonConfig(
                        Component.text("+").color(TextColor.fromHexString("0dcf2c")),
                        Component.text("+").color(TextColor.fromHexString("098b1d")),
                        () -> {
                            awaitingInput.put(player, true);
                            awaitingPosition.put(player, -1);
                            player.sendMessage(Component.text("Введите имя финального победителя").color(Palette.GRAY_LIGHT));
                        }
                )
                        .setPosition(rightTopEdge.clone().addVertical(step * 7).addDepth(-0.01f))
                        .setScale(scale, scale, scale)
                        .setTolerance(0.015)
                        .setHoveredTransformation(new Transformation(
                                new Vector3f(0, -0.05f, 0),
                                new AxisAngle4f(),
                                new Vector3f(.5f, .5f, .5f),
                                new AxisAngle4f()
                        ), 2)
                        .setBackgroundAlpha(0)
                        .setHoveredBackgroundAlpha(0)
        );

        // Кнопка удаления для финала
        createTextWidget(new TextDisplayButtonConfig(
                        Component.text("-").color(Palette.RED_LIGHT),
                        Component.text("-").color(Palette.RED_DARK),
                        () -> {
                            finalWinner = null;
                            finalWinnerWidget.setText(
                                    Component.text("----------------").color(Palette.GRAY_LIGHT),
                                    Component.text("----------------").color(Palette.GRAY_LIGHT)
                            );
                        }
                )
                        .setPosition(rightTopEdge.clone().addVertical(step * 7).addHorizontal(horizontalStep).addDepth(-0.01f))
                        .setScale(scale, scale, scale)
                        .setTolerance(0.015)
                        .setHoveredTransformation(new Transformation(
                                new Vector3f(0, -0.05f, 0),
                                new AxisAngle4f(),
                                new Vector3f(.5f, .5f, .5f),
                                new AxisAngle4f()
                        ), 2)
                        .setBackgroundAlpha(0)
                        .setHoveredBackgroundAlpha(0)
        );

        // Кнопка колокольчика для финала
        createTextWidget(new TextDisplayButtonConfig(
                        Component.text("🔔").color(Palette.YELLOW_LIGHT),
                        Component.text("🔔").color(Palette.YELLOW_DARK),
                        () -> {
                            if (finalWinner != null) {
                                String message = String.format("В финале побеждает %s!", finalWinner);
                                Bukkit.broadcast(Component.text(message).color(Palette.PURPLE_LIGHT));
                            }
                        }
                )
                        .setPosition(rightTopEdge.clone().addVertical(step * 7).addHorizontal(horizontalStep * 2).addDepth(-0.01f))
                        .setScale(scale, scale, scale)
                        .setTolerance(0.015)
                        .setHoveredTransformation(new Transformation(
                                new Vector3f(0, -0.05f, 0),
                                new AxisAngle4f(),
                                new Vector3f(.5f, .5f, .5f),
                                new AxisAngle4f()
                        ), 2)
                        .setBackgroundAlpha(0)
                        .setHoveredBackgroundAlpha(0)
        );
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (awaitingInput.getOrDefault(player, false)) {
            event.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(event.message());

            boolean playerExists = Bukkit.getOnlinePlayers().stream()
                    .anyMatch(p -> p.getName().equals(message));

            if (playerExists) {
                int position = awaitingPosition.get(player);
                if (position == -1) {
                    // Обработка финального победителя
                    finalWinner = message;
                    player.sendMessage(Component.text("Финальный победитель " + message + " добавлен!").color(Palette.PURPLE_LIGHT));
                    if (finalWinnerWidget != null) {
                        finalWinnerWidget.update();
                    }
                } else {
                    // Обработка обычных победителей
                    winners.set(position, message);
                    player.sendMessage(Component.text("Игрок " + message + " добавлен!").color(Palette.GRAY_LIGHT));
                    if (position < winnerWidgets.size()) {
                        TextDisplayButtonWidget widget = winnerWidgets.get(position);
                        widget.update();
                    }
                }
            } else {
                player.sendMessage(Component.text("[Ошибка] Такого игрока нет в сети").color(Palette.RED_LIGHT));
            }

            awaitingInput.remove(player);
            awaitingPosition.remove(player);
        }
    }

    public static List<String> getWinners() {
        return winners.stream()
                .filter(name -> name != null)
                .collect(Collectors.toList());
    }

    public static String getFinalWinner() {
        return finalWinner;
    }
}
