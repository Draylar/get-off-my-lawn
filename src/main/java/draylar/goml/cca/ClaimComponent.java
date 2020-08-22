package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimInfo;
import nerdhub.cardinal.components.api.util.sync.WorldSyncedComponent;

public interface ClaimComponent extends WorldSyncedComponent {
    RTreeMap<ClaimBox, ClaimInfo> getClaims();
    void add(ClaimBox box, ClaimInfo info);
    void remove(ClaimBox box);
}
