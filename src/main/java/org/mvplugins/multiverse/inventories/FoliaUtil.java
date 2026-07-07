package org.mvplugins.multiverse.inventories;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public final class FoliaUtil {
    private static final boolean FOLIA;
    static {
        boolean folia;
        try { Class.forName("io.papermc.paper.threadedregions.RegionizedServer"); folia = true; }
        catch (ClassNotFoundException ignored) { folia = false; }
        FOLIA = folia;
    }
    private FoliaUtil() {}
    public static boolean isFolia() { return FOLIA; }

    public static void runGlobalSync(Plugin plugin, Runnable task) {
        try { Object s = global();
            s.getClass().getMethod("run", Plugin.class, Consumer.class).invoke(s, plugin, (Consumer<Object>) t -> task.run());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void runGlobalDelayed(Plugin plugin, Runnable task, long delayTicks) {
        try { Object s = global();
            s.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, long.class).invoke(s, plugin, (Consumer<Object>) t -> task.run(), delayTicks);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static Object runGlobalTimer(Plugin plugin, Runnable task, long init, long period) {
        try { Object s = global();
            return s.getClass().getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class)
                    .invoke(s, plugin, (Consumer<Object>) t -> task.run(), init, period);
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public static void cancelTask(Object task) {
        if (task == null) return;
        try { task.getClass().getMethod("cancel").invoke(task); } catch (Exception ignored) {}
    }

    private static Object global() throws Exception {
        return Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler").invoke(Bukkit.getServer());
    }
}
