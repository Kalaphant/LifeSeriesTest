package net.mat0u5.lifeseries.series;

import java.util.List;

public enum SeriesList {
    UNASSIGNED,

    THIRD_LIFE,
    LAST_LIFE,
    DOUBLE_LIFE,
    LIMITED_LIFE,
    SECRET_LIFE,
    WILD_LIFE;

    public static String getStringNameFromSeries(SeriesList series) {
        if (series == THIRD_LIFE) return "thirdlife";
        if (series == LAST_LIFE) return "lastlife";
        if (series == DOUBLE_LIFE) return "doublelife";
        if (series == LIMITED_LIFE) return "limitedlife";
        if (series == SECRET_LIFE) return "secretlife";
        if (series == WILD_LIFE) return "wildlife";
        return "unassigned";
    }

    public static List<SeriesList> getAll() {
        return List.of(THIRD_LIFE,LAST_LIFE,DOUBLE_LIFE,LIMITED_LIFE,SECRET_LIFE,WILD_LIFE);
    }

    public static List<SeriesList> getAllImplemented() {
        return List.of(THIRD_LIFE,LAST_LIFE,DOUBLE_LIFE,LIMITED_LIFE,SECRET_LIFE);
    }

    public static List<String> getImplementedSeriesNames() {
        return List.of("thirdlife", "lastlife", "doublelife", "limitedlife", "secretlife");
    }

    public static String getDatapackName(SeriesList series) {
        if (series == THIRD_LIFE) return "Third Life Recipe Datapack.zip";
        if (series == LAST_LIFE) return "Last Life Recipe Datapack.zip";
        if (series == DOUBLE_LIFE) return "Double Life Recipe Datapack.zip";
        if (series == LIMITED_LIFE) return "Limited Life Recipe Datapack.zip";
        if (series == SECRET_LIFE) return "Secret Life Recipe Datapack.zip";
        if (series == WILD_LIFE) return "";
        return "";
    }
}
