package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.Claim;
import nerdhub.cardinal.components.api.util.sync.WorldSyncedComponent;

public interface ClaimComponent extends WorldSyncedComponent {
    RTreeMap<ClaimBox, Claim> getClaims();
    void add(ClaimBox box, Claim info);
    void remove(ClaimBox box);
}
