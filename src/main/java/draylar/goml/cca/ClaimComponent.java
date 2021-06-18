package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;

public interface ClaimComponent extends ComponentV3, AutoSyncedComponent {
    RTreeMap<ClaimBox, Claim> getClaims();
    void add(ClaimBox box, Claim info);
    void remove(ClaimBox box);
}
