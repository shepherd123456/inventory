package cz.technico.inventory.constructionwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConstructionWorkServiceImpl implements ConstructionWorkService{
    @Autowired
    private ConstructionWorkRepository constructionWorkRepository;

    @Override
    public ConstructionWork save(ConstructionWork constructionWork) {
        return constructionWorkRepository.save(constructionWork);
    }

    @Override
    public void saveAll(List<ConstructionWork> works) {
        constructionWorkRepository.saveAll(works);
    }
    @Override
    public ConstructionWork getBySpidAndName(UUID spid, String name) {
        return constructionWorkRepository.getBySpidAndName(spid, name);
    }
}
