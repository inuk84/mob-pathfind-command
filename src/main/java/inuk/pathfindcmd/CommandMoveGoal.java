
package inuk.pathfindcmd;

import java.util.EnumSet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class CommandMoveGoal extends Goal {
    private static final double ARRIVAL_DISTANCE_SQR = 1.75;
    private static final double TARGET_MOVE_DISTANCE_SQR = 1.75;
    private static final int REPATH_DELAY = 10;

    private final Mob mob;

    private Vec3 destination;
    private Entity target;

    private final double speed;

    private Vec3 lastTargetPosition;

    private int ticksUntilRepath;
    private boolean active = true;

    public CommandMoveGoal(Mob mob, Vec3 destination, double speed) {
        this.mob = mob;
        this.destination = destination;
        this.speed = speed;
        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public CommandMoveGoal(Mob mob, Entity target, double speed) {
        this.mob = mob;
        this.target = target;
        this.speed = speed;
        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return active;
    }

    @Override
    public boolean canContinueToUse() {
        if (!active) {
            return false;
        }
        if (target != null) {
            if (target.isRemoved() || target.level() != mob.level()) {
                return false;
            }
            return mob.distanceToSqr(target) > ARRIVAL_DISTANCE_SQR;
        }
        return destination != null && mob.distanceToSqr(destination.x, destination.y,destination.z) > ARRIVAL_DISTANCE_SQR;
    }

    @Override
    public void start() {
        ticksUntilRepath = 0;
        updatePath();
    }

    @Override
    public void tick() {
        ticksUntilRepath--;
        if (target != null) {
            if (target.isRemoved() || target.level() != mob.level()) {
                active = false;
                return;
            }
            Vec3 currentTargetPosition = target.position();
            boolean targetMoved = lastTargetPosition == null || currentTargetPosition.distanceToSqr(lastTargetPosition) > TARGET_MOVE_DISTANCE_SQR;
            if (targetMoved && ticksUntilRepath <= 0) {
                updatePath();
                return;
            }
        }
        if (mob.getNavigation().isDone() && ticksUntilRepath <= 0) {
            updatePath();
        }
    }

    private void updatePath() {
        if (target != null) {
            destination = target.position();
        }
        if (destination == null) {
            return;
        }
        lastTargetPosition = destination;
        ticksUntilRepath = REPATH_DELAY;
        var navigation = mob.getNavigation();
        var path = navigation.createPath( destination.x, destination.y,destination.z,0);
        if (path != null) {
            navigation.moveTo(path, speed);
        }
    }

    @Override
    public void stop() {
        active = false;
        mob.getNavigation().stop();
    }
}
