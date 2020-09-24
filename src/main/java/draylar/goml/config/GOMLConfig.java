package draylar.goml.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@Config(name = "getoffmylawn")
public class GOMLConfig implements ConfigData {

    public List<String> dimensionBlacklist = new ArrayList<>();
}
