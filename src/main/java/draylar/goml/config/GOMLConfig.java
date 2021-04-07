package draylar.goml.config;


import draylar.omegaconfig.api.Config;

import java.util.ArrayList;
import java.util.List;

public class GOMLConfig implements Config {

    public List<String> dimensionBlacklist = new ArrayList<>();
    public int makeshiftRadius = 10;
    public int reinforcedRadius = 25;
    public int glisteningRadius = 50;
    public int crystalRadius = 75;
    public int emeradicRadius = 125;
    public int witheredRadius = 200;

    @Override
    public String getName() {
        return "getoffmylawn";
    }

    @Override
    public String getExtension() {
        return "json5";
    }
}
