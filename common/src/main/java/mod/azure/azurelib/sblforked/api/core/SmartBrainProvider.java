/**
 * This class is a fork of the matching class found in the SmartBrainLib repository. Original source:
 * https://github.com/Tslat/SmartBrainLib Copyright © 2024 Tslat. Licensed under Mozilla Public License 2.0:
 * https://github.com/Tslat/SmartBrainLib/blob/1.21/LICENSE.
 */
package mod.azure.azurelib.sblforked.api.core;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import mod.azure.azurelib.sblforked.api.SmartBrainOwner;
import mod.azure.azurelib.sblforked.api.core.behaviour.GroupBehaviour;
import mod.azure.azurelib.sblforked.api.core.sensor.ExtendedSensor;

/**
 * The provider of {@link SmartBrain SmartBrains}. All entities intending to utilise this library should return a new
 * instance of this in {@link LivingEntity#brainProvider()} <br>
 * All entities that use this provider use SmartBrains.
 *
 * @param <E> The entity
 */
public class SmartBrainProvider<E extends LivingEntity & SmartBrainOwner<E>> extends Brain.Provider<E> {

    private static final Map<EntityType<? extends LivingEntity>, ImmutableList<MemoryModuleType<?>>> BRAIN_MEMORY_CACHE =
        new Object2ObjectOpenHashMap<>();

    private final E owner;

    private final boolean nonStaticMemories;

    /**
     * @param owner The owner of the brain
     */
    public SmartBrainProvider(E owner) {
        this(owner, false);
    }

    /**
     * @param owner             The owner of the brain
     * @param nonStaticMemories Whether the entity has different behaviours or sensors depending on the entity instance
     */
    public SmartBrainProvider(E owner, boolean nonStaticMemories) {
        super(List.of(), List.of());

        this.owner = owner;
        this.nonStaticMemories = nonStaticMemories;
    }

    @Override
    public final SmartBrain<E> makeBrain(Dynamic<?> codecLoader) {
        List<? extends ExtendedSensor<? extends E>> sensors = this.owner.getSensors();
        List<BrainActivityGroup<? extends E>> taskList = compileTasks();
        ImmutableList<MemoryModuleType<?>> memories;

        if (!this.nonStaticMemories && BRAIN_MEMORY_CACHE.containsKey(this.owner.getType())) {
            memories = BRAIN_MEMORY_CACHE.get(this.owner.getType());
        } else {
            memories = createMemoryList(taskList, sensors);

            if (!this.nonStaticMemories)
                BRAIN_MEMORY_CACHE.put((EntityType<? extends LivingEntity>) this.owner.getType(), memories);
        }

        SmartBrain<E> brain = new SmartBrain(memories, sensors, taskList);

        finaliseBrain(brain);

        return brain;
    }

    private ImmutableList<MemoryModuleType<?>> createMemoryList(
        List<BrainActivityGroup<? extends E>> taskList,
        List<? extends ExtendedSensor<?>> sensors
    ) {
        Set<MemoryModuleType<?>> memoryTypes = new ObjectOpenHashSet<>();

        taskList.forEach(
            activityGroup -> activityGroup.getBehaviours()
                .forEach(behavior -> collectMemoriesFromTask(memoryTypes, behavior))
        );
        sensors.forEach(sensor -> memoryTypes.addAll(sensor.memoriesUsed()));

        return ImmutableList.copyOf(memoryTypes);
    }

    private void collectMemoriesFromTask(Set<MemoryModuleType<?>> memories, BehaviorControl<?> behaviour) {
        if (behaviour instanceof GateBehavior<?> gateBehaviour) {
            gateBehaviour.behaviors.stream().forEach(subBehaviour -> collectMemoriesFromTask(memories, subBehaviour));
        } else if (behaviour instanceof GroupBehaviour<?> groupBehaviour) {
            groupBehaviour.getBehaviours()
                .forEachRemaining(subBehaviour -> collectMemoriesFromTask(memories, subBehaviour));
        } else if (behaviour instanceof Behavior<?> behaviour2) {
            memories.addAll(behaviour2.entryCondition.keySet());
        }
    }

    private List<BrainActivityGroup<? extends E>> compileTasks() {
        List<BrainActivityGroup<? extends E>> tasks = new ObjectArrayList<>();
        BrainActivityGroup<? extends E> activityGroup;

        if (!(activityGroup = owner.getCoreTasks()).getBehaviours().isEmpty())
            tasks.add(activityGroup);

        if (!(activityGroup = owner.getIdleTasks()).getBehaviours().isEmpty())
            tasks.add(activityGroup);

        if (!(activityGroup = owner.getFightTasks()).getBehaviours().isEmpty())
            tasks.add(activityGroup);

        tasks.addAll(owner.getAdditionalTasks().values());

        return tasks;
    }

    private void finaliseBrain(SmartBrain<E> brain) {
        brain.setCoreActivities(this.owner.getAlwaysRunningActivities());
        brain.setDefaultActivity(this.owner.getDefaultActivity());
        brain.useDefaultActivity();
        brain.setSchedule(this.owner.getSchedule());
        this.owner.handleAdditionalBrainSetup(brain);
    }

    /**
     * Use one of the startup 'getTasks' methods if adding at startup, or else use
     * {@link mod.azure.azurelib.sblforked.util.BrainUtils#addActivity(Brain, BrainActivityGroup)}
     */
    @Deprecated(forRemoval = true)
    protected void addActivity(SmartBrain<E> brain, Activity activity, BrainActivityGroup<E> activityGroup) {
        brain.activityRequirements.put(activity, activityGroup.getActivityStartMemoryConditions());

        if (!activityGroup.getWipedMemoriesOnFinish().isEmpty())
            brain.activityMemoriesToEraseWhenStopped.put(activity, activityGroup.getWipedMemoriesOnFinish());

        for (Pair<Integer, ? extends Behavior<? super E>> pair : activityGroup.pairBehaviourPriorities()) {
            brain.addBehaviour(pair.getFirst(), activity, pair.getSecond());
        }
    }
}
