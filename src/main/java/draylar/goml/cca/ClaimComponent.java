package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import nerdhub.cardinal.components.api.util.sync.WorldSyncedComponent;
import draylar.goml.api.ClaimInfo;

public interface ClaimComponent extends WorldSyncedComponent {
    RTreeMap<Box, ClaimInfo> getClaims();
    void add(Box box, ClaimInfo info);
    void remove(Box box);
}
