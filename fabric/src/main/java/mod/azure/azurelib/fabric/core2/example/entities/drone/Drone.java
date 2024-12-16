package mod.azure.azurelib.fabric.core2.example.entities.drone;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.core2.util.EDARef;
import mod.azure.azurelib.core2.util.EDARefSerializers;
import mod.azure.azurelib.fabric.core2.example.MoveAnalysis;
import mod.azure.azurelib.fabric.core2.example.entities.drone.util.CrawlPathNodeEvaluator;

public class Drone extends Monster {

    private static final EntityDataAccessor<Boolean> IS_CRAWLING = SynchedEntityData.defineId(
        Drone.class,
        EntityDataSerializers.BOOLEAN
    );

    private final DroneAnimationDispatcher animationDispatcher;

    private final MoveAnalysis moveAnalysis;

    public final EDARef<Boolean> isCrawlingRef;

    public Drone(EntityType<? extends Drone> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new DroneAnimationDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
        this.isCrawlingRef = EDARef.builder(this, IS_CRAWLING)
            .setSerializer("crawling", EDARefSerializers.BOOLEAN)
            .setSetterCallback($ -> refreshDimensions())
            .setSyncUpdateCallback(this::refreshDimensions)
            .build();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_CRAWLING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(Drone.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level) {

            @Override
            protected @NotNull PathFinder createPathFinder(int i) {
                this.nodeEvaluator = new CrawlPathNodeEvaluator();
                this.nodeEvaluator.setCanPassDoors(true);
                return new PathFinder(this.nodeEvaluator, i);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        moveAnalysis.update();

        if (level().isClientSide) {
            runPassiveAnimations();
        } else {
            tryToCrawl();
        }
    }

    private void tryToCrawl() {
        var path = navigation.getPath();

        var current = blockPosition();
        var isTight = isTightSpace(current);

        if (path != null && path.getNextNodeIndex() < path.getNodeCount()) {
            var previousNode = path.getPreviousNode();
            isTight = isTight || previousNode != null && isTightSpace(previousNode.asBlockPos());
            var nextNode = path.getNextNode();
            isTight = isTight || isTightSpace(nextNode.asBlockPos());
        }

        isCrawlingRef.set(isTight);
    }

    private boolean isTightSpace(BlockPos blockPos) {
        var above = blockPos.above();
        var aboveState = level().getBlockState(above);
        return !aboveState.isAir();
    }

    private void runPassiveAnimations() {
        var dispatcher = animationDispatcher;
        var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
        var isShort = getBbHeight() <= 1;
        Runnable animFunction;

        if (isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::clientSwim;
        } else if (isMovingOnGround) {
            animFunction = isShort ? dispatcher::clientCrawl : dispatcher::clientWalk;
        } else {
            // TODO: idle crawl
            animFunction = isShort ? dispatcher::clientCrawlHold : dispatcher::clientIdle;
        }

        animFunction.run();
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        var result = super.doHurtTarget(entity);

        if (result) {
            var isClawAttack = random.nextBoolean();

            if (isClawAttack) {
                animationDispatcher.serverClawAttack();
            } else {
                animationDispatcher.serverTailAttack();
            }
        }

        return result;
    }

    @Override
    public @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        var defaultDimensions = getType().getDimensions();
        return defaultDimensions.scale(1, isCrawlingRef.get() ? 0.4f : 1);
    }
}
