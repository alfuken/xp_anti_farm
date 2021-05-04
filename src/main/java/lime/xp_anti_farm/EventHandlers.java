package lime.xp_anti_farm;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid= XpAntiFarm.MODID)
public class EventHandlers {

    @SubscribeEvent
    public void onChunkDataEventLoad(ChunkDataEvent.Load event){
        if (event.getWorld().isRemote) return;

        NBTTagCompound data = event.getData();
        String dataKey = XpAntiFarm.MODID + ":chunkKillsCount";
        if (data.hasKey(dataKey)){
            XpAntiFarm.instance.chunkKillsMap.put(event.getChunk().getPos(), data.getInteger(dataKey));
            XpAntiFarm.logger.debug("Loaded kill count for "+event.getChunk().getPos().toString() + ": "+data.getInteger(dataKey));
        }
    }

    @SubscribeEvent
    public void onChunkDataEventSave(ChunkDataEvent.Save event){
        if (event.getWorld().isRemote) return;

        int currentKillsCount = XpAntiFarm.instance.chunkKillsMap.getOrDefault(event.getChunk().getPos(), 0);
        if (currentKillsCount > 0){
            NBTTagCompound data = event.getData();
            String dataKey = XpAntiFarm.MODID + ":chunkKillsCount";
            data.setInteger(dataKey, currentKillsCount);
            XpAntiFarm.logger.debug("Saved kill count for "+event.getChunk().getPos().toString() + ": "+currentKillsCount);
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingExperienceDropEvent event){
        if (event.getEntityLiving().getEntityWorld().isRemote) return;
        if (event.getOriginalExperience() == 0) return;

        World w = event.getEntityLiving().getEntityWorld();
        Chunk c = w.getChunkFromBlockCoords(event.getEntityLiving().getPosition());
        int currentKillsCount = XpAntiFarm.instance.chunkKillsMap.getOrDefault(c.getPos(), 0) + 1;
        XpAntiFarm.instance.chunkKillsMap.put(c.getPos(), currentKillsCount);
        int currentStep = currentKillsCount / XpAntiFarm.xpDecreaseEveryThisManyKills;
        int xpDecrease = XpAntiFarm.xpDecreaseAmount * currentStep;
        int newXP = Math.max(event.getOriginalExperience() - xpDecrease, XpAntiFarm.minXpDropped);

        event.setDroppedExperience(newXP);

        String str = "Kills in chunk "+c.getPos().toString()+": "+currentKillsCount+"; Decreased XP drop from "+event.getOriginalExperience()+" to "+newXP;
        XpAntiFarm.logger.debug(str);
    }
}
