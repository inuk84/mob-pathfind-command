
package inuk.pathfindcmd;

import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import inuk.pathfindcmd.mixin.MobAccessor;

public class MobGoto {
    private static final double DEFAULT_SPEED = 1.0;
    private static final float DEFAULT_PATH_LENGTH = 64.0F;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            dispatcher.register(
                Commands.literal("mobpathfind")
                    .then(Commands.argument("mob", EntityArgument.entities())
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                            .executes(context -> moveToPosition(
                                EntityArgument.getEntities(context, "mob"),
                                Vec3Argument.getVec3(context, "pos"),
                                DEFAULT_SPEED,
                                DEFAULT_PATH_LENGTH,
                                context.getSource()
                            ))
                            .then(Commands.argument("speed", DoubleArgumentType.doubleArg(0.01))
                                .executes(context -> moveToPosition(
                                    EntityArgument.getEntities(context, "mob"),
                                    Vec3Argument.getVec3(context, "pos"),
                                    DoubleArgumentType.getDouble(context, "speed"),
                                    DEFAULT_PATH_LENGTH,
                                    context.getSource()
                                ))
                                .then(Commands.argument("maxPathLength", DoubleArgumentType.doubleArg(1.0))
                                    .executes(context -> moveToPosition(
                                        EntityArgument.getEntities(context, "mob"),
                                        Vec3Argument.getVec3(context, "pos"),
                                        DoubleArgumentType.getDouble(context, "speed"),
                                        (float) DoubleArgumentType.getDouble(context, "maxPathLength"),
                                        context.getSource()
                                    ))
                                )
                            )
                        )
                        .then(Commands.argument("target", EntityArgument.entity())
                            .executes(context -> moveToEntity(
                                EntityArgument.getEntities(context, "mob"),
                                EntityArgument.getEntity(context, "target"),
                                DEFAULT_SPEED,
                                DEFAULT_PATH_LENGTH,
                                context.getSource()
                            ))
                            .then(Commands.argument("speed", DoubleArgumentType.doubleArg(0.01))
                                .executes(context -> moveToEntity(
                                    EntityArgument.getEntities(context, "mob"),
                                    EntityArgument.getEntity(context, "target"),
                                    DoubleArgumentType.getDouble(context, "speed"),
                                    DEFAULT_PATH_LENGTH,
                                    context.getSource()
                                ))
                                .then(Commands.argument("maxPathLength", DoubleArgumentType.doubleArg(1.0))
                                    .executes(context -> moveToEntity(
                                        EntityArgument.getEntities(context, "mob"),
                                        EntityArgument.getEntity(context, "target"),
                                        DoubleArgumentType.getDouble(context, "speed"),
                                        (float) DoubleArgumentType.getDouble(context, "maxPathLength"),
                                        context.getSource()
                                    ))
                                )
                            )
                        )
                        .then(Commands.literal("stop")
                            .executes(context -> {
                                int count = 0;
                                for (Entity entity : EntityArgument.getEntities(context, "mob")) {
                                    if (entity instanceof Mob mob) {
                                        stopMob(mob);
                                        count++;
                                    }
                                }
                                int result = count;
                                context.getSource().sendSuccess(
                                    () -> Component.literal("Stopped " + result + " mobs"),
                                    true
                                );
                                return count;
                            })
                        )
                    )
            );
        });
    }

    private static int moveToPosition(
        Iterable<? extends Entity> entities,
        Vec3 position,
        double speed,
        float maxPathLength,
        net.minecraft.commands.CommandSourceStack source
    ) {
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Mob mob) {
                setGoal(mob, new CommandMoveGoal(
                    mob,
                    position,
                    speed
                ), maxPathLength);
                count++;
            }
        }
        int result = count;
        source.sendSuccess(
            () -> Component.literal("Made " + result + " mobs pathfind"),
            true
        );
        return count;
    }

    private static int moveToEntity(
        Iterable<? extends Entity> entities,
        Entity target,
        double speed,
        float maxPathLength,
        net.minecraft.commands.CommandSourceStack source
    ) {
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Mob mob && mob.level() == target.level()) {
                setGoal(mob, new CommandMoveGoal(
                    mob,
                    target,
                    speed
                ), maxPathLength);
                count++;
            }
        }
        int result = count;
        source.sendSuccess(
            () -> Component.literal("Made " + result + " mobs pathfind to entity."),
            true
        );
        return count;
    }

    private static void setGoal(Mob mob, CommandMoveGoal goal, float maxPathLength) {
        mob.getNavigation().setRequiredPathLength(maxPathLength);
        var selector = ((MobAccessor) mob).pathfindcmd$getGoalSelector();
        selector.removeAllGoals(existing -> existing instanceof CommandMoveGoal);
        selector.addGoal(-100, goal);
    }

    private static void stopMob(Mob mob) {
        var selector = ((MobAccessor) mob).pathfindcmd$getGoalSelector();
        selector.removeAllGoals(existing -> existing instanceof CommandMoveGoal);
        mob.getNavigation().stop();
    }
}
