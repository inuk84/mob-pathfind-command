
package inuk.pathfindcmd;

import net.fabricmc.api.ModInitializer;

public class PathfindCmd implements ModInitializer {
    @Override
    public void onInitialize() {
        MobGoto.register();
    }
}
