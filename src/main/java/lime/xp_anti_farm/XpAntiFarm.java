package lime.xp_anti_farm;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod(modid = XpAntiFarm.MODID, name = XpAntiFarm.NAME, version = XpAntiFarm.VERSION)
public class XpAntiFarm
{
    public static final String MODID = "xp_anti_farm";
    public static final String NAME = "XP Anti-farm";
    public static final String VERSION = "1.0";
    public static Logger logger;

    public HashMap<ChunkPos, Integer> chunkKillsMap = new HashMap<ChunkPos, Integer>();

    @Mod.Instance(MODID)
    public static XpAntiFarm instance;

    static Configuration config;
    static int xpDecreaseEveryThisManyKills = 50;
    static int xpDecreaseAmount = 1;
    static int minXpDropped = 1;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());
        try {
            config.load();
            xpDecreaseEveryThisManyKills = config.getInt("xp decreases every *this* many kills", Configuration.CATEGORY_GENERAL, xpDecreaseEveryThisManyKills,1, Integer.MAX_VALUE,"XP dropped from killing monsters in chunk decreases every *this* many kills (milestone)");
            xpDecreaseAmount = config.getInt("xp decrease amount", Configuration.CATEGORY_GENERAL, xpDecreaseAmount,1, Integer.MAX_VALUE,"XP decrease amount per milestone");
            minXpDropped = config.getInt("minimum xp dropped", Configuration.CATEGORY_GENERAL, minXpDropped,0, Integer.MAX_VALUE,"Dropped XP amount won't be less than this. If set to 1, farming will still be possible, though very, very inefficient");
        } catch (Exception e1) {
            logger.log(Level.ERROR, "Problem loading "+XpAntiFarm.MODID+" config file!", e1);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandlers());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (config.hasChanged()) {
            config.save();
        }
    }
}
