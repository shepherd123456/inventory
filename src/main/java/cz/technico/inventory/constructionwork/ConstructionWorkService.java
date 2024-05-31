package cz.technico.inventory.constructionwork;

import java.util.List;
import java.util.UUID;

public interface ConstructionWorkService {
    ConstructionWork save(ConstructionWork constructionWork);
    void saveAll(List<ConstructionWork> works);
    ConstructionWork getBySpidAndName(UUID uuid, String name);
}
