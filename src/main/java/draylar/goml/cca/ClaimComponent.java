package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.Claim;

public interface ClaimComponent extends AutoSyncedComponent {
    RTreeMap<ClaimBox, Claim> getClaims();
    void add(ClaimBox box, Claim info);
    void remove(ClaimBox box);
}
