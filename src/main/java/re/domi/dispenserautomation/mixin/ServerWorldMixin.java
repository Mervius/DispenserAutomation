package re.domi.dispenserautomation.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import re.domi.dispenserautomation.DispenserTicker;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements DispenserTicker
{
    @Unique
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Override
    public void dispAuto_add(Task task)
    {
        this.tasks.add(task);
    }

    @Override
    public void dispAuto_tick() {
        for (Task task : tasks) {

            // stop ticking if this chunk is unloading
            // I don't know if I should also be checking the position the dispenser interacts with as wel... we'll have to see if this causes issues.
            if (!((ServerWorld) (Object) this).isChunkLoaded(task.dispenserPos))
                continue;

            if (task.tick(this)) {
                tasks.remove(task); // safe in CopyOnWriteArrayList
            }
        }
    }

    protected ServerWorldMixin()
    {
        //noinspection ConstantConditions
        super(null, null, null, null, false, false, 0, 0);
    }
}
